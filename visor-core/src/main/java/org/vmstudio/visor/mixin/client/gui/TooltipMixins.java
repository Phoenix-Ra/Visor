package org.vmstudio.visor.mixin.client.gui;


import org.vmstudio.visor.api.client.gui.overlays.framework.VROverlayScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
//? if >=1.20.3 {
import net.minecraft.client.gui.components.Tooltip;
//?} else {
/*import net.minecraft.client.gui.components.AbstractWidget;
*///?}


public class TooltipMixins {

    /**
     * Attaches a tooltip to the overlay handling screen
     */
    //? if >=1.20.3 {
    // 1.20.3 moved the attach point off AbstractWidget onto the tooltip itself.
    @Mixin(Tooltip.class)
    public static class TooltipScreenMixin {

        @Redirect(
                method = "refreshTooltipForNextRenderPass",
                at = @At(
                        value = "FIELD",
                        target = "Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screens/Screen;"
                )
        )
        private Screen visor$redirectMinecraftScreen(Minecraft minecraftInstance) {
            VROverlayScreen overlay = VROverlayScreen.getRenderingOverlay();
            if (overlay != null) {
                return overlay;
            }
            return minecraftInstance.screen;
        }
    }
    //?} else {
    /*@Mixin(AbstractWidget.class)
    public static class TooltipScreenMixin {

        @Redirect(
                method = "updateTooltip",
                at = @At(
                        value = "FIELD",
                        target = "Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screens/Screen;"
                )
        )
        private Screen visor$redirectMinecraftScreen(Minecraft minecraftInstance) {
            VROverlayScreen overlay = VROverlayScreen.getRenderingOverlay();
            if (overlay != null) {
                return overlay;
            }
            return minecraftInstance.screen;
        }
    }
    *///?}
}
