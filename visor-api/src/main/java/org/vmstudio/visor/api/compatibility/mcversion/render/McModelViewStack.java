package org.vmstudio.visor.api.compatibility.mcversion.render;

import com.mojang.blaze3d.systems.RenderSystem;

/**
 * Cross-mc-version facade over the GL model-view stack.
 */
public class McModelViewStack {
    private McModelViewStack() {
        throw new UnsupportedOperationException("This is an utility class and cannot be instantiated");
    }

    public static void push() {
        //? if >=1.20.5 {
        RenderSystem.getModelViewStack().pushMatrix();
        //?} else {
        /*RenderSystem.getModelViewStack().pushPose();
        *///?}
    }

    public static void pop() {
        //? if >=1.20.5 {
        RenderSystem.getModelViewStack().popMatrix();
        //?} else {
        /*RenderSystem.getModelViewStack().popPose();
        *///?}
    }

    public static void identity() {
        //? if >=1.20.5 {
        RenderSystem.getModelViewStack().identity();
        //?} else {
        /*RenderSystem.getModelViewStack().setIdentity();
        *///?}
    }

    public static void translate(float x, float y, float z) {
        RenderSystem.getModelViewStack().translate(x, y, z);
    }
}
