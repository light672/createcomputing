package com.lightdev6.computing.block.inputs;

import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IInputBlockEntity{
    String getSignalName();
    void setSignalName(String signalName);
    BlockPos getTargetPos();
    void setTargetPos(BlockPos targetPos);
    SyncedBlockEntity getBlockEntity();
}
