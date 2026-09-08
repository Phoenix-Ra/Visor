package org.vmstudio.visor.loader.neoforge.mixin;

import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
//? if <1.21 {
/*import org.vmstudio.visor.core.client.render.VRRenderState;
import net.minecraft.client.Camera;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
*///?}


@Mixin(GameRenderer.class)
public class NeoForgeGameRendererMixin {

    //? if <1.21 {
    /*@Redirect(at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/Camera;setAnglesInternal(FF)V", remap = false),
            method = "renderLevel")
    public void visor$keepVRAnglesInEyes(Camera camera, float yaw, float pitch) {
        if (VRRenderState.getPhase().isVanilla()
                || !VRRenderState.getRenderPass().isEye()) {
            camera.setAnglesInternal(yaw, pitch);
        }
    }

    @Redirect(at = @At(value = "INVOKE",
            target = "Lnet/neoforged/neoforge/client/event/ViewportEvent$ComputeCameraAngles;getRoll()F",
            remap = false),
            method = "renderLevel")
    public float visor$dropEventRollInEyes(ViewportEvent.ComputeCameraAngles event) {
        if (VRRenderState.getPhase().isVanilla()
                || !VRRenderState.getRenderPass().isEye()) {
            return event.getRoll();
        }
        return 0F;
    }
    *///?}
}
