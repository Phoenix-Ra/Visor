package org.vmstudio.visor.api.compatibility.mcversion.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.model.geom.ModelPart;
//? if >=1.21 {
import net.minecraft.client.gui.GuiGraphics;
//?}
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.vmstudio.visor.api.compatibility.mcversion.McVersionUtils;

import java.util.function.Supplier;

/**
 * Cross-mc-version Utils for rendering methods
 */
public class McRenderUtils {
    private McRenderUtils() {
        throw new UnsupportedOperationException("This is an utility class and cannot be instantiated");
    }

    // ------- PLAYER SKIN -------

    public static ResourceLocation getSkinTexture(AbstractClientPlayer player) {
        //? if >=1.20.2 {
        return player.getSkin().texture();
        //?} else {
        /*return player.getSkinTextureLocation();
        *///?}
    }

    public static String getModelName(AbstractClientPlayer player) {
        //? if >=1.20.2 {
        return player.getSkin().model().id();
        //?} else {
        /*return player.getModelName();
        *///?}
    }

    // ------- CROSSHAIR -------

    public static ResourceLocation crosshairTexture() {
        //? if >=1.20.2 {
        return McVersionUtils.newResourceLoc("minecraft", "textures/gui/sprites/hud/crosshair.png");
        //?} else {
        /*return Gui.GUI_ICONS_LOCATION;
        *///?}
    }

    public static float crosshairUvSize() {
        //? if >=1.20.2 {
        return 1f;
        //?} else {
        /*return 15f / 256f;
        *///?}
    }

    // ------- SHADERS -------

    public static Supplier<ShaderInstance> positionTexColorNormalShader() {
        //? if >=1.20.5 {
        return GameRenderer::getRendertypeCloudsShader;
        //?} else {
        /*return GameRenderer::getPositionTexColorNormalShader;
        *///?}
    }

    // ------- TIMING -------

    public static float deltaFrameTicks() {
        //? if >=1.21 {
        return Minecraft.getInstance().getTimer().getGameTimeDeltaTicks();
        //?} else {
        /*return Minecraft.getInstance().getDeltaFrameTime();
        *///?}
    }

    public static float partialTick() {
        //? if >=1.21 {
        return Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        //?} else {
        /*return Minecraft.getInstance().getFrameTime();
        *///?}
    }

    // ------- MODEL PARTS -------

    public static void renderModelPart(ModelPart part,
                                       PoseStack poseStack,
                                       VertexConsumer consumer,
                                       int packedLight,
                                       int packedOverlay) {
        //? if >=1.21 {
        part.render(poseStack, consumer, packedLight, packedOverlay, 0xFFFFFFFF);
        //?} else {
        /*part.render(poseStack, consumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        *///?}
    }

    // ------- GAME RENDERER -------

    public static void renderGame(GameRenderer renderer,
                                  float partialTicks,
                                  long nanoTime,
                                  boolean renderLevel) {
        //? if >=1.21 {
        renderer.render(Minecraft.getInstance().getTimer(), renderLevel);
        //?} else {
        /*renderer.render(partialTicks, nanoTime, renderLevel);
        *///?}
    }

    public static void renderItemActivationAnimation(GameRenderer renderer, float partialTicks) {
        //? if >=1.21 {
        Minecraft minecraft = Minecraft.getInstance();
        renderer.renderItemActivationAnimation(
                new GuiGraphics(minecraft, minecraft.renderBuffers().bufferSource()),
                partialTicks
        );
        //?} else {
        /*renderer.renderItemActivationAnimation(0, 0, partialTicks);
        *///?}
    }

    // ------- MATRICES -------

    // 1.20.5 renamed PoseStack.mulPoseMatrix to mulPose
    public static void mulPose(PoseStack poseStack, Matrix4f matrix) {
        //? if >=1.20.5 {
        poseStack.mulPose(matrix);
        //?} else {
        /*poseStack.mulPoseMatrix(matrix);
        *///?}
    }
}
