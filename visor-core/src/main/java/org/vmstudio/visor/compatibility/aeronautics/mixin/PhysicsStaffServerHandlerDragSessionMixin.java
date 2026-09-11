package org.vmstudio.visor.compatibility.aeronautics.mixin;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.vmstudio.visor.api.VisorAPI;
import org.vmstudio.visor.api.common.player.VRPlayer;
import org.vmstudio.visor.api.common.player.VRPlayerPose;
import org.vmstudio.visor.api.common.player.VRPose;
import org.vmstudio.visor.api.common.player.VisorPlayer;
import org.vmstudio.visor.compatibility.MixinGate;

import java.util.Objects;

@Mixin(targets = "dev.simulated_team.simulated.content.physics_staff.PhysicsStaffServerHandler$DragSession", remap = false)
@MixinGate(classes = "dev.simulated_team.simulated.content.physics_staff.PhysicsStaffServerHandler$DragSession")
@Pseudo
public class PhysicsStaffServerHandlerDragSessionMixin {
    @Redirect(
            method = "physicsTick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/entity/player/Player;xOld:D"
            )
    )
    private double redirectXOld(Player player) {
        VisorPlayer visorPlayer = VisorAPI.getVisorPlayer(player);
        if (visorPlayer != null && visorPlayer.isVR()) {
            VRPlayer vrPlayer = visorPlayer.asVR();

            VRPlayerPose playerPose = vrPlayer.getPoseHistoryTick().getEntry(1);
            VRPose handPose;
            handPose = Objects.requireNonNullElseGet(playerPose, vrPlayer::getPoseData).getHand(vrPlayer.getActiveHand());

            return handPose.getPositionVec3().x();
        }

        return player.xOld;
    }

    @Redirect(
            method = "physicsTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;getX()D"
            )
    )
    private double redirectGetX(Player player) {
        VisorPlayer visorPlayer = VisorAPI.getVisorPlayer(player);
        if (visorPlayer != null && visorPlayer.isVR()) {
            VRPlayer vrPlayer = visorPlayer.asVR();
            VRPlayerPose playerPose = vrPlayer.getPoseData();

            VRPose handPose = playerPose.getHand(vrPlayer.getActiveHand());
            return handPose.getPositionVec3().x();
        }

        return player.getX();
    }

    @Redirect(
            method = "physicsTick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/entity/player/Player;yOld:D"
            )
    )
    private double redirectYOld(Player player) {
        VisorPlayer visorPlayer = VisorAPI.getVisorPlayer(player);
        if (visorPlayer != null && visorPlayer.isVR()) {
            VRPlayer vrPlayer = visorPlayer.asVR();

            VRPlayerPose playerPose = vrPlayer.getPoseHistoryTick().getEntry(1);
            VRPose handPose;
            handPose = Objects.requireNonNullElseGet(playerPose, vrPlayer::getPoseData).getHand(vrPlayer.getActiveHand());

            return handPose.getPositionVec3().y();
        }

        return player.yOld;
    }

    @Redirect(
            method = "physicsTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;getY()D"
            )
    )
    private double redirectGetY(Player player) {
        VisorPlayer visorPlayer = VisorAPI.getVisorPlayer(player);
        if (visorPlayer != null && visorPlayer.isVR()) {
            VRPlayer vrPlayer = visorPlayer.asVR();
            VRPlayerPose playerPose = vrPlayer.getPoseData();

            VRPose handPose = playerPose.getHand(vrPlayer.getActiveHand());
            return handPose.getPositionVec3().y();
        }

        return player.getY();
    }

    @Redirect(
            method = "physicsTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;getEyeHeight()F"
            )
    )
    private float redirectGetEyeHeight(Player player) {
        VisorPlayer visorPlayer = VisorAPI.getVisorPlayer(player);
        if (visorPlayer != null && visorPlayer.isVR()) {
            return 0.0f;
        }

        return player.getEyeHeight();
    }

    @Redirect(
            method = "physicsTick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/entity/player/Player;zOld:D"
            )
    )
    private double redirectZOld(Player player) {
        VisorPlayer visorPlayer = VisorAPI.getVisorPlayer(player);
        if (visorPlayer != null && visorPlayer.isVR()) {
            VRPlayer vrPlayer = visorPlayer.asVR();

            VRPlayerPose playerPose = vrPlayer.getPoseHistoryTick().getEntry(1);
            VRPose handPose;
            handPose = Objects.requireNonNullElseGet(playerPose, vrPlayer::getPoseData).getHand(vrPlayer.getActiveHand());

            return handPose.getPositionVec3().z();
        }

        return player.zOld;
    }

    @Redirect(
            method = "physicsTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;getZ()D"
            )
    )
    private double redirectGetZ(Player player) {
        VisorPlayer visorPlayer = VisorAPI.getVisorPlayer(player);
        if (visorPlayer != null && visorPlayer.isVR()) {
            VRPlayer vrPlayer = visorPlayer.asVR();
            VRPlayerPose playerPose = vrPlayer.getPoseData();

            VRPose handPose = playerPose.getHand(vrPlayer.getActiveHand());
            return handPose.getPositionVec3().z();
        }

        return player.getZ();
    }
}
