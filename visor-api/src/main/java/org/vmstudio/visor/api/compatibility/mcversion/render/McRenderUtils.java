package org.vmstudio.visor.api.compatibility.mcversion.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.vmstudio.visor.api.compatibility.mcversion.McVersionUtils;

import java.util.function.Supplier;

/**
 * Cross-mc-version Utils for rendering methods
 */
public class McRenderUtils {
    private McRenderUtils() {
        throw new UnsupportedOperationException("This is an utility class and cannot be instantiated");
    }

    // ------- PLAYER SKIN -------

    public static ResourceLocation getSkinTexture(AbstractClientPlayer player) {
        //? if >=1.20.2 {
        return player.getSkin().texture();
        //?} else {
        /*return player.getSkinTextureLocation();
        *///?}
    }

    public static String getModelName(AbstractClientPlayer player) {
        //? if >=1.20.2 {
        return player.getSkin().model().id();
        //?} else {
        /*return player.getModelName();
        *///?}
    }

    // ------- CROSSHAIR -------

    public static ResourceLocation crosshairTexture() {
        //? if >=1.20.2 {
        return McVersionUtils.newResourceLoc("minecraft", "textures/gui/sprites/hud/crosshair.png");
        //?} else {
        /*return Gui.GUI_ICONS_LOCATION;
        *///?}
    }

    public static float crosshairUvSize() {
        //? if >=1.20.2 {
        return 1f;
        //?} else {
        /*return 15f / 256f;
        *///?}
    }

    // ------- SHADERS -------

    // 1.20.5 replaced the position_tex_color_normal shader with rendertype_clouds
    public static Supplier<ShaderInstance> positionTexColorNormalShader() {
        //? if >=1.20.5 {
        return GameRenderer::getRendertypeCloudsShader;
        //?} else {
        /*return GameRenderer::getPositionTexColorNormalShader;
        *///?}
    }

    // ------- MATRICES -------

    // 1.20.5 renamed PoseStack.mulPoseMatrix to mulPose
    public static void mulPose(PoseStack poseStack, Matrix4f matrix) {
        //? if >=1.20.5 {
        poseStack.mulPose(matrix);
        //?} else {
        /*poseStack.mulPoseMatrix(matrix);
        *///?}
    }
}
