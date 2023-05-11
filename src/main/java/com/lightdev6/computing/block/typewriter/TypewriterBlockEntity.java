package com.lightdev6.computing.block.typewriter;

import com.lightdev6.computing.AllTileEntities;
import com.simibubi.create.foundation.tileEntity.SyncedTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TypewriterBlockEntity extends SyncedTileEntity {
    private String script = "";
    private ItemStack plate = ItemStack.EMPTY;

    public TypewriterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(AllTileEntities.TYPEWRITER.get(), pos, state);
    }

    public void setScript(String script){
        this.script = script;
    }
    public String getScript(){
        return script;
    }

    public void setPlate(ItemStack plate){
        this.plate = plate;
    }

    public ItemStack getPlate(){
        return plate;
    }

    public void givePlateToPlayer(ServerPlayer player){
        if (player.getMainHandItem().isEmpty()){
            player.setItemInHand(InteractionHand.MAIN_HAND, getPlate());
        }
        setPlate(ItemStack.EMPTY);
        Typewriter.resetPlateState(level, getBlockPos(), getBlockState(), false);
    }



    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if(pTag.contains("Plate", 10)) {
            plate = ItemStack.of(pTag.getCompound("Plate"));
        } else {
            plate = ItemStack.EMPTY;
        }


    }


    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (!this.getPlate().isEmpty()) {
            pTag.put("Plate", this.getPlate().save(new CompoundTag()));
        }
    }
}
