package com.lightdev6.computing.block.inputs.redstonedetector;

import com.lightdev6.computing.AllTileEntities;
import com.lightdev6.computing.block.inputs.IInputBlockEntity;
import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class RedstoneDetectorBlockEntity extends SyncedBlockEntity implements IInputBlockEntity {
    private String signalName = "";
    private BlockPos targetPos = getBlockPos();

    public RedstoneDetectorBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
        super(AllTileEntities.REDSTONE_DETECTOR.get(), blockPos, blockState);
    }

    @Override
    public void setSignalName(String signalName){this.signalName = signalName;}

    @Override
    public SyncedBlockEntity getBlockEntity() {
        return this;
    }



    @Override
    public String getSignalName(){return signalName;}
    @Override
    public void setTargetPos(BlockPos targetPos){
        this.targetPos = targetPos;
    }
    @Override
    public BlockPos getTargetPos(){
        return targetPos;
    }



    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        signalName = compound.getString("SignalName");
        targetPos = new BlockPos(compound.getInt("TX"),compound.getInt("TY"),compound.getInt("TZ"));
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putString("SignalName", this.signalName);
        compound.putInt("TX", getTargetPos().getX());
        compound.putInt("TY", getTargetPos().getY());
        compound.putInt("TZ", getTargetPos().getZ());

    }

}
