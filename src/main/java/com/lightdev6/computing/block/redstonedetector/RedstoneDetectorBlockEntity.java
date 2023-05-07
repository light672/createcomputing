package com.lightdev6.computing.block.redstonedetector;

import com.lightdev6.computing.AllTileEntities;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.foundation.tileEntity.SyncedTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class RedstoneDetectorBlockEntity extends SyncedTileEntity {
    private String signalName = "";
    private BlockPos targetPos = getBlockPos();

    public RedstoneDetectorBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
        super(AllTileEntities.REDSTONE_DETECTOR.get(), blockPos, blockState);
    }


    public void setSignalName(String signalName){this.signalName = signalName;}

    public String getSignalName(){return signalName;}

    public void setTargetPos(BlockPos targetPos){
        this.targetPos = targetPos;
    }

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
