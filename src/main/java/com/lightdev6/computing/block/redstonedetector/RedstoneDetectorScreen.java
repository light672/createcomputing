package com.lightdev6.computing.block.redstonedetector;

import com.lightdev6.computing.AllPackets;
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
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class RedstoneDetectorScreen extends AbstractSimiScreen {

    private AllGuiTextures background;
    private EditBox nameField;
    private BlockPos blockPos;
    private IconButton abort;
    private IconButton confirm;

    private final Component abortLabel = Lang.translateDirect("action.discard");
    private final Component confirmLabel = Lang.translateDirect("action.saveToFile");

    private RedstoneDetectorBlockEntity redstoneDetector;

    public RedstoneDetectorScreen(RedstoneDetectorBlockEntity redstoneDetector){
        super(Component.literal("Edit Signal Name"));
        background = AllGuiTextures.SCHEMATIC_PROMPT;
        this.redstoneDetector = redstoneDetector;
        this.blockPos = redstoneDetector.getBlockPos();
    }

    @Override
    protected void init() {
        setWindowSize(background.width, background.height);
        super.init();

        int x = guiLeft;
        int y = guiTop;

        nameField = new EditBox(font, x+49, y + 26, 131, 10, Components.immutableEmpty());
        nameField.setTextColor(-1);
        nameField.setTextColorUneditable(-1);
        nameField.setBordered(false);
        nameField.changeFocus(true);
        //setFocused(nameField);
        nameField.setValue(redstoneDetector.getSignalName());
        addRenderableWidget(nameField);



        abort = new IconButton(x + 7, y + 53, AllIcons.I_TRASH);
        abort.withCallback(() -> {
            CreateClient.SCHEMATIC_AND_QUILL_HANDLER.discard();
            onClose();
        });
        abort.setToolTip(abortLabel);
        addRenderableWidget(abort);

        confirm = new IconButton(x + 158, y + 53, AllIcons.I_CONFIRM);
        confirm.withCallback(() -> {
            confirm();
        });
        confirm.setToolTip(confirmLabel);
        addRenderableWidget(confirm);


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
        if (keyCode == GLFW.GLFW_KEY_ENTER){
            confirm();
            return true;
        }
        if (keyCode == 256 && this.shouldCloseOnEsc()){
            this.onClose();
            return true;
        }
        return nameField.keyPressed(keyCode, scanCode, modifiers);
    }

    private void confirm(){
        AllPackets.channel.sendToServer(new ConfigureRedstoneDetectorSignalPacket(blockPos, nameField.getValue()));
        onClose();
    }
}
