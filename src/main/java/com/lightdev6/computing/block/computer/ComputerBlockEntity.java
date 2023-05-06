package com.lightdev6.computing.block.computer;

import com.lightdev6.computing.AllTileEntities;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ComputerBlockEntity extends KineticTileEntity {

    private String script = "";
    private String terminal = "";

    public ComputerBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
        super(AllTileEntities.COMPUTER.get(), blockPos, blockState);
    }



    public void setScript(String script){
        this.script = script;
    }
    public String getScript(){
        return script;
    }
    public void setTerminal(String terminal){
        this.terminal = terminal;
    }
    public String getTerminal(){
        return terminal;
    }
    @Override
    public boolean isSpeedRequirementFulfilled() {return super.isSpeedRequirementFulfilled();}


    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        this.script = compound.getString("Script");
        this.terminal = compound.getString("Terminal");
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putString("Script", this.script);
        compound.putString("Terminal", this.terminal);
    }

    @Override
    protected void addStressImpactStats(List<Component> tooltip, float stressAtBase) {
        super.addStressImpactStats(tooltip, stressAtBase);
    }
}
