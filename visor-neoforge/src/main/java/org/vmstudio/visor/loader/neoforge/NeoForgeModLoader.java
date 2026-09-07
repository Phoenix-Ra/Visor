package org.vmstudio.visor.loader.neoforge;


import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import io.netty.buffer.Unpooled;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.event.EventNetworkChannel;
import org.vmstudio.visor.api.ModLoader;
import org.vmstudio.visor.api.VisorAPI;
import org.vmstudio.visor.api.client.render.RenderPipelineCallback;
import org.vmstudio.visor.api.client.render.RenderPipelineStage;
import org.vmstudio.visor.api.common.VRException;
import org.vmstudio.visor.api.common.network.VisorChannel;
import org.vmstudio.visor.api.common.network.VisorPayloadToClient;
import org.vmstudio.visor.api.common.network.VisorPayloadToServer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.loading.moddiscovery.ModFileInfo;
import net.neoforged.neoforgespi.language.IModFileInfo;
import net.neoforged.neoforgespi.language.ModFileScanData;
import net.neoforged.neoforge.network.INetworkDirection;
import net.neoforged.neoforge.network.PlayNetworkDirection;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;


public class NeoForgeModLoader implements ModLoader {
    private File configFolder = FMLPaths.CONFIGDIR.get().toFile();

    private final Map<RenderPipelineStage, List<RenderPipelineCallback>> pipelineCallbacks
            = new EnumMap<>(RenderPipelineStage.class);

    private boolean levelStageListenerRegistered = false;


    @Override
    public File getConfigFolder() {
        return configFolder;
    }

    @Override
    public boolean isModLoaded(@NotNull String id) {
        return FMLLoader.getLoadingModList().getModFileById(id) != null;
    }

    @Override
    public @NotNull String getModVersion(@NotNull String id) {
        if (isModLoaded(VisorAPI.MOD_ID)) {
            return FMLLoader.getLoadingModList()
                    .getModFileById(id).versionString();
        }
        return "no version";
    }

    @Override
    public boolean isDedicatedServer() {
        return FMLEnvironment.dist == Dist.DEDICATED_SERVER;
    }


    @Override
    public void addToRenderPipeline(@NotNull RenderPipelineStage stage,
                                    @NotNull RenderPipelineCallback callback) {
        pipelineCallbacks
                .computeIfAbsent(stage, k -> new CopyOnWriteArrayList<>())
                .add(callback);

        if (!levelStageListenerRegistered) {
            NeoForge.EVENT_BUS.addListener(this::onRenderLevelStage);
            levelStageListenerRegistered = true;
        }
    }



    @Override
    public boolean enableRenderTargetStencil(@NotNull RenderTarget renderTarget) {
        renderTarget.enableStencil();
        return true;
    }

    @Override
    public double getItemEntityReach(double baseRange, ItemStack itemStack, EquipmentSlot slot) {
        Collection<AttributeModifier> attributes = itemStack.getAttributeModifiers(slot)
                // NeoForge exposes this as a Holder, Forge as a RegistryObject
                .get(NeoForgeMod.ENTITY_REACH.value());
        for (AttributeModifier entry : attributes) {
            if (entry.getOperation() == AttributeModifier.Operation.ADDITION) {
                baseRange += entry.getAmount();
            }
        }
        double totalRange = baseRange;
        for (AttributeModifier entry : attributes) {
            if (entry.getOperation() == AttributeModifier.Operation.MULTIPLY_BASE) {
                totalRange += baseRange * entry.getAmount();
            }
        }
        for (AttributeModifier entry : attributes) {
            if (entry.getOperation() == AttributeModifier.Operation.MULTIPLY_TOTAL) {
                totalRange *= 1.0 + entry.getAmount();
            }
        }
        return totalRange;
    }

