package dev.ryanhcode.sable.companion;

import dev.ryanhcode.sable.sublevel.SubLevel;

public interface SableCompanion {
    SableCompanion INSTANCE = null;

    Object projectOutOfSubLevel(Object level, Object pos);

    SubLevel getContaining(Object level, Object pos);
}
