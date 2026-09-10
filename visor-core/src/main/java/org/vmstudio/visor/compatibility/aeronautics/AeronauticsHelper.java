package org.vmstudio.visor.compatibility.aeronautics;

import org.jetbrains.annotations.NotNull;
import org.vmstudio.visor.api.ModLoader;
import org.vmstudio.visor.api.VisorAPI;
import org.vmstudio.visor.api.common.addon.VisorAddon;

public class AeronauticsHelper {
    public static void initializeCompat(@NotNull VisorAddon owner) {
        if (isLoaded()) {
            VisorAPI.addonManager().getRegistries().itemPoses().registerComponent(new CreativeStaffItemPose(owner));
        }
    }

    public static boolean isLoaded() {
        return ModLoader.get().isModLoaded("simulated");
    }
}
