package org.vmstudio.visor.compatibility.aeronautics.internal;

import org.joml.Quaterniond;
import org.vmstudio.visor.api.common.utils.LoggerUtils;
import org.vmstudio.visor.compatibility.OneShotSetup;

import java.lang.reflect.Method;


public final class AeronauticsStaffInternal {
    private static final String CLIENT_CLASS =
            "dev.simulated_team.simulated.SimulatedClient";
    private static final String STAFF_HANDLER_FIELD = "PHYSICS_STAFF_CLIENT_HANDLER";
    private static final String DRAG_SESSION_CLASS =
            "dev.simulated_team.simulated.content.physics_staff.PhysicsStaffClientHandler$ClientDragSession";

    private static final OneShotSetup SETUP = new OneShotSetup(AeronauticsStaffInternal::resolve);

    private static Object staffHandler;
    private static Method getDragSessionMethod;
    private static Method dragOrientationMethod;



    public static boolean isDragging() {
        return getDragSession() != null;
    }

    public static void rotateDragged(double radians) {
        Object session = getDragSession();
        if (session == null) {
            return;
        }
        try {
            Object orientation = dragOrientationMethod.invoke(session);
            if (orientation instanceof Quaterniond quaternion) {
                quaternion.rotateLocalY(radians);
            }
        } catch (Throwable t) {
            fail("rotate the physics staff drag session", t);
        }
    }


    private static Object getDragSession() {
        if (!SETUP.ok()) {
            return null;
        }
        try {
            return getDragSessionMethod.invoke(staffHandler);
        } catch (Throwable t) {
            fail("read the physics staff drag session", t);
            return null;
        }
    }

    private static void fail(String what, Throwable t) {
        SETUP.disable();
        LoggerUtils.getLogger().warn("Visor: failed to {}, physics staff input compat disabled", what, t);
    }

    private static boolean resolve() throws ReflectiveOperationException {
        staffHandler = Class.forName(CLIENT_CLASS)
                .getField(STAFF_HANDLER_FIELD)
                .get(null);
        if (staffHandler == null) {
            return false;
        }

        getDragSessionMethod = staffHandler.getClass().getMethod("getDragSession");
        dragOrientationMethod = Class.forName(DRAG_SESSION_CLASS).getMethod("dragOrientation");

        return true;
    }


    private AeronauticsStaffInternal() {
        throw new UnsupportedOperationException("This is an utility class and cannot be instantiated");
    }
}
