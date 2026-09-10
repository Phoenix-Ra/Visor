package org.vmstudio.visor.compatibility.sable;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.vmstudio.visor.api.ModLoader;
import org.vmstudio.visor.compatibility.sable.internal.SableCompatHelperInternal;

public final class SableCompatHelper {
    public static final String MOD_ID = "sable";

    public static boolean isLoaded() {
        return ModLoader.get().isModLoaded(MOD_ID);
    }

    public static Vec3 toWorldPos(@Nullable Level level, @Nullable HitResult hitResult, Vec3 fallback) {
        return SableCompatHelperInternal.toWorldPos(level, hitResult, fallback);
    }

    public static @Nullable Quaterniond getSubLevelOrientation(@Nullable Level level, Vec3 pos) {
        return SableCompatHelperInternal.getSubLevelOrientation(level, pos);
    }
}
