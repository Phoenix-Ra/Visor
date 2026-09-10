package org.vmstudio.visor.mixin.client.renderer;

import org.vmstudio.visor.api.client.render.VRRenderPass;
import org.vmstudio.visor.core.client.VisorState;
import org.vmstudio.visor.core.client.render.VRRenderState;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class VRLightsMixins {
    @Mixin(ClientLevel.class)
    public static class ClientLevelMixin {


        /**
         * Only process this when rendering vanilla
         * or VR camera that is a worldUpdater
         */
        @Inject(at = @At("HEAD"), method = "pollLightUpdates", cancellable = true)
        public void visor$noUpdateOncePerFrame(CallbackInfo info){
            if(VisorState.get().isNotActive() || VRRenderState.getPhase().isNotVRWorld()) return;
            if (VRRenderState.getRenderPass() != VRRenderPass.worldUpdater()) {
                info.cancel();
            }
        }
    }

}
