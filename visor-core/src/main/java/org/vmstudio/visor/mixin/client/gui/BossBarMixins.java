package org.vmstudio.visor.mixin.client.gui;

import org.vmstudio.visor.api.client.ClientFeature;
import org.vmstudio.visor.core.client.ClientContext;
import org.vmstudio.visor.core.client.VisorState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
//? if >=1.20.5 {
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//?} else {
/*import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.injection.Redirect;
*///?}

public class BossBarMixins {

    /**
     * Hides the vanilla boss bar
     */
    //? if >=1.20.5 {
    @Mixin(BossHealthOverlay.class)
    public static class BossBarHideMixin {

        @Final
        @Shadow
        private Minecraft minecraft;

        @Inject(at = @At("HEAD"), method = "render", cancellable = true)
        public void visor$noVanillaGuiBossHealth(GuiGraphics guiGraphics, CallbackInfo ci) {
            if (VisorState.get().isNotActive() || (minecraft.screen == null
                    && ClientContext.visor.isFeatureDisabled(ClientFeature.GUI_DISABLE_HUD))) {
                return;
            }
            ci.cancel();
        }
    }
    //?} else {
    /*@Mixin(Gui.class)
    public static class BossBarHideMixin {

        @Final
        @Shadow
        private Minecraft minecraft;

        @Redirect(at = @At(value = "INVOKE",
                target = "Lnet/minecraft/client/gui/components/BossHealthOverlay;render(Lnet/minecraft/client/gui/GuiGraphics;)V"),
                method = "render")
        public void visor$noVanillaGuiBossHealth(BossHealthOverlay instance,
                                                 GuiGraphics guiGraphics) {
            if(VisorState.get().isNotActive() || (minecraft.screen == null
                    && ClientContext.visor.isFeatureDisabled(ClientFeature.GUI_DISABLE_HUD))) {
                instance.render(guiGraphics);
            }
        }
    }
    *///?}
}
