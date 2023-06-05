package com.lightdev6.computing.block.inputs.redstonedetector;

import com.lightdev6.computing.AllBlockEntities;
import com.lightdev6.computing.Computing;
import com.lightdev6.computing.block.computer.ComputerBlockEntity;
import com.lightdev6.computing.block.inputs.screens.InputSignalScreen;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.block.IBE;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class RedstoneDetectorBlock extends Block implements EntityBlock, IBE<RedstoneDetectorBlockEntity> {
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    public RedstoneDetectorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(LIT, Boolean.valueOf(false)));
    }


    @Override
    public Class<RedstoneDetectorBlockEntity> getBlockEntityClass() {
        return RedstoneDetectorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends RedstoneDetectorBlockEntity> getBlockEntityType() {
        return AllBlockEntities.REDSTONE_DETECTOR.get();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return AllBlockEntities.REDSTONE_DETECTOR.get().create(blockPos, blockState);
    }


    @Override
    public void neighborChanged(BlockState pState, Level level, BlockPos blockPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        super.neighborChanged(pState, level, blockPos, pBlock, pFromPos, pIsMoving);
        if (level.isClientSide()) return;
        boolean flag = pState.getValue(LIT);
        if (flag != level.hasNeighborSignal(blockPos)){
            level.setBlock(blockPos, pState.cycle(LIT), 3);
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
        ItemStack held = player.getMainHandItem();
        if (AllItems.WRENCH.isIn(held))
            return InteractionResult.PASS;
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> withBlockEntityDo(level, blockPos, te -> this.displayScreen(te, player)));


        return InteractionResult.SUCCESS;
    }

    @OnlyIn(value = Dist.CLIENT)
    protected void displayScreen(RedstoneDetectorBlockEntity redstoneDetector, Player player){
        if (player instanceof LocalPlayer)
            ScreenOpener.open(new InputSignalScreen(redstoneDetector));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(LIT);
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
