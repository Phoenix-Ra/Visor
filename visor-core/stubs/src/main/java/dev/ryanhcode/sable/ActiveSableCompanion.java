package dev.ryanhcode.sable;

import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.sublevel.SubLevel;

public class ActiveSableCompanion implements SableCompanion {
    @Override
    public Object projectOutOfSubLevel(Object level, Object pos) {
        return null;
    }

    @Override
    public SubLevel getContaining(Object level, Object pos) {
        return null;
    }
}
