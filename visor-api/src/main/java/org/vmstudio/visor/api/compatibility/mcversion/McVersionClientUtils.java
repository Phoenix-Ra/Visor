package org.vmstudio.visor.api.compatibility.mcversion;

import net.minecraft.network.chat.Component;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Player;

import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
//? if >=1.20.5 {
import net.minecraft.client.gui.screens.GenericMessageScreen;
//?} else {
/*import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
*///?}
//? if <1.21.9 {
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
//?}

/**
 * Cross-mc-version Utils for client methods
 *
 */
public class McVersionClientUtils {
    private McVersionClientUtils() {
        throw new UnsupportedOperationException("This is an utility class and cannot be instantiated");
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
        if (screen instanceof GenericMessageScreen) return true;
        //?} else {
        /*if (screen instanceof GenericDirtMessageScreen) return true;
        *///?}
        //? if <1.21.9 {
        if (screen instanceof ReceivingLevelScreen) return true;
        //?}
        return screen instanceof ProgressScreen;
    }

    public static Screen savingLevelScreen(Component message) {
        //? if >=1.20.5 {
        return new GenericMessageScreen(message);
        //?} else {
        /*return new GenericDirtMessageScreen(message);
        *///?}
    }

    // ------- INTERACTION -------

    // 1.20.5 replaced MultiPlayerGameMode.getPickRange() with the interaction range attributes
    public static double blockPickRange(MultiPlayerGameMode gameMode, Player player) {
        //? if >=1.20.5 {
        return player.blockInteractionRange();
        //?} else {
        /*return gameMode.getPickRange();
        *///?}
    }
}
