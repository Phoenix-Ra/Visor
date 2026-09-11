package org.vmstudio.visor.api.client.input.redirect;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2fc;
import org.vmstudio.visor.api.client.input.InputHelper;
import org.vmstudio.visor.api.common.addon.VisorAddon;
import org.vmstudio.visor.api.common.addon.component.ComponentPriority;
import org.vmstudio.visor.api.common.addon.component.PrioritySupporter;
import org.vmstudio.visor.api.common.addon.component.VisorComponent;

/**
 * Redirects VR movement input from joystick
 * to methods in this component.
 * <p>
 *     Useful, when you need to add compatibility with mods that
 *     use mouse move/scroll in-game without a Screen
 *     (e.g. Create Aeronautics levers, physics assembler)
 * </p>
 */
public abstract class VRInputRedirect implements VisorComponent, PrioritySupporter {

    @Getter
    private final VisorAddon owner;

    @Getter @Setter
    private boolean enabled = true;

    public VRInputRedirect(@NotNull VisorAddon owner) {
        this.owner = owner;
    }


    /**
     * If VR input can be redirected
     *
     * @param player the local player
     * @return true/false
     */
    public abstract boolean canRedirect(@NotNull LocalPlayer player);

    /**
     * The raw joystick state, delivered every VR pre-tick while the redirect is
     * active.
     *
     * <p>
     *     Use it for anything that follows the stick continuously - a lever
     *     being pulled, a wheel being turned.
     * </p>
     *
     * @param player the local player
     * @param axis joystick position, x - right, y - forward, within [-1;1]
     * @return true if the axis was used, which also skips
     *         {@link #onScroll(LocalPlayer, double, double)} for this tick
     */
    public boolean onAxis(@NotNull LocalPlayer player,
                          @NotNull Vector2fc axis) {
        return false;
    }

    /**
     * Discrete scroll steps built up from the vertical joystick axis.
     *
     * @param player the local player
     * @param deltaX horizontal steps
     * @param deltaY vertical steps
     */
    public void onScroll(@NotNull LocalPlayer player,
                         double deltaX,
                         double deltaY) {
    }

    /**
     * Called once when the redirect stops being active.
     *
     * @param player the local player
     */
    public void onStop(@Nullable LocalPlayer player) {

    }


    public final boolean isEnabledAndCanRedirect(@NotNull LocalPlayer player) {
        return enabled && canRedirect(player);
    }

    @Override
    public @NotNull ComponentPriority getPriority() {
        return ComponentPriority.NORMAL;
    }
}
