package com.lightdev6.computing.packets;

import com.lightdev6.computing.block.computer.ComputerBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class ComputerSendTerminalPacket extends BlockEntityConfigurationPacket<ComputerBlockEntity> {
    private String terminal;
    public ComputerSendTerminalPacket(FriendlyByteBuf buffer) {
        super(buffer);
    }

    public ComputerSendTerminalPacket(BlockPos pos, String terminal){
        super(pos);
        this.terminal = terminal;
    }

    @Override
    protected void writeSettings(FriendlyByteBuf buffer) {
        CompoundTag tag = new CompoundTag();
        tag.putString("Terminal", this.terminal);
        buffer.writeNbt(tag);
    }

    @Override
    protected void readSettings(FriendlyByteBuf buffer) {
        this.terminal = buffer.readNbt().getString("Terminal");
    }

    @Override
    protected void applySettings(ComputerBlockEntity computerBlock) {
        computerBlock.setTerminal(this.terminal);
        computerBlock.sendData();
    }
}
