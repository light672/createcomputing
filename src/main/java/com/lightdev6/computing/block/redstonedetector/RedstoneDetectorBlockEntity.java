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
    private BlockPos targetPos = getBlockPos();

    public RedstoneDetectorBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
        super(AllTileEntities.REDSTONE_DETECTOR.get(), blockPos, blockState);
    }


    public void setSignalName(String signalName){this.signalName = signalName;}

    public String getSignalName(){return signalName;}

    public void setTargetPos(BlockPos targetPos){
        this.targetPos = targetPos;
        sendData();
    }

    public BlockPos getTargetPos(){
        return targetPos;
    }

    @Override
    public boolean isSpeedRequirementFulfilled() {
        return super.isSpeedRequirementFulfilled();
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        signalName = compound.getString("SignalName");
        targetPos = new BlockPos(compound.getInt("TX"),compound.getInt("TY"),compound.getInt("TZ"));
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putString("SignalName", this.signalName);
        compound.putInt("TX", getTargetPos().getX());
        compound.putInt("TY", getTargetPos().getY());
        compound.putInt("TZ", getTargetPos().getZ());

    }

    @Override
    protected void addStressImpactStats(List<Component> tooltip, float stressAtBase) {
        super.addStressImpactStats(tooltip, stressAtBase);
    }

    public void tick(){
        //this does not call on the client btw

    }

}
