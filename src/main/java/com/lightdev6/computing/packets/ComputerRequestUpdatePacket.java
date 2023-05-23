package com.lightdev6.computing.packets;

import com.lightdev6.computing.block.computer.ComputerBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class ComputerRequestUpdatePacket extends BlockEntityConfigurationPacket<ComputerBlockEntity> {

    public ComputerRequestUpdatePacket(FriendlyByteBuf buffer) {
        super(buffer);
    }

    public ComputerRequestUpdatePacket(BlockPos pos) {
        super(pos);
    }

    @Override
    protected void writeSettings(FriendlyByteBuf buffer) {

    }

    @Override
    protected void readSettings(FriendlyByteBuf buffer) {

    }

    @Override
    protected void applySettings(ComputerBlockEntity computerBlock) {

    }
}
