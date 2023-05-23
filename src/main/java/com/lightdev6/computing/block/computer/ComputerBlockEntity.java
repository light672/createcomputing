package com.lightdev6.computing.block.computer;

import com.lightdev6.computing.AllTileEntities;
import com.lightdev6.computing.Computing;
import com.lightdev6.computing.Location;
import com.lightdev6.zinc.*;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import org.checkerframework.checker.units.qual.C;

import java.util.*;

public class ComputerBlockEntity extends KineticBlockEntity {

    private static final int NEIGHBOUR_CHECKING = 100;
    private int neighbourCheckCooldown;

    private String script = "";
    private String terminal = "";
    private boolean running = false;
    private List<String> displayFreqs = new ArrayList<>(Arrays.asList("", "", "", "", "", ""));

    private Environment globals = Environment.defaultGlobals(this);

    private final Location location = new Location(getBlockPos(), getLevel());

    private LinkedHashSet<LazyOptional<IItemHandler>> attachedInventories;

    public ComputerBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
        super(AllTileEntities.COMPUTER.get(), blockPos, blockState);
        attachedInventories = new LinkedHashSet<>();

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




    public void findInventories(){
        attachedInventories.clear();

        for (Direction facing : Iterate.directions){
            if (!level.isLoaded(worldPosition.relative(facing)))
                continue;
            BlockEntity tileEntity = level.getBlockEntity(worldPosition.relative(facing));

            if (tileEntity != null){
                LazyOptional<IItemHandler> capability = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
                if (capability.isPresent()){
                    attachedInventories.add(capability);
                }
            }
        }
    }

    public Object readPlate(String identifier){
        attachedInventories.removeIf(cap -> !cap.isPresent());
        for (LazyOptional<IItemHandler> cap : attachedInventories){
            IItemHandler iItemHandler = cap.orElse(EmptyHandler.INSTANCE);
            for (int i = 0; i < iItemHandler.getSlots(); i++) {
                ItemStack currentStack = iItemHandler.getStackInSlot(i);
                if (currentStack.getItem() == AllItems.WRENCH.get() && currentStack.hasTag() && currentStack.getTag().getString("Identifier").equals(identifier)){
                    CompoundTag compoundTag = currentStack.getTag();
                    Object value;
                    Byte b = compoundTag.getTagType("Value");
                    if (b == Tag.TAG_DOUBLE) {
                        value = compoundTag.getDouble("Value");
                    } else if (b == Tag.TAG_STRING) {
                        value = compoundTag.getString("Value");
                    } else if (b == Tag.TAG_BYTE) {
                        value = compoundTag.getBoolean("Value");
                    } else {
                        value = null;
                    }
                    return value;
                }
            }
        }
        return null;
    }

    public boolean findAndModifyPlate(String identifier, Object data){
        attachedInventories.removeIf(cap -> !cap.isPresent());
        int firstEmptyAvailableSlot = -1;
        IItemHandler firstEmptyAvailableSlotIItemHandler = null;
        int matchingIdentifierSlot = -1;
        IItemHandler matchingIdentifierSlotIItemHandler = null;
        for (LazyOptional<IItemHandler> cap : attachedInventories){
            IItemHandler iItemHandler = cap.orElse(EmptyHandler.INSTANCE);
            for (int i = 0; i < iItemHandler.getSlots(); i++) {
                ItemStack currentStack = iItemHandler.getStackInSlot(i);
                if (currentStack.getItem() == AllItems.WRENCH.get()){
                    if (!currentStack.hasTag()){
                        //iItemHandler.insertItem(i, writeToPlate(identifier,data,iItemHandler.extractItem(i,1, false)), false);
                        if (firstEmptyAvailableSlot == -1) {
                            firstEmptyAvailableSlot = i;
                            firstEmptyAvailableSlotIItemHandler = iItemHandler;
                        }
                    } else if (currentStack.getTag().getString("Identifier").equals(identifier)){
                        //iItemHandler.insertItem(i, writeToPlate(identifier,data,iItemHandler.extractItem(i,1, false)), false);
                        if (matchingIdentifierSlot == -1) {
                            matchingIdentifierSlot = i;
                            matchingIdentifierSlotIItemHandler = iItemHandler;
                        }
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
            }
        }
        if (matchingIdentifierSlot != -1){
            matchingIdentifierSlotIItemHandler.insertItem(
                    matchingIdentifierSlot, writeToPlate(
                            identifier,data,matchingIdentifierSlotIItemHandler.extractItem(matchingIdentifierSlot,1, false)
                    ),
                    false);
            return true;
        } else if (firstEmptyAvailableSlot != -1){
            firstEmptyAvailableSlotIItemHandler.insertItem(
                    firstEmptyAvailableSlot, writeToPlate(
                            identifier,data,firstEmptyAvailableSlotIItemHandler.extractItem(firstEmptyAvailableSlot,1, false)
                    ),
                    false);
            return true;
        }
        return false;
    }

    private ItemStack writeToPlate(String identifier, Object data, ItemStack modifiablePlate){

        CompoundTag compoundTag = new CompoundTag();
        if (data instanceof Double d){
            compoundTag.putString("Identifier", identifier);
            compoundTag.putDouble("Value", d);
            modifiablePlate.setTag(compoundTag);
            modifiablePlate.setHoverName(Component.literal(identifier).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.ITALIC));
        } else if (data instanceof String s){
            compoundTag.putString("Identifier", identifier);
            compoundTag.putString("Value", s);
            modifiablePlate.setTag(compoundTag);
            modifiablePlate.setHoverName(Component.literal(identifier).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.ITALIC));
        } else if (data instanceof Boolean b){
            compoundTag.putString("Identifier", identifier);
            compoundTag.putBoolean("Value", b);
            modifiablePlate.setTag(compoundTag);
            modifiablePlate.setHoverName(Component.literal(identifier).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.ITALIC));
        }
        return modifiablePlate;
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

