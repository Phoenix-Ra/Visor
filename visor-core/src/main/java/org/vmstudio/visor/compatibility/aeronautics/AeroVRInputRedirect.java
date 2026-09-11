package org.vmstudio.visor.compatibility.aeronautics;

import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2fc;
import org.vmstudio.visor.api.client.input.redirect.VRInputRedirect;
import org.vmstudio.visor.api.common.addon.VisorAddon;
import org.vmstudio.visor.compatibility.aeronautics.internal.AeronauticsInputInternal;


public class AeroVRInputRedirect extends VRInputRedirect {
    private static final String ID = "aeronautics";

    public AeroVRInputRedirect(@NotNull VisorAddon owner) {
        super(owner);
    }


    @Override
    public boolean canRedirect(@NotNull LocalPlayer player) {
        return AeronauticsInputInternal.isHoldInteractionActive();
    }

    @Override
    public boolean onAxis(@NotNull LocalPlayer player,
                          @NotNull Vector2fc axis) {
        if (axis.x() == 0 && axis.y() == 0) {
            return false;
        }
        return AeronauticsInputInternal.sendMouseMove(
                axis.x() * 60.0,
                -axis.y() * 15.0
        );
    }

    @Override
    public void onScroll(@NotNull LocalPlayer player,
                         double deltaX,
                         double deltaY) {
        AeronauticsInputInternal.sendMouseScroll(deltaX, deltaY);
    }

    @Override
    public @NotNull String getId() {
        return ID;
    }
}
