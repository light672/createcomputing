package com.lightdev6.computing.block.computer;

/*

 */


import com.lightdev6.computing.AllPackets;
import com.lightdev6.computing.block.redstonedetector.RedstoneDetectorBlockEntity;
import com.lightdev6.computing.packets.ConfigureRedstoneDetectorSignalPacket;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllItems;
import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.element.GuiGameElement;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import com.sun.jna.platform.win32.WinDef;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.Whence;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import org.lwjgl.glfw.GLFW;

public class ComputerScreen extends AbstractSimiScreen {
    private AllGuiTextures background;
    private MultiLineEditBox terminal;
    private BlockPos blockPos;
    private IconButton abort;
    private IconButton confirm;

    private final Component abortLabel = Lang.translateDirect("action.discard");
    private final Component confirmLabel = Lang.translateDirect("action.saveToFile");

    private ComputerBlockEntity computer;

    public ComputerScreen(ComputerBlockEntity computer){
        super(Component.literal("Edit Signal Name"));
        background = AllGuiTextures.SCHEMATIC_PROMPT;
        this.computer = computer;
        this.blockPos = computer.getBlockPos();
    }

    @Override
    protected void init() {
        setWindowSize(background.width, background.height);
        super.init();

        int x = guiLeft;
        int y = guiTop;

        /*nameField = new EditBox(font, x+49, y + 26, 131, 10, Components.immutableEmpty());
        nameField.setTextColor(-1);
        nameField.setTextColorUneditable(-1);
        nameField.setBordered(false);
        nameField.changeFocus(true);
        //setFocused(nameField);
        nameField.setValue(redstoneDetector.getSignalName());
        addRenderableWidget(nameField);*/

        terminal = new MultiLineEditBox(font, x + 49, y + 60, 131, 100, Components.immutableEmpty(), Components.immutableEmpty());
        terminal.setValue("");
        setInitialFocus(terminal);
        addRenderableWidget(terminal);




    }

    @Override
    protected void renderWindow(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;
        background.render(ms, x, y, this);
        drawCenteredString(ms, font, title,  x + (background.width - 8) / 2, y + 3, 0xFFFFFF);
        GuiGameElement.of(AllItems.ANDESITE_ALLOY.asStack())
                .at(x + 22, y + 23, 0)
                .render(ms);


    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 && this.shouldCloseOnEsc()) {
            this.onClose();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_TAB) {
            terminal.textField.insertText("    ");
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_LEFT_BRACKET) {
            return true;
        }
        return terminal.keyPressed(keyCode, scanCode, modifiers);


    }

    @Override
    public boolean charTyped(char p_94683_, int p_94684_) {
        if (p_94683_ == "{".charAt(0)){
            terminal.textField.insertText("{");
            terminal.textField.insertText("}");
            terminal.textField.setSelecting(true);
            terminal.textField.seekCursor(Whence.RELATIVE, -1);
            terminal.textField.setSelecting(false);
            return true;
        }
        return super.charTyped(p_94683_, p_94684_);
    }

    private void confirm(){
        System.out.println(terminal.getValue());
        //AllPackets.channel.sendToServer(new ConfigureRedstoneDetectorSignalPacket(blockPos, terminal.getValue()));
        //onClose();
    }
}
