package org.vmstudio.visor.compatibility.aeronautics.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
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

@Mixin(targets = "dev.simulated_team.simulated.content.physics_staff.PhysicsStaffItemRenderer", remap = false)
@MixinGate(classes = "dev.simulated_team.simulated.content.physics_staff.PhysicsStaffItemRenderer")
@Pseudo
public class PhysicsStaffItemRendererMixin {
    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;getEyePosition(F)Lnet/minecraft/world/phys/Vec3;"
            )
    )
    private Vec3 redirectRenderEyePos(Player player, float partialTicks) {
        VisorPlayer visorPlayer = VisorAPI.getVisorPlayer(player);
        if (visorPlayer != null && visorPlayer.isVR()) {
            VRPlayer vrPlayer = visorPlayer.asVR();
            VRPlayerPose playerPose = vrPlayer.getPoseData();

            VRPose handPose = playerPose.getHand(vrPlayer.getActiveHand());
            return handPose.getPositionVec3();
        }

        return player.getEyePosition(partialTicks);
    }
}
