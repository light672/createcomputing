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
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ComputerBlockEntity extends KineticTileEntity {

    private String script = "";
    private String terminal = "";
    private boolean running = false;
    private List<String> displayFreqs = new ArrayList<>(Arrays.asList("", "", "", "", "", ""));

    private Environment globals = Environment.defaultGlobals(this);

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
    public void setDisplayFreqs(List<String> displayFreqs){
        this.displayFreqs = displayFreqs;
    }
    public List<String> getDisplayFreqs(){
        return this.displayFreqs;
    }
    public void setDisplayFreq(String displayFreq, int index){
        displayFreqs.set(index, displayFreq);
    }
    public String getDisplayFreq(int index){
        return displayFreqs.get(index);
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
        this.displayFreqs = getDisplayFrequenciesFromCompound(compound);
        this.globals = defineEnvironmentFromTag(compound, this);
    }
    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putString("Script", this.script);
        compound.putString("Terminal", this.terminal);
        compound.putBoolean("Running", this.running);
        compound.put("Memory", environmentToTag(getGlobals()));
        compound.put("DisplayFreqs", listToListTag(displayFreqs));

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
    private static Environment defineEnvironmentFromTag(CompoundTag compound, ComputerBlockEntity computer){
        Environment environment = Environment.defaultGlobals(computer);
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

    private static ListTag listToListTag(List<String> list){
        ListTag listTag = new ListTag();
        for (int i = 0; i < list.size(); i++) {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putString("Value", list.get(i));
            listTag.add(compoundTag);
        }

        return listTag;
    }

    private static List<String> getDisplayFrequenciesFromCompound(CompoundTag compound){
        ListTag listTag = compound.getList("DisplayFreqs", Tag.TAG_COMPOUND);
        List<String> list = new ArrayList<>(Arrays.asList("", "", "", "", "", ""));
        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag compoundTag = listTag.getCompound(i);
            list.set(i, compoundTag.getString("Value"));
        }
        return list;
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
