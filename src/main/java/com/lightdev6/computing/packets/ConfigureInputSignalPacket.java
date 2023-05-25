package com.lightdev6.computing.packets;

import com.lightdev6.computing.block.inputs.IInputBlockEntity;
import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class ConfigureInputSignalPacket extends BlockEntityConfigurationPacket<SyncedBlockEntity> {
    private String signalName;
    public ConfigureInputSignalPacket(BlockPos pos, String signalName) {
        super(pos);
        this.signalName = signalName;
    }

    public ConfigureInputSignalPacket(FriendlyByteBuf buffer){
        super(buffer);
    }



    @Override
    protected void writeSettings(FriendlyByteBuf buffer) {
        CompoundTag tag = new CompoundTag();
        tag.putString("SignalName", this.signalName);
        buffer.writeNbt(tag);
    }

    @Override
    protected void readSettings(FriendlyByteBuf buffer) {
        this.signalName = buffer.readNbt().getString("SignalName");
    }

    @Override
    protected void applySettings(SyncedBlockEntity be) {
        if (!(be instanceof IInputBlockEntity input))
            return;
        input.setSignalName(this.signalName);
        input.getBlockEntity().sendData();
    }
}
