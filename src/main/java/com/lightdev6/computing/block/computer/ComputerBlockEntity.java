package com.lightdev6.computing.block.computer;

import com.lightdev6.computing.block.BlockEntities;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ComputerBlockEntity extends KineticTileEntity {

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
    public boolean isSpeedRequirementFulfilled() {return super.isSpeedRequirementFulfilled();}


    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        this.script = compound.getString("Script");
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putString("Script", this.script);
    }

    @Override
    protected void addStressImpactStats(List<Component> tooltip, float stressAtBase) {
        super.addStressImpactStats(tooltip, stressAtBase);
    }
}
