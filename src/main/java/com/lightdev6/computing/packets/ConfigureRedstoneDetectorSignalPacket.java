package com.lightdev6.computing.packets;

import com.lightdev6.computing.block.redstonedetector.RedstoneDetectorBlockEntity;
import com.simibubi.create.foundation.networking.TileEntityConfigurationPacket;
import com.simibubi.create.foundation.tileEntity.SyncedTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class ConfigureRedstoneDetectorSignalPacket extends TileEntityConfigurationPacket<RedstoneDetectorBlockEntity> {
    private String signalName;
    public ConfigureRedstoneDetectorSignalPacket(BlockPos pos, String signalName) {
        super(pos);
        this.signalName = signalName;
    }

    public ConfigureRedstoneDetectorSignalPacket(FriendlyByteBuf buffer){
        super(buffer);
    }



    @Override
    protected void writeSettings(FriendlyByteBuf buffer) {
        System.out.println("rain drop falling from da ceilin");
        CompoundTag tag = new CompoundTag();
        tag.putString("SignalName", this.signalName);
        buffer.writeNbt(tag);
    }

    @Override
    protected void readSettings(FriendlyByteBuf buffer) {
        this.signalName = buffer.readNbt().getString("SignalName");
    }

    @Override
    protected void applySettings(RedstoneDetectorBlockEntity redstoneDetector) {
        redstoneDetector.setSignalName(this.signalName);
        redstoneDetector.sendData();
    }
}
