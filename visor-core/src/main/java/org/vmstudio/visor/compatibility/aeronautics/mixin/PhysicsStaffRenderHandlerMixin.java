package org.vmstudio.visor.compatibility.aeronautics.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
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

@Mixin(targets = "dev.simulated_team.simulated.content.physics_staff.PhysicsStaffRenderHandler", remap = false)
@MixinGate(classes = "dev.simulated_team.simulated.content.physics_staff.PhysicsStaffRenderHandler")
@Pseudo
public class PhysicsStaffRenderHandlerMixin {
    @Redirect(
            method = "updateHoverPos",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;pick(DFZ)Lnet/minecraft/world/phys/HitResult;"
            )
    )
    private static HitResult redirectHoverPick(LocalPlayer player, double range, float tickDelta, boolean includeFluids) {
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
}
