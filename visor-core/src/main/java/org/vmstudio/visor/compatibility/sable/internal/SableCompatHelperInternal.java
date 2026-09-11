package org.vmstudio.visor.compatibility.sable.internal;

import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.Position;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.vmstudio.visor.api.common.utils.LoggerUtils;
import org.vmstudio.visor.compatibility.OneShotSetup;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * This class should not be called anywhere outside SableCompatHelper to ensure JVM will not load any sable classes unless it's installed.
 * @see org.vmstudio.visor.compatibility.sable.SableCompatHelper
 */
public class SableCompatHelperInternal {
    private static final OneShotSetup SETUP = new OneShotSetup(SableCompatHelperInternal::resolve);

    private static Object helperInstance;
    private static Method projectOutOfSubLevelMethod;
    private static Method getContainingMethod;

    public static Vec3 toWorldPos(@Nullable Level level, @Nullable HitResult hitResult, Vec3 fallback) {
        if (!SETUP.ok() || level == null || !(hitResult instanceof BlockHitResult)) {
            return fallback;
        }

        try {
            Vec3 location = hitResult.getLocation();
            Object projected = projectOutOfSubLevelMethod.invoke(helperInstance, level, location);
            if (projected instanceof Vec3 vec3) {
                return vec3;
            }
        } catch (Throwable t) {
            LoggerUtils.getLogger().warn("Visor: failed to project position out of Sable sub-level", t);
        }

        return fallback;
    }

    public static @Nullable Quaterniond getSubLevelOrientation(@Nullable Level level, Vec3 pos) {
        if (!SETUP.ok()) {
            return null;
        }

        try {
            Object subLevelObject = getContainingMethod.invoke(helperInstance, level, pos);
            if (subLevelObject == null) {
                return null;
            }
            SubLevel subLevel = (SubLevel) subLevelObject;

            Pose3dc pose;
            if (subLevel instanceof ClientSubLevel clientSubLevel) {
                pose = clientSubLevel.renderPose();
            } else {
                pose = subLevel.logicalPose();
            }

            return (Quaterniond) pose.getClass().getMethod("orientation").invoke(pose);
        } catch (Throwable t) {
            LoggerUtils.getLogger().warn("Visor: failed to get rotation of Sable sub-level", t);
        }

        return null;
    }

    private static boolean resolve() throws ReflectiveOperationException {
        Class<?> sableClass = Class.forName("dev.ryanhcode.sable.Sable");
        Field helperField = sableClass.getField("HELPER");
        helperInstance = helperField.get(null);
        if (helperInstance == null) {
            return false;
        }

        Class<?> companionClass = helperInstance.getClass();

        for (Method m : companionClass.getMethods()) {
            if ("projectOutOfSubLevel".equals(m.getName()) && m.getParameterCount() == 2) {
                Class<?>[] params = m.getParameterTypes();
                if (Level.class.isAssignableFrom(params[0]) && Position.class.isAssignableFrom(params[1])) {
                    projectOutOfSubLevelMethod = m;
                }
            }
        }

        for (Method m : companionClass.getMethods()) {
            if ("getContaining".equals(m.getName()) && m.getParameterCount() == 2) {
                Class<?>[] params = m.getParameterTypes();
                if (Level.class.isAssignableFrom(params[0]) && Position.class.isAssignableFrom(params[1])) {
                    getContainingMethod = m;
                }
            }
        }

        return (projectOutOfSubLevelMethod != null && getContainingMethod != null);
    }

}
