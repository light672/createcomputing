package com.lightdev6.computing.block.advanced;

import com.lightdev6.computing.Computing;
import com.lightdev6.computing.block.entity.BlockEntities;
import com.lightdev6.computing.block.entity.ComputerBlockEntity;
import com.lightdev6.cscript.CScript;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class Computer extends Block implements EntityBlock {


    public Computer(Properties properties) {
        super(properties);
    }


    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult result) {
        //called on right click
        if(!level.isClientSide && hand.equals(InteractionHand.MAIN_HAND)){
            ItemStack item = player.getMainHandItem();
            String itemName = item.getHoverName().getString();

            //Computing.runProgram(itemName, Vec3.atCenterOf(blockPos), player);
            if (level.getBlockEntity(blockPos) instanceof ComputerBlockEntity computer){
                if(item.getItem().equals(Items.STICK)){
                    Computing.runProgram(computer.getScript(), Vec3.atCenterOf(blockPos), player);
                } else {
                    computer.setScript(itemName);
                    player.sendSystemMessage(Component.literal("Set script"));
                }
            }


        }

        return super.use(blockState, level, blockPos, player, hand, result);
    }



    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return BlockEntities.COMPUTER.get().create(blockPos, blockState);
    }


}