    @Override
    public @NotNull List<Class<?>> getClassesAnnotated(@NotNull Class<? extends Annotation> annotation,
                                                       @NotNull String modId,
                                                       @NotNull String packagePath) {
        List<Class<?>> result = new ArrayList<>();
        IModFileInfo info = ModList.get().getModFileById(modId);
        if (!(info instanceof ModFileInfo modFileInfo)) {
            return result;
        }

        ModFileScanData scanData = modFileInfo.getFile().getScanResult();
        String annotationName = annotation.getName();

        for (var annotationData : scanData.getAnnotations()) {
            String className = annotationData.clazz().getClassName();

            if (!className.startsWith(packagePath)) {
                continue;
            }
            if (!annotationData.annotationType()
                    .getClassName().equals(annotationName)) {
                continue;
            }

            try {
                Class<?> cls = Class.forName(className, false,
                        Thread.currentThread().getContextClassLoader());
                result.add(cls);
            } catch (ClassNotFoundException e) {
                throw new VRException(e);
            }
        }

        return result;

    }

    @Override
    public void registerNetworkChannel(@NotNull VisorChannel channel) {
        String version = String.valueOf(channel.getNetworkVersion());
        EventNetworkChannel eventChannel = NetworkRegistry.ChannelBuilder
                .named(channel.getChannelId())
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .networkProtocolVersion(() -> version)
                .eventNetworkChannel();

        eventChannel.addListener(event -> {
            FriendlyByteBuf payload = event.getPayload();
            if (payload == null) return;

            FriendlyByteBuf copy = new FriendlyByteBuf(Unpooled.buffer());
            copy.writeBytes(payload.copy());

            var context = event.getSource();
            if (context.getDirection().getOriginationSide().isClient()) {
                if (channel.hasPacketsToServer() && context.getSender() != null) {
                    var sender = context.getSender();
                    context.enqueueWork(() -> channel.handleToServer(copy, sender,
                            p -> context.getNetworkManager().send(
                                    ModLoader.get().createPacketToClient(channel.getChannelId(), p)
                            )));
                }
            } else {
                if (channel.hasPacketsToClient()) {
                    context.enqueueWork(() -> channel.handleToClient(copy));
                }
            }
            context.setPacketHandled(true);
        });
    }

    @Override
    public @NotNull Packet<?> createPacketToClient(@NotNull ResourceLocation channelId,
                                                   @NotNull VisorPayloadToClient payload) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        payload.write(buffer);
        return PlayNetworkDirection.PLAY_TO_CLIENT.buildPacket(
                new INetworkDirection.PacketData(buffer, 0), channelId);
    }

    @Override
    public @NotNull Packet<?> createPacketToServer(@NotNull ResourceLocation channelId,
                                                   @NotNull VisorPayloadToServer payload) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        payload.write(buffer);
        return PlayNetworkDirection.PLAY_TO_SERVER.buildPacket(
                new INetworkDirection.PacketData(buffer, 0), channelId);
    }

    @Override
    public boolean renderWaterOverlay(Player player, PoseStack mat) {
        return ClientHooks.renderWaterOverlay(player, mat);
    }
    @Override
    public boolean renderFireOverlay(Player player, PoseStack mat) {
        return ClientHooks.renderFireOverlay(player, mat);
    }

    @Override
    public @NotNull LoaderType getType() {
        return LoaderType.NEOFORGE;
    }


    // ----- INNER -----

    private void onRenderLevelStage(RenderLevelStageEvent event) {
        RenderPipelineStage stage = mapForgeStage(event.getStage());
        if (stage == null) return;

        List<RenderPipelineCallback> callbacks = pipelineCallbacks.get(stage);
        if (callbacks == null || callbacks.isEmpty()) return;

        PoseStack poseStack = event.getPoseStack();
        float partialTicks = event.getPartialTick();

        for (RenderPipelineCallback callback : callbacks) {
            callback.render(poseStack, partialTicks);
        }
    }


    private static RenderPipelineStage mapForgeStage(RenderLevelStageEvent.Stage forgeStage) {
         if (forgeStage == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            return RenderPipelineStage.AFTER_SOLID;
        }
        if (forgeStage == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return RenderPipelineStage.AFTER_TRANSLUCENT;
        }
        if (forgeStage == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            return RenderPipelineStage.AFTER_WORLD;
        }
        return null;
    }
}