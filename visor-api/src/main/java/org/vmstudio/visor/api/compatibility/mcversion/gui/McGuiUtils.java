package org.vmstudio.visor.api.compatibility.mcversion.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
//? if >=1.21.9 {
/*import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
*///?}

/**
 * Utils for cross-mc-version GUI methods
 */
@Environment(EnvType.CLIENT)
public class McGuiUtils {
    private McGuiUtils() {
        throw new UnsupportedOperationException("This is an utility class and cannot be instantiated");
    }


    // ------- SCREEN -------

    public static void initScreen(Screen screen, int width, int height) {
        //? if >=1.21.9 {
        /*screen.init(width, height);
        *///?} else {
        screen.init(Minecraft.getInstance(), width, height);
        //?}
    }

    public static void renderWithTooltip(Screen screen, GuiGraphics guiGraphics,
                                         int mouseX, int mouseY, float partialTick) {
        //? if >=1.21.9 {
        /*screen.renderWithTooltipAndSubtitles(guiGraphics, mouseX, mouseY, partialTick);
        *///?} else {
        screen.renderWithTooltip(guiGraphics, mouseX, mouseY, partialTick);
        //?}
    }

    public static void setTooltipForNextRenderPass(Screen screen, GuiGraphics guiGraphics,
                                                   Tooltip tooltip, ClientTooltipPositioner positioner,
                                                   int mouseX, int mouseY, boolean focused) {
        //? if >=1.21.9 {
        /*guiGraphics.setTooltipForNextFrame(
                screen.getFont(), tooltip.toCharSequence(Minecraft.getInstance()),
                positioner, mouseX, mouseY, focused
        );
        *///?} else {
        screen.setTooltipForNextRenderPass(tooltip, positioner, focused);
        //?}
    }


    // ------- INPUT -------
    // classic signatures, forwarded to whatever the version expects

    public static boolean mouseClicked(GuiEventListener listener,
                                       double mouseX, double mouseY, int button) {
        //? if >=1.21.9 {
        /*return listener.mouseClicked(mouseButtonEvent(mouseX, mouseY, button), false);
        *///?} else {
        return listener.mouseClicked(mouseX, mouseY, button);
        //?}
    }

    public static boolean mouseReleased(GuiEventListener listener,
                                        double mouseX, double mouseY, int button) {
        //? if >=1.21.9 {
        /*return listener.mouseReleased(mouseButtonEvent(mouseX, mouseY, button));
        *///?} else {
        return listener.mouseReleased(mouseX, mouseY, button);
        //?}
    }

    public static boolean mouseDragged(GuiEventListener listener,
                                       double mouseX, double mouseY, int button,
                                       double dragX, double dragY) {
        //? if >=1.21.9 {
        /*return listener.mouseDragged(mouseButtonEvent(mouseX, mouseY, button), dragX, dragY);
        *///?} else {
        return listener.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        //?}
    }

    public static boolean mouseScrolled(GuiEventListener listener,
                                        double mouseX, double mouseY, double verticalAmount) {
        //? if >=1.20.2 {
        /*return listener.mouseScrolled(mouseX, mouseY, 0, verticalAmount);
        *///?} else {
        return listener.mouseScrolled(mouseX, mouseY, verticalAmount);
        //?}
    }

    public static boolean keyPressed(GuiEventListener listener,
                                     int keyCode, int scanCode, int modifiers) {
        //? if >=1.21.9 {
        /*return listener.keyPressed(new KeyEvent(keyCode, scanCode, modifiers));
        *///?} else {
        return listener.keyPressed(keyCode, scanCode, modifiers);
        //?}
    }

    public static boolean keyReleased(GuiEventListener listener,
                                      int keyCode, int scanCode, int modifiers) {
        //? if >=1.21.9 {
        /*return listener.keyReleased(new KeyEvent(keyCode, scanCode, modifiers));
        *///?} else {
        return listener.keyReleased(keyCode, scanCode, modifiers);
        //?}
    }

    public static boolean charTyped(GuiEventListener listener, char chr, int modifiers) {
        //? if >=1.21.9 {
        /*return listener.charTyped(new CharacterEvent(chr, modifiers));
        *///?} else {
        return listener.charTyped(chr, modifiers);
        //?}
    }


    // ------- KEY MODIFIERS -------

    public static boolean hasControlDown() {
        //? if >=1.21.9 {
        /*return Minecraft.getInstance().hasControlDown();
        *///?} else {
        return Screen.hasControlDown();
        //?}
    }

    public static boolean hasShiftDown() {
        //? if >=1.21.9 {
        /*return Minecraft.getInstance().hasShiftDown();
        *///?} else {
        return Screen.hasShiftDown();
        //?}
    }

    public static boolean hasAltDown() {
        //? if >=1.21.9 {
        /*return Minecraft.getInstance().hasAltDown();
        *///?} else {
        return Screen.hasAltDown();
        //?}
    }

    public static boolean isCopy(int keyCode) {
        //? if >=1.21.9 {
        /*return keyEvent(keyCode).isCopy();
        *///?} else {
        return Screen.isCopy(keyCode);
        //?}
    }

    public static boolean isCut(int keyCode) {
        //? if >=1.21.9 {
        /*return keyEvent(keyCode).isCut();
        *///?} else {
        return Screen.isCut(keyCode);
        //?}
    }

    public static boolean isPaste(int keyCode) {
        //? if >=1.21.9 {
        /*return keyEvent(keyCode).isPaste();
        *///?} else {
        return Screen.isPaste(keyCode);
        //?}
    }

    public static boolean isSelectAll(int keyCode) {
        //? if >=1.21.9 {
        /*return keyEvent(keyCode).isSelectAll();
        *///?} else {
        return Screen.isSelectAll(keyCode);
        //?}
    }

    //? if >=1.21.9 {
    /*public static MouseButtonEvent mouseButtonEvent(double mouseX, double mouseY, int button) {
        return new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(button, currentModifiers()));
    }

    private static KeyEvent keyEvent(int keyCode) {
        return new KeyEvent(keyCode, 0, currentModifiers());
    }

    private static int currentModifiers() {
        Minecraft mc = Minecraft.getInstance();
        return (mc.hasShiftDown() ? InputConstants.MOD_SHIFT : 0)
                | (mc.hasControlDown() ? InputConstants.MOD_CONTROL : 0)
                | (mc.hasAltDown() ? InputConstants.MOD_ALT : 0);
    }
    *///?}
}
