package com.lightdev6.computing.block.redstonedetector;

import com.lightdev6.computing.AllTileEntities;
import com.lightdev6.computing.Computing;
import com.lightdev6.computing.block.computer.ComputerBlockEntity;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.gui.ScreenOpener;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class RedstoneDetector extends Block implements EntityBlock, ITE<RedstoneDetectorBlockEntity> {

    public RedstoneDetector(Properties properties) {
        super(properties);
    }

    @Override
    public Class<RedstoneDetectorBlockEntity> getTileEntityClass() {
        return RedstoneDetectorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends RedstoneDetectorBlockEntity> getTileEntityType() {
        return AllTileEntities.REDSTONE_DETECTOR.get();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return AllTileEntities.REDSTONE_DETECTOR.get().create(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
        return level.isClientSide() ? null : ($0, blockPos, $1, blockEntity) -> {
            if (blockEntity instanceof RedstoneDetectorBlockEntity redstoneDetector){
                redstoneDetector.tick();
                int powerLevel = level.getSignal(blockPos, Direction.EAST);
            }
        };
    }

    @Override
    public void neighborChanged(BlockState pState, Level level, BlockPos blockPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        super.neighborChanged(pState, level, blockPos, pBlock, pFromPos, pIsMoving);
        if (level.isClientSide()){
            return;
        }
        if (!level.getBlockTicks().willTickThisTick(blockPos, this)) level.scheduleTick(blockPos, this, 0);
    }

    @Override
    public void tick(BlockState blockState, ServerLevel level, BlockPos blockPos, RandomSource randomSource) {
        if (level.isClientSide){
            return;
        }
        getRedstoneSignalUpdate(getPower(level, blockPos), level, blockPos);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult result) {
        /*if (level.isClientSide) return super.use(blockState, level, blockPos, player, hand, result);

        if (!hand.equals(InteractionHand.MAIN_HAND)) return super.use(blockState, level, blockPos, player, hand, result);

        ItemStack item = player.getMainHandItem();
        String itemName = item.getHoverName().getString();

        if (item.getItem().equals(Items.PAPER))
            if (level.getBlockEntity(blockPos) instanceof RedstoneDetectorBlockEntity redstoneDetector){
                redstoneDetector.setSignalName(itemName);
                player.sendSystemMessage(Component.literal("Set signal to: " + itemName));
            }
        */
        ItemStack held = player.getMainHandItem();
        if (AllItems.WRENCH.isIn(held))
            return InteractionResult.PASS;
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> withTileEntityDo(level, blockPos, te -> this.displayScreen(te, player)));


        return InteractionResult.SUCCESS;
    }

    @OnlyIn(value = Dist.CLIENT)
    protected void displayScreen(RedstoneDetectorBlockEntity redstoneDetector, Player player){
        if (player instanceof LocalPlayer)
            ScreenOpener.open(new RedstoneDetectorScreen(redstoneDetector));
    }

    private int getPower(Level world, BlockPos pos){
        int power = 0;
        for (Direction direction : Iterate.directions)
            power = Math.max(world.getSignal(pos.relative(direction),direction), power);
        for (Direction direction : Iterate.directions)
            power = Math.max(world.getSignal(pos.relative(direction), Direction.UP), power);
        return power;
    }

    private void getRedstoneSignalUpdate(int power, ServerLevel level, BlockPos blockPos){
        RedstoneDetectorBlockEntity redstoneDetector = (RedstoneDetectorBlockEntity) level.getBlockEntity(blockPos);
        if (level.getBlockEntity(redstoneDetector.getTargetPos()) instanceof ComputerBlockEntity computer){
            Computing.runFunctionProgram(redstoneDetector.getSignalName(), Arrays.asList((double)power),computer.getScript(), computer);
        }
    }
}
