package com.lightdev6.computing.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.network.chat.Component;

public class MultiLineTextBox extends MultiLineEditBox {
    public MultiLineTextBox(Font pFont, int pX, int pY, int pWidth, int pHeight, Component pPlaceholder, Component pMessage) {
        super(pFont, pX, pY, pWidth, pHeight, pPlaceholder, pMessage);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        return false;
    }

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        return false;
    }

}
