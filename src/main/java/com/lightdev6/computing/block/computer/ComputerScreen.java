package com.lightdev6.computing.block.computer;




import com.lightdev6.computing.AllPackets;
import com.lightdev6.computing.Computing;
import com.lightdev6.computing.packets.ComputerSendRunPacket;
import com.lightdev6.computing.packets.ConfigureComputerScriptPacket;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

public class ComputerScreen extends AbstractSimiScreen {
    private AllGuiTextures background;
    private MultiLineEditBox terminal;
    private BlockPos blockPos;
    private IconButton save;
    private IconButton run;

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
        setWindowSize(426, 254);
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

        terminal = new MultiLineEditBox(font, x, y, 426, 200, Components.immutableEmpty(), Components.immutableEmpty());
        terminal.setValue(computer.getScript());
        setInitialFocus(terminal);

        addRenderableWidget(terminal);




    }

    @Override
    protected void renderWindow(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;
        //background.render(ms, x, y, this);
        //drawCenteredString(ms, font, title,  x + (background.width - 8) / 2, y + 3, 0xFFFFFF);
        //GuiGameElement.of(AllItems.ANDESITE_ALLOY.asStack())
        //        .at(x + 22, y + 23, 0)
        //        .render(ms);


    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 && this.shouldCloseOnEsc()) {
            confirm();
            return true;
        }

        System.out.println(keyCode);
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
        if (p_94683_ == "{".charAt(0)){
            terminal.textField.insertText("{");
            int cursor = terminal.textField.cursor;
            terminal.textField.insertText("}");
            terminal.textField.cursor = cursor;
            terminal.textField.selectCursor = cursor;
            return true;
        }
        if (p_94683_ == "(".charAt(0)){
            terminal.textField.insertText("()");
            terminal.textField.cursor -= 1;
            terminal.textField.selectCursor = terminal.textField.cursor;
            return true;
        }


        return super.charTyped(p_94683_, p_94684_);
    }

    private void confirm(){
        AllPackets.channel.sendToServer(new ConfigureComputerScriptPacket(blockPos, terminal.getValue()));
        onClose();
    }

    private void save(){
        AllPackets.channel.sendToServer(new ConfigureComputerScriptPacket(blockPos, terminal.getValue()));
    }

    private void run(){
        AllPackets.channel.sendToServer(new ComputerSendRunPacket(blockPos));
    }
}