        if (neighbourCheckCooldown-- <= 0){
            neighbourCheckCooldown = NEIGHBOUR_CHECKING;
            findInventories();
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
            } else if (entry.getValue() instanceof Boolean b){
                compoundTag.putString("Identifier", entry.getKey());
                compoundTag.putBoolean("Value", b);
                listTag.add(compoundTag);
            } else if (entry.getValue() instanceof ZincObject s){
                compoundTag.putString("Identifier", entry.getKey());
                compoundTag.put("Value", structObjectToTag(s));
                compoundTag.putString("StructureIdentifier", s.getStructure().getName());
                listTag.add(compoundTag);
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
            if (b == Tag.TAG_DOUBLE) {
                value = compoundTag.getDouble("Value");
            } else if (b == Tag.TAG_STRING) {
                value = compoundTag.getString("Value");
            } else if (b == Tag.TAG_BYTE) {
                value = compoundTag.getBoolean("Value");
            } else if (b == Tag.TAG_LIST){
                value = tagToStructObject(compoundTag);
            } else {
                value = null;
            }
            environment.define(name, value);
        }
        return environment;
    }

    private static ListTag structObjectToTag(ZincObject struct){
        ListTag listTag = new ListTag();
        for (Map.Entry<String, Object> entry : struct.getFields().entrySet()){
            CompoundTag entryTag = new CompoundTag();
            if (entry.getValue() instanceof Double d){
                entryTag.putString("Identifier", entry.getKey());
                entryTag.putDouble("Value", d);
                listTag.add(entryTag);
            } else if (entry.getValue() instanceof String s){
                entryTag.putString("Identifier", entry.getKey());
                entryTag.putString("Value", s);
                listTag.add(entryTag);
            } else if (entry.getValue() instanceof Boolean b){
                entryTag.putString("Identifier", entry.getKey());
                entryTag.putBoolean("Value", b);
            } else if (entry.getValue() instanceof ZincObject s){
                entryTag.putString("Identifier", entry.getKey());
                entryTag.put("Value",structObjectToTag(s));
            }
        }

        return listTag;
    }

    private static ZincStructureConversionObject tagToStructObject(CompoundTag compound){
        String structureName = compound.getString("StructureIdentifier");
        ListTag listTag = compound.getList("Value", Tag.TAG_COMPOUND);
        Map<String, Object> fields = new HashMap<>();
        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag compoundTag = listTag.getCompound(i);
            String identifier = compoundTag.getString("Identifier");
            Object value;
            Byte b = compoundTag.getTagType("Value");
            if (b == Tag.TAG_DOUBLE){
                value = compoundTag.getDouble("Value");
                fields.put(identifier, value);
            } else if (b == Tag.TAG_STRING){
                value = compoundTag.getString("Value");
                fields.put(identifier, value);
            } else if (b == Tag.TAG_BYTE){
                value = compoundTag.getBoolean("Value");
                fields.put(identifier, value);
            } else if (b == Tag.TAG_LIST){
                value = tagToStructObject(compoundTag);
                fields.put(identifier, value);
            }
        }
        return new ZincStructureConversionObject(structureName, fields);
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
