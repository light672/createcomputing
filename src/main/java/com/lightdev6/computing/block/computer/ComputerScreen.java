package com.lightdev6.computing.block.computer;




import com.lightdev6.computing.AllPackets;
import com.lightdev6.computing.Computing;
import com.lightdev6.computing.gui.MultiLineTextBox;
import com.lightdev6.computing.packets.ComputerRequestTerminalUpdatePacket;
import com.lightdev6.computing.packets.ComputerSendRunPacket;
import com.lightdev6.computing.packets.ComputerSendTerminalPacket;
import com.lightdev6.computing.packets.ConfigureComputerScriptPacket;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

public class ComputerScreen extends AbstractSimiScreen {
    private MultiLineEditBox terminal;
    private MultiLineTextBox output;
    private BlockPos blockPos;
    private IconButton save;
    private IconButton run;
    private IconButton clear;


    private ComputerBlockEntity computer;

    private int timer;

    public ComputerScreen(ComputerBlockEntity computer){
        super(Component.literal("Edit Signal Name"));
        this.computer = computer;
        this.blockPos = computer.getBlockPos();
    }


    @Override
    public void tick() {
        super.tick();
        computer.getUpdateTag();
        output.setValue(computer.getTerminal());
        if (timer >= 4){
            timer = 0;
            AllPackets.channel.sendToServer(new ComputerRequestTerminalUpdatePacket(blockPos));
        } else {
            timer++;
        }
    }

    @Override
    protected void init() {
        int width = 380;
        int height = 210;
        setWindowSize(width, height);
        super.init();

        int x = guiLeft;
        int y = guiTop;



        run = new IconButton(x - 20, y, AllIcons.I_PLAY);
        run.withCallback(() -> {
            run();
        });
        run.setToolTip(Component.literal("Run Script"));
        addRenderableWidget(run);

        save = new IconButton(x - 20,  y + 20, AllIcons.I_CONFIG_SAVE);
        save.withCallback(() -> {
            save();
        });
        save.setToolTip(Component.literal("Save Script"));
        addRenderableWidget(save);

        terminal = new MultiLineEditBox(font, x, y, width, 150, Components.immutableEmpty(), Components.immutableEmpty());
        terminal.setValue(computer.getScript());
        setInitialFocus(terminal);

        addRenderableWidget(terminal);

        output = new MultiLineTextBox(font, x, y + 153, width, 57, Components.immutableEmpty(), Components.immutableEmpty());
        output.setValue(computer.getTerminal());
        addRenderableWidget(output);

        clear = new IconButton(x - 20,  y + 153, AllIcons.I_TRASH);
        clear.withCallback(() -> {
            clearTerminal();
        });
        clear.setToolTip(Component.literal("Clear Terminal"));
        addRenderableWidget(clear);




    }

    @Override
    protected void renderWindow(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;


    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 && this.shouldCloseOnEsc()) {
            confirm();
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
        AllPackets.channel.sendToServer(new ConfigureComputerScriptPacket(blockPos, terminal.getValue()));


    }

    private void run(){
        AllPackets.channel.sendToServer(new ComputerSendRunPacket(blockPos));
    }

    private void clearTerminal(){
        AllPackets.channel.sendToServer(new ComputerSendTerminalPacket(blockPos, ""));
    }
}
