package org.vmstudio.visor.api.compatibility.mcversion;

import org.vmstudio.visor.api.compatibility.mcversion.McVersionUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
//? if >=1.20.5 {
/*import net.minecraft.client.gui.screens.GenericMessageScreen;
*///?} else {
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
//?}
//? if <1.21.9 {
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
//?}

/**
 * Cross-mc-version Utils for client methods
 */
public class McVersionClientUtils {
    private McVersionClientUtils() {
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

    // ------- LEVEL -------

    public static boolean isConnectedToRealms(Minecraft minecraft) {
        //? if >=1.20.2 {
        ServerData server = minecraft.getCurrentServer();
        return server != null && server.isRealm();
        //?} else {
        /*return minecraft.isConnectedToRealms();
        *///?}
    }

    public static void clearLevel(Minecraft minecraft) {
        //? if >=1.20.2 {
        minecraft.disconnect();
        //?} else {
        /*minecraft.clearLevel();
        *///?}
    }

    public static void clearLevel(Minecraft minecraft, Screen progressScreen) {
        //? if >=1.20.2 {
        minecraft.disconnect(progressScreen);
        //?} else {
        /*minecraft.clearLevel(progressScreen);
        *///?}
    }

    public static boolean isLevelTransitionScreen(@Nullable Screen screen) {
        //? if >=1.20.5 {
        /*if (screen instanceof GenericMessageScreen) return true;
        *///?} else {
        if (screen instanceof GenericDirtMessageScreen) return true;
        //?}
        //? if <1.21.9 {
        if (screen instanceof ReceivingLevelScreen) return true;
        //?}
        return screen instanceof ProgressScreen;
    }
}
