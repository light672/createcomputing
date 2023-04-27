package com.lightdev6.computing.block.redstonedetector;

import com.lightdev6.computing.AllTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class RedstoneDetector extends Block implements EntityBlock {

    public RedstoneDetector(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return AllTileEntities.REDSTONE_DETECTOR.get().create(blockPos, blockState);
    }
}
