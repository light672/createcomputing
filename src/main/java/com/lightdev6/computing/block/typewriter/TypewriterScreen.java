package com.lightdev6.computing.block.typewriter;

import com.lightdev6.computing.AllPackets;
import com.lightdev6.computing.block.computer.ComputerBlockEntity;
import com.lightdev6.computing.gui.MultiLineTextBox;
import com.lightdev6.computing.packets.ComputerRequestTerminalUpdatePacket;
import com.lightdev6.computing.packets.ComputerSendRunPacket;
import com.lightdev6.computing.packets.ComputerSendTerminalPacket;
import com.lightdev6.computing.packets.ConfigurePlateScriptPacket;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import org.lwjgl.glfw.GLFW;

public class TypewriterScreen extends AbstractSimiScreen {
    private MultiLineEditBox terminal;
    private BlockPos blockPos;
    private IconButton save;


    private TypewriterBlockEntity typewriter;


    public TypewriterScreen(TypewriterBlockEntity typewriter){
        super(Component.literal("Edit Signal Name"));
        this.typewriter = typewriter;
        this.blockPos = this.typewriter.getBlockPos();
    }

    @Override
    protected void init() {
        int width = 380;
        int height = 150;
        setWindowSize(width, height);
        super.init();
        int x = guiLeft;
        int y = guiTop;
        save = new IconButton(x - 20,  y, AllIcons.I_CONFIG_SAVE);
        save.withCallback(() -> {
            save();
        });
        save.setToolTip(Component.literal("Save Script To Plate"));
        addRenderableWidget(save);
        terminal = new MultiLineEditBox(font, x, y, width, 150, Components.immutableEmpty(), Components.immutableEmpty());
        terminal.setValue(typewriter.getPlate().getTag().getString("Script"));
        setInitialFocus(terminal);
        addRenderableWidget(terminal);
    }

    @Override
    protected void renderWindow(PoseStack ms, int mouseX, int mouseY, float partialTicks) {}

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 && this.shouldCloseOnEsc()) {
            leave();
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_TAB && terminal.isFocused()) {
            terminal.textField.insertText("    ");
        }

        //getting a character by the index of the cursor will always return the character infront of the cursor.
        if (keyCode == GLFW.GLFW_KEY_ENTER && terminal.isFocused()){
            if (terminal.textField.cursor != 0) {
                if (terminal.getValue().charAt(terminal.textField.cursor - 1) == "{".charAt(0)) {
                    terminal.textField.insertText("\n    \n}");
                    terminal.textField.cursor -= 2;
                    terminal.textField.selectCursor = terminal.textField.cursor;
                    return true;
                }

            }
        }
        return terminal.keyPressed(keyCode, scanCode, modifiers);



    }

    @Override
    public boolean charTyped(char p_94683_, int p_94684_) {
        if (p_94683_ == "(".charAt(0)){
            terminal.textField.insertText("()");
            terminal.textField.cursor -= 1;
            terminal.textField.selectCursor = terminal.textField.cursor;
            return true;
        }


        return super.charTyped(p_94683_, p_94684_);
    }

    private void confirm(){
        save();
        onClose();
    }

    private void save(){
        getMinecraft().player.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1.0F, 1.0F);
        AllPackets.channel.sendToServer(new ConfigurePlateScriptPacket(blockPos, terminal.getValue()));
    }

    private void leave(){
        //TODO: Run a check and display a GUI element if the player has unsaved changes.
        confirm();
    }
}
