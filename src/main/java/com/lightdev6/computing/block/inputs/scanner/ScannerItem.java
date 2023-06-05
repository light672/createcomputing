package com.lightdev6.computing.block.inputs.scanner;

import com.lightdev6.computing.AllPackets;
import com.lightdev6.computing.packets.ScannerPlacementPacket;
import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class ScannerItem extends AssemblyOperatorBlockItem {

    public ScannerItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Player player = pContext.getPlayer();
        if (player != null && player.isShiftKeyDown())
            return InteractionResult.SUCCESS;
        return super.useOn(pContext);
    }

    @Override
    public boolean canAttackBlock(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
        return !pPlayer.isShiftKeyDown();
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pPos, Level pLevel, @Nullable Player pPlayer, ItemStack pStack, BlockState pState) {
        if (!pLevel.isClientSide && pPlayer instanceof ServerPlayer sp)
            //A block has been placed on the server
            AllPackets.channel.send(PacketDistributor.PLAYER.with(() -> sp), new ScannerPlacementPacket.ClientBoundRequest(pPos));
        return super.updateCustomBlockEntityTag(pPos, pLevel, pPlayer, pStack, pState);
    }
}
