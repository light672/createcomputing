package com.lightdev6.computing.block.inputs.scanner;

import com.lightdev6.computing.AllBlockEntities;
import com.lightdev6.computing.Computing;
import com.lightdev6.computing.block.computer.ComputerBlockEntity;
import com.lightdev6.computing.block.inputs.IInputBlockEntity;
import com.lightdev6.zinc.ZincStructureConversionObject;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScannerBlockEntity extends KineticBlockEntity implements ScannerBehaviour.ScanningBehaviorSpecifics, IInputBlockEntity {
    public ScannerBehaviour processingBehaviour;
    private BlockPos targetPos = getBlockPos();
    private String signalName = "";
    public ScannerBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(AllBlockEntities.SCANNER.get(), pos, state);
    }
    @Override
    public void setTargetPos(BlockPos targetPos){
        this.targetPos = targetPos;
    }
    @Override
    public BlockPos getTargetPos(){
        return targetPos;
    }
    @Override
    public String getSignalName() {
        return signalName;
    }
    @Override
    public void setSignalName(String signalName) {
        this.signalName = signalName;
    }
    @Override
    public SyncedBlockEntity getBlockEntity() {
        return this;
    }



    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        signalName = compound.getString("SignalName");
        targetPos = new BlockPos(compound.getInt("TX"),compound.getInt("TY"),compound.getInt("TZ"));
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putString("SignalName", this.signalName);
        compound.putInt("TX", getTargetPos().getX());
        compound.putInt("TY", getTargetPos().getY());
        compound.putInt("TZ", getTargetPos().getZ());
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        processingBehaviour = new ScannerBehaviour(this);
        behaviours.add(processingBehaviour);
    }


    @Override
    public boolean scanOnBelt(TransportedItemStack itemStack) {
        if (level.getBlockEntity(targetPos) instanceof ComputerBlockEntity computer) {
            Map<String, Object> fields = new HashMap<>();
            fields.put("id", ForgeRegistries.ITEMS.getKey(itemStack.stack.getItem()).toString());
            fields.put("name", itemStack.stack.getHoverName().getString());
            fields.put("count", (double)itemStack.stack.getCount());
            Computing.runFunctionProgram(signalName, Arrays.asList(new ZincStructureConversionObject("Item", fields)), computer.getScript(), computer);
        }
        System.out.println(itemStack.stack.getHoverName());
        return true;
    }

    @Override
    public float getKineticSpeed() {
        return getSpeed();
    }
}
