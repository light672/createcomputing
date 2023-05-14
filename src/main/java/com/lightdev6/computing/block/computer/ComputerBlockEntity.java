package com.lightdev6.computing.block.computer;

import com.lightdev6.computing.AllTileEntities;
import com.lightdev6.computing.Computing;
import com.lightdev6.computing.Location;
import com.lightdev6.zinc.Environment;
import com.lightdev6.zinc.ZincInstance;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Map;

public class ComputerBlockEntity extends KineticTileEntity {

    private String script = "";
    private String terminal = "";
    private boolean running = false;

    private Environment globals = Environment.defaultGlobals();

    private final Location location = new Location(getBlockPos(), getLevel());

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
    public void setRunning(boolean running){
        this.running = running;
    }
    public boolean getRunning(){
        return running;
    }
    public void setGlobals(Environment globals){
        this.globals = globals;
    }
    public Environment getGlobals(){
        return globals;
    }
    public Location getLocation(){
        return location;
    }


    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        this.script = compound.getString("Script");
        this.terminal = compound.getString("Terminal");
        this.running = compound.getBoolean("Running");
        this.globals = defineEnvironmentFromTag(compound);
    }
    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putString("Script", this.script);
        compound.putString("Terminal", this.terminal);
        compound.putBoolean("Running", this.running);
        compound.put("Memory", environmentToTag(getGlobals()));
    }

    @Override
    public void tick() {
        super.tick();
        if (!isSpeedRequirementFulfilled() && getRunning()){
            stop();
            setTerminal(getTerminal() + "ERROR: Speed requirement not fulfilled, stopped program." + "\n");
        }
    }

    private static ListTag environmentToTag(Environment environment){
        ListTag listTag = new ListTag();
        for (Map.Entry<String, Object> entry : environment.getValues().entrySet()){
            CompoundTag compoundTag = new CompoundTag();
            if (entry.getValue() instanceof Double d){
                compoundTag.putString("Identifier", entry.getKey());
                compoundTag.putDouble("Value", d);
                listTag.add(compoundTag);
            } else if (entry.getValue() instanceof String s){
                compoundTag.putString("Identifier", entry.getKey());
                compoundTag.putString("Value", s);
                listTag.add(compoundTag);
            } else if (entry.getValue() instanceof ZincInstance){
                //handle classes
            }
        }
        return listTag;
    }
    private static Environment defineEnvironmentFromTag(CompoundTag compound){
        Environment environment = Environment.defaultGlobals();
        ListTag listTag = compound.getList("Memory", Tag.TAG_COMPOUND);

        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag compoundTag = listTag.getCompound(i);
            String name = compoundTag.getString("Identifier");
            Object value;
            Byte b = compoundTag.getTagType("Value");
            if (b == Tag.TAG_DOUBLE){
                value = compoundTag.getDouble("Value");
            } else if (b == Tag.TAG_STRING){
                value = compoundTag.getString("Value");
            } else if (b == Tag.TAG_COMPOUND){
                //handle classes
                value = null;
            } else {
                value = null;
            }
            environment.define(name, value);
        }
        return environment;
    }


    public void stop() {
        System.out.println(Computing.runningPrograms.get(getLocation()));
        if (Computing.runningPrograms.containsKey(getLocation())) {
            Computing.runningPrograms.get(getLocation()).interpreter.stopRequested = true;
            Computing.runningPrograms.remove(getLocation());
        }

        setRunning(false);
    }
}
