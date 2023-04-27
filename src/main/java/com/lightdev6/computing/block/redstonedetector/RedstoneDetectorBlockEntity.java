package com.lightdev6.computing.block.redstonedetector;

import com.lightdev6.computing.AllTileEntities;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class RedstoneDetectorBlockEntity extends KineticTileEntity {
    private String signalName = "";

    public RedstoneDetectorBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
        super(AllTileEntities.REDSTONE_DETECTOR.get(), blockPos, blockState);
    }


    public void setSignalName(String signalName){this.signalName = signalName;}

    public String getSignalName(){return signalName;}

    @Override
    public boolean isSpeedRequirementFulfilled() {
        return super.isSpeedRequirementFulfilled();
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        this.signalName = compound.getString("SignalName");
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putString("SignalName", this.signalName);
    }

    @Override
    protected void addStressImpactStats(List<Component> tooltip, float stressAtBase) {
        super.addStressImpactStats(tooltip, stressAtBase);
    }

    public void tick(){
        //this does not call on the client btw

    }

}
