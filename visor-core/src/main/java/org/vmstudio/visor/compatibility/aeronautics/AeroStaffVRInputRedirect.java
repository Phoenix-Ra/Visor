package org.vmstudio.visor.compatibility.aeronautics;

import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2fc;
import org.vmstudio.visor.api.client.input.redirect.VRInputRedirect;
import org.vmstudio.visor.api.common.addon.VisorAddon;
import org.vmstudio.visor.compatibility.aeronautics.internal.AeronauticsInputInternal;
import org.vmstudio.visor.compatibility.aeronautics.internal.AeronauticsStaffInternal;

public class AeroStaffVRInputRedirect extends VRInputRedirect {
    private static final String ID = "aeronautics_staff";


    private static final double ROTATE_DEGREES_PER_TICK = 4.0;

    public AeroStaffVRInputRedirect(@NotNull VisorAddon owner) {
        super(owner);
    }


    @Override
    public boolean canRedirect(@NotNull LocalPlayer player) {
        return player.isShiftKeyDown() && AeronauticsStaffInternal.isDragging();
    }

    @Override
    public boolean onAxis(@NotNull LocalPlayer player,
                          @NotNull Vector2fc axis) {
        if (axis.x() != 0) {
            AeronauticsStaffInternal.rotateDragged(
                    Math.toRadians(axis.x() * ROTATE_DEGREES_PER_TICK)
            );
        }
        //We need scroll steps for the distance, so,
        // don't send true here to not cancel scroll method
        return false;
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
