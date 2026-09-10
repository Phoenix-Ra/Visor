package org.vmstudio.visor.compatibility.aeronautics.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.vmstudio.visor.api.VisorAPI;
import org.vmstudio.visor.api.common.player.VRPlayer;
import org.vmstudio.visor.api.common.player.VRPlayerPose;
import org.vmstudio.visor.api.common.player.VRPose;
import org.vmstudio.visor.api.common.player.VisorPlayer;
import org.vmstudio.visor.compatibility.MixinGate;

@Mixin(targets = "dev.simulated_team.simulated.content.physics_staff.PhysicsStaffClientHandler", remap = false)
@MixinGate(classes = "dev.simulated_team.simulated.content.physics_staff.PhysicsStaffClientHandler")
@Pseudo
public class PhysicsStaffClientHandlerMixin {
    @Inject(method = "getStaffFocusPos", at = @At("HEAD"), cancellable = true)
    private static void getStaffFocusPos(final Player player, final boolean mainHand, final float partialTicks, CallbackInfoReturnable<Vec3> cir) {
        VisorPlayer visorPlayer = VisorAPI.getVisorPlayer(player);
        if (visorPlayer != null && visorPlayer.isVR()) {
            VRPlayer vrPlayer = visorPlayer.asVR();
            VRPlayerPose playerPose = vrPlayer.getPoseData();

            VRPose handPose = mainHand ? playerPose.getMainHand() : playerPose.getOffhand();

            Matrix4fc rotation = handPose.getRotation();
            Vector3f offset = new Vector3f(0, 0.75f, -0.35f);
            rotation.transformDirection(offset);

            cir.setReturnValue(handPose.getPositionVec3().add(
                    new Vec3(offset.x, offset.y, offset.z)
            ));
        }
    }

    @Redirect(
            method = "onItemUsed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;pick(DFZ)Lnet/minecraft/world/phys/HitResult;"
            )
    )
    private HitResult redirectPick(LocalPlayer player, double range, float tickDelta, boolean includeFluids) {
        VisorPlayer visorPlayer = VisorAPI.getVisorPlayer(player);
        if (visorPlayer != null && visorPlayer.isVR()) {
            VRPlayer vrPlayer = visorPlayer.asVR();
            VRPlayerPose playerPose = vrPlayer.getPoseData();

            VRPose handPose = playerPose.getHand(vrPlayer.getActiveHand());

            Vec3 startPos = handPose.getPositionVec3();
            Vec3 endPos = startPos.add(handPose.getDirectionVec3().scale(range));

            return player.level().clip(new ClipContext(startPos, endPos, ClipContext.Block.OUTLINE, includeFluids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, player));
        }

        return player.pick(range, tickDelta, includeFluids);
    }

    @Redirect(
            method = "sendDraggingData",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;getLookAngle()Lnet/minecraft/world/phys/Vec3;"
            )
    )
    private Vec3 redirectLookAngle(Player player) {
        VisorPlayer visorPlayer = VisorAPI.getVisorPlayer(player);
        if (visorPlayer != null && visorPlayer.isVR()) {
            VRPlayer vrPlayer = visorPlayer.asVR();
            VRPlayerPose playerPose = vrPlayer.getPoseData();

            VRPose handPose = playerPose.getHand(vrPlayer.getActiveHand());
            return handPose.getDirectionVec3();
        }

        return player.getLookAngle();
    }

    @Redirect(
            method = "startDraggingSubLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getEyePosition()Lnet/minecraft/world/phys/Vec3;"
            )
    )
    private Vec3 redirectStartDraggingEyePosition(LocalPlayer player) {
        VisorPlayer visorPlayer = VisorAPI.getVisorPlayer(player);
        if (visorPlayer != null && visorPlayer.isVR()) {
            VRPlayer vrPlayer = visorPlayer.asVR();
            VRPlayerPose playerPose = vrPlayer.getPoseData();

            VRPose handPose = playerPose.getHand(vrPlayer.getActiveHand());
            return handPose.getPositionVec3();
        }

        return player.getEyePosition();
    }
}
