package org.vmstudio.visor.compatibility.aeronautics;

import org.jetbrains.annotations.NotNull;
import org.vmstudio.visor.api.ModLoader;
import org.vmstudio.visor.api.VisorAPI;
import org.vmstudio.visor.api.common.addon.VisorAddon;

public class AeronauticsHelper {
    public static void initializeCompat(@NotNull VisorAddon owner) {
        if (isLoaded()) {
            var registries = VisorAPI.addonManager().getRegistries();

            registries.itemPoses().registerComponent(new CreativeStaffItemPose(owner));
            registries.inputRedirects().registerComponent(new AeroVRInputRedirect(owner));
        }
    }

    public static boolean isLoaded() {
        return ModLoader.get().isModLoaded("simulated");
    }
}
