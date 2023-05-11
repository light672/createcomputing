package com.lightdev6.computing.block.typewriter;

import com.lightdev6.computing.AllTileEntities;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.gui.ScreenOpener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import org.apache.logging.log4j.core.jmx.Server;
import org.jetbrains.annotations.Nullable;

public class Typewriter extends Block implements EntityBlock, ITE<TypewriterBlockEntity> {
    public static final BooleanProperty HAS_PLATE = BooleanProperty.create("has_plate");
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public Typewriter(Properties properties){
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(HAS_PLATE, Boolean.valueOf(false)).setValue(FACING, Direction.NORTH));

    }

    @Override
    public InteractionResult use(BlockState pState, Level level, BlockPos blockPos, Player player, InteractionHand pHand, BlockHitResult pHit) {
        if (pState.getValue(HAS_PLATE)){
            if (!player.isShiftKeyDown()) {
                ItemStack held = player.getMainHandItem();
                if (AllItems.WRENCH.isIn(held))
                    return InteractionResult.PASS;
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                        () -> () -> withTileEntityDo(level, blockPos, te -> this.displayScreen(te, player)));
                return InteractionResult.SUCCESS;
            } else if (!level.isClientSide){
                withTileEntityDo(level, blockPos, te -> te.givePlateToPlayer((ServerPlayer) player));
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(pState, level, blockPos, player, pHand, pHit);
    }




    @OnlyIn(value = Dist.CLIENT)
    protected void displayScreen(TypewriterBlockEntity typewriter, Player player){
        if (player instanceof LocalPlayer)
            ScreenOpener.open(new TypewriterScreen(typewriter));
    }

    public static boolean tryPlacePlate(@Nullable Player player, Level level, BlockPos pos, BlockState state, ItemStack plate){
        if (!state.getValue(HAS_PLATE)){
            if (!level.isClientSide){
                placePlate(player, level, pos, state, plate);
            }
            return true;
        } else {
            return false;
        }
    }

    private static void placePlate(@Nullable Player player, Level level, BlockPos pos, BlockState state, ItemStack plate){
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TypewriterBlockEntity typewriter){
            typewriter.setPlate(plate.split(1));
            resetPlateState(level, pos, state, true);
            level.playSound((Player) null, pos, SoundEvents.ARMOR_EQUIP_IRON, SoundSource.BLOCKS,  1.0F, 1.0F);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }
    }

    public static void resetPlateState(Level level, BlockPos pos, BlockState state, boolean hasPlate){
        level.setBlock(pos, state.setValue(HAS_PLATE, Boolean.valueOf(hasPlate)), 3);
        level.updateNeighborsAt(pos.below(), state.getBlock());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(HAS_PLATE);
        pBuilder.add(FACING);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    public BlockState rotate(BlockState pState, Rotation pRotation){
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public Class<TypewriterBlockEntity> getTileEntityClass() {
        return TypewriterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends TypewriterBlockEntity> getTileEntityType() {
        return AllTileEntities.TYPEWRITER.get();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return AllTileEntities.TYPEWRITER.get().create(pPos, pState);
    }
}
