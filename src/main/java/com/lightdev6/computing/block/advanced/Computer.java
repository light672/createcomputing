package com.lightdev6.computing.block.advanced;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.awt.*;

public class Computer extends Block {

    String playerName;

    public Computer(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity) {
        if(entity instanceof Player player) {
            if (playerName.length() == 0){
                playerName = player.getDisplayName().getString();
            }
            player.sendSystemMessage(Component.literal(playerName));
        }
        super.stepOn(level, blockPos, blockState, entity);
    }
}
