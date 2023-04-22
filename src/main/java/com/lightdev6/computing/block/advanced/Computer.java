package com.lightdev6.computing.block.advanced;

import com.lightdev6.cscript.CScript;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.awt.*;

public class Computer extends Block {

    String playerName;

    public Computer(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity) {

        super.stepOn(level, blockPos, blockState, entity);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult result) {
        //called on right click
        if(!level.isClientSide && hand.equals(InteractionHand.MAIN_HAND)){
            ItemStack item = player.getMainHandItem();
            String itemName = item.getHoverName().getString();
            //player.sendSystemMessage(Component.literal(itemName));
            new CScript(itemName, player);
        }

        return super.use(blockState, level, blockPos, player, hand, result);
    }
}
