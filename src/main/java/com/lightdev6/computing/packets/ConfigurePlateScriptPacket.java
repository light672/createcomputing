package com.lightdev6.computing.packets;

import com.lightdev6.computing.block.computer.ComputerBlockEntity;
import com.lightdev6.computing.block.typewriter.Typewriter;
import com.lightdev6.computing.block.typewriter.TypewriterBlockEntity;
import com.simibubi.create.foundation.networking.TileEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ConfigurePlateScriptPacket extends TileEntityConfigurationPacket<TypewriterBlockEntity> {
    private String script;
    private BlockPos pos;
    public ConfigurePlateScriptPacket(FriendlyByteBuf buffer) {
        super(buffer);
    }

    public ConfigurePlateScriptPacket(BlockPos pos, String script){
        super(pos);
        this.pos = pos;
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
    protected void applySettings(TypewriterBlockEntity typewriter) {
        ItemStack plate = typewriter.getPlate();
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("Script", script);
        plate.setTag(compoundTag);
        typewriter.setPlate(plate);
        typewriter.sendData();
        typewriter.getLevel().playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }



}
