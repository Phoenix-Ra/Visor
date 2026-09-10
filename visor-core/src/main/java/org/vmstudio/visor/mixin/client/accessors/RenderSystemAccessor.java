package org.vmstudio.visor.mixin.client.accessors;

import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderSystem.class)
public interface RenderSystemAccessor {

    @Accessor
    static int[] getShaderTextures() {
        return null;
    }

    @Accessor
    static void setShaderGameTime(float shaderGameTime) {
    }
}
