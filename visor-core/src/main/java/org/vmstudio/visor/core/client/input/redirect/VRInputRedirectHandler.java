package org.vmstudio.visor.core.client.input.redirect;

import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.vmstudio.visor.api.client.input.redirect.VRInputRedirect;
import org.vmstudio.visor.core.client.ClientContext;

import static org.vmstudio.visor.core.client.VisorClientImpl.MC;


public class VRInputRedirectHandler {
    public static final VRInputRedirectHandler INSTANCE = new VRInputRedirectHandler();

    private static final float DEAD_ZONE = 0.1f;
    private static final int REPEAT_DELAY_TICKS = 6;
    private static final double MIN_STEPS_PER_SECOND = 3.0;
    private static final double MAX_STEPS_PER_SECOND = 15.0;

    private final Vector2f axis = new Vector2f();

    private VRInputRedirect active;

    private int heldDirection;
    private int ticksHeld;
    private double repeatSaved;


    public boolean isRedirecting() {
        return active != null;
    }


    public boolean handle(@Nullable LocalPlayer player,
                          @NotNull Vector2fc joystick) {
        VRInputRedirect redirect = player == null ? null : findRedirect(player);

        if (redirect != active) {
            stop(player);
            active = redirect;
        }
        if (redirect == null || player == null) {
            return false;
        }

        axis.set(
                applyDeadZone(joystick.x()),
                applyDeadZone(joystick.y())
        );

        if (redirect.onAxis(player, axis)) {
            resetScroll();
            return true;
        }

        scroll(player, redirect, axis.y);
        return true;
    }


    public void stop(@Nullable LocalPlayer player) {
        if (active == null) {
            return;
        }
        VRInputRedirect previous = active;
        active = null;
        resetScroll();
        previous.onStop(player);
    }

    public void stop() {
        stop(MC.player);
    }


    public void onUnregistered(@NotNull VRInputRedirect component) {
        if (active == component) {
            stop();
        }
    }


    @Nullable
    private VRInputRedirect findRedirect(@NotNull LocalPlayer player) {
        for (VRInputRedirect entry : ClientContext.inputManager
                .getInputRedirectRegistry().getSortedComponents()) {
            if (entry.isEnabledAndCanRedirect(player)) {
                return entry;
            }
        }
        return null;
    }

    private void scroll(@NotNull LocalPlayer player,
                        @NotNull VRInputRedirect redirect,
                        float value) {
        if (value == 0) {
            resetScroll();
            return;
        }

        int direction = value > 0 ? 1 : -1;
        if (direction != heldDirection) {
            heldDirection = direction;
            ticksHeld = 0;
            repeatSaved = 0;
            redirect.onScroll(player, 0, direction);
            return;
        }

        if (++ticksHeld < REPEAT_DELAY_TICKS) {
            return;
        }

        double stepsPerSecond = MIN_STEPS_PER_SECOND
                + (MAX_STEPS_PER_SECOND - MIN_STEPS_PER_SECOND) * Math.abs(value);

        repeatSaved += stepsPerSecond / 20.0;
        if (repeatSaved < 1) {
            return;
        }
        // keep the remainder, so the repeat speed stays even
        repeatSaved -= 1;
        redirect.onScroll(player, 0, direction);
    }

    private void resetScroll() {
        heldDirection = 0;
        ticksHeld = 0;
        repeatSaved = 0;
    }

    private float applyDeadZone(float value) {
        float strength = Math.max(Math.abs(value) - DEAD_ZONE, 0F) / (1F - DEAD_ZONE);
        return Math.copySign(strength, value);
    }
}
