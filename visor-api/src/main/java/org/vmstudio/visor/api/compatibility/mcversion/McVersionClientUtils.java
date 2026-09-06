package org.vmstudio.visor.api.compatibility.mcversion;

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
 * Utils for client cross--mc-version methods
 */
public class McVersionClientUtils {
    private McVersionClientUtils() {
        throw new UnsupportedOperationException("This is an utility class and cannot be instantiated");
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
