package com.lightdev6.computing;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class Location {
    BlockPos pos;
    Level level;
    public Location(BlockPos pos, Level level){
        this.pos = pos;
        this.level = level;
    }
}
