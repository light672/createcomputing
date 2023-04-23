package com.lightdev6.computing.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ComputerBlockEntity extends BlockEntity {

    private String script = "";

    public ComputerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntities.COMPUTER.get(), blockPos, blockState);
    }

    public void setScript(String script){
        this.script = script;
    }

    public String getScript(){
        return script;
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.script = nbt.getString("Script");
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putString("Script", this.script);
    }
}
