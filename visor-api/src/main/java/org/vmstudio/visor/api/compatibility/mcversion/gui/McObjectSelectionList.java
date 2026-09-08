package org.vmstudio.visor.api.compatibility.mcversion.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;

/**
 * Cross-mc-version adapter for ObjectSelectionList
 */
@Environment(EnvType.CLIENT)
public abstract class McObjectSelectionList<E extends ObjectSelectionList.Entry<E>> extends ObjectSelectionList<E> {

    protected McObjectSelectionList(Minecraft minecraft,
                                    int width, int height,
                                    int x, int y,
                                    int itemHeight) {
        //? if >=1.20.3 {
        super(minecraft, width, height, y, itemHeight);
        this.setX(x);
        //?} else {
        /*super(minecraft, width, height, y, y + height, itemHeight);
        this.setLeftPos(x);
        *///?}
    }


    // ------- STABLE API -------

    protected void renderContents(GuiGraphics guiGraphics,
                                  int mouseX, int mouseY,
                                  float partialTick) {
        renderDefault(guiGraphics, mouseX, mouseY, partialTick);
    }

    /**
     * The vanilla list body - background, header, rows, scrollbar and decorations.
     */
    protected final void renderDefault(GuiGraphics guiGraphics,
                                       int mouseX, int mouseY,
                                       float partialTick) {
        //? if >=1.20.3 {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        //?} else {
        /*super.render(guiGraphics, mouseX, mouseY, partialTick);
        *///?}
    }


    // 1.20.5 dropped the flag, the background moved to renderListBackground
    public void setRenderBackground(boolean render) {
        //? if <1.20.5 {
        /*super.setRenderBackground(render);
        *///?}
    }


    protected final int listLeft() {
        //? if >=1.20.3 {
        return getX();
        //?} else {
        /*return x0;
        *///?}
    }
    protected final int listRight() {
        //? if >=1.20.3 {
        return getRight();
        //?} else {
        /*return x1;
        *///?}
    }

    protected final int listTop() {
        //? if >=1.20.3 {
        return getY();
        //?} else {
        /*return y0;
        *///?}
    }
    protected final int listBottom() {
        //? if >=1.20.3 {
        return getBottom();
        //?} else {
        /*return y1;
        *///?}
    }


    // ------- MC-VERSION SPECIFIC IMPLEMENTATION -------

    //? if >=1.20.3 {
    @Override
    public final void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderContents(guiGraphics, mouseX, mouseY, partialTick);
    }
    //?} else {
    /*@Override
    public final void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderContents(guiGraphics, mouseX, mouseY, partialTick);
    }
    *///?}

}
