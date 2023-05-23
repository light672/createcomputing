package com.lightdev6.computing.packets;

import com.lightdev6.computing.block.computer.ComputerBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ConfigureComputerScriptPacket extends BlockEntityConfigurationPacket<ComputerBlockEntity> {
    private String script;
    public ConfigureComputerScriptPacket(FriendlyByteBuf buffer) {
        super(buffer);
    }

    public ConfigureComputerScriptPacket(BlockPos pos, String script){
        super(pos);
        this.script = script;
    }

    @Override
    protected void writeSettings(FriendlyByteBuf buffer) {
        CompoundTag tag = new CompoundTag();
        tag.putString("Script", this.script);
        buffer.writeNbt(tag);
    }

    @Override
    protected void readSettings(FriendlyByteBuf buffer) {
        this.script = buffer.readNbt().getString("Script");
    }

    @Override
    protected void applySettings(ComputerBlockEntity computerBlock) {
        computerBlock.setScript(this.script);
        computerBlock.sendData();
    }

}
