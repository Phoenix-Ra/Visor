package org.vmstudio.visor.api.compatibility.mcversion.render;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.joml.Matrix4f;

/**
 * Cross-mc-version immediate-mode vertex building
 */
public final class McVertexBuilder {

    private static final McVertexBuilder INSTANCE = new McVertexBuilder();

    private BufferBuilder builder;

    private McVertexBuilder() {
    }

    // ------- STABLE API -------

    public static McVertexBuilder get() {
        //? if <1.21 {
        /*INSTANCE.builder = Tesselator.getInstance().getBuilder();
        *///?}
        return INSTANCE;
    }

    public McVertexBuilder begin(VertexFormat.Mode mode, VertexFormat format) {
        //? if >=1.21 {
        this.builder = Tesselator.getInstance().begin(mode, format);
        //?} else {
        /*this.builder.begin(mode, format);
        *///?}
        return this;
    }

    public McVertexBuilder vertex(Matrix4f matrix, float x, float y, float z) {
        //? if >=1.21 {
        builder.addVertex(matrix, x, y, z);
        //?} else {
        /*builder.vertex(matrix, x, y, z);
        *///?}
        return this;
    }

    public McVertexBuilder vertex(float x, float y, float z) {
        //? if >=1.21 {
        builder.addVertex(x, y, z);
        //?} else {
        /*builder.vertex(x, y, z);
        *///?}
        return this;
    }

    public McVertexBuilder vertex(double x, double y, double z) {
        return vertex((float) x, (float) y, (float) z);
    }

    public McVertexBuilder uv(float u, float v) {
        //? if >=1.21 {
        builder.setUv(u, v);
        //?} else {
        /*builder.uv(u, v);
        *///?}
        return this;
    }

    public McVertexBuilder color(int red, int green, int blue, int alpha) {
        //? if >=1.21 {
        builder.setColor(red, green, blue, alpha);
        //?} else {
        /*builder.color(red, green, blue, alpha);
        *///?}
        return this;
    }

    public McVertexBuilder color(float red, float green, float blue, float alpha) {
        //? if >=1.21 {
        builder.setColor(red, green, blue, alpha);
        //?} else {
        /*builder.color(red, green, blue, alpha);
        *///?}
        return this;
    }

    public McVertexBuilder normal(float x, float y, float z) {
        //? if >=1.21 {
        builder.setNormal(x, y, z);
        //?} else {
        /*builder.normal(x, y, z);
        *///?}
        return this;
    }

    public McVertexBuilder uv2(int packedLight) {
        //? if >=1.21 {
        builder.setLight(packedLight);
        //?} else {
        /*builder.uv2(packedLight);
        *///?}
        return this;
    }

    public McVertexBuilder overlayCoords(int packedOverlay) {
        //? if >=1.21 {
        builder.setOverlay(packedOverlay);
        //?} else {
        /*builder.overlayCoords(packedOverlay);
        *///?}
        return this;
    }

    public McVertexBuilder endVertex() {
        //? if <1.21 {
        /*builder.endVertex();
        *///?}
        return this;
    }

    public void draw() {
        //? if >=1.21 {
        BufferUploader.drawWithShader(builder.buildOrThrow());
        //?} else {
        /*BufferUploader.drawWithShader(builder.end());
        *///?}
    }

    public void drawNoShader() {
        //? if >=1.21 {
        BufferUploader.draw(builder.buildOrThrow());
        //?} else {
        /*BufferUploader.draw(builder.end());
        *///?}
    }

    public BufferBuilder handle() {
        return builder;
    }
}
