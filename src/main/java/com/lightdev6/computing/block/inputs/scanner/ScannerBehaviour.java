package com.lightdev6.computing.block.inputs.scanner;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;

public class ScannerBehaviour extends BeltProcessingBehaviour {
    public static final int CYCLE = 240;
    public static final int ENTITY_SCAN = 10;
    public ScanningBehaviorSpecifics specifics;
    public int prevRunningTicks;
    public int runningTicks;
    public boolean running;
    public boolean finished;

    public interface ScanningBehaviorSpecifics {
        public boolean scanOnBelt(TransportedItemStack itemStack);
        public float getKineticSpeed();
    }

    public <T extends SmartBlockEntity & ScanningBehaviorSpecifics> ScannerBehaviour(T be){
        super(be);
        this.specifics = be;
        whenItemEnters((s,i) -> BeltScannerCallbacks.onItemReceived(s, i, this));
        whileItemHeld((s,i) -> BeltScannerCallbacks.whenItemHeld(s, i, this));
    }

    @Override
    public void read(CompoundTag nbt, boolean clientPacket) {
        running = nbt.getBoolean("Running");
        finished = nbt.getBoolean("Finished");
        prevRunningTicks = runningTicks = nbt.getInt("Ticks");
        super.read(nbt, clientPacket);
    }


    @Override
    public void write(CompoundTag nbt, boolean clientPacket) {
        nbt.putBoolean("Running", running);
        nbt.putBoolean("Finished", finished);
        nbt.putInt("Ticks", runningTicks);
        super.write(nbt, clientPacket);
    }

    @Override
    public void tick() {
        super.tick();

        Level level = getWorld();
        BlockPos worldPosition = getPos();

        if (level.isClientSide && runningTicks == -CYCLE / 2) {
            prevRunningTicks = CYCLE / 2;
            return;
        }

        if (runningTicks == CYCLE / 2 && specifics.getKineticSpeed() != 0) {
            if (level.getBlockState(worldPosition.below(2))
                    .getSoundType() == SoundType.WOOL)
                AllSoundEvents.MECHANICAL_PRESS_ACTIVATION_ON_BELT.playOnServer(level, worldPosition);
            else
                AllSoundEvents.MECHANICAL_PRESS_ACTIVATION.playOnServer(level, worldPosition, .5f,
                        .75f + (Math.abs(specifics.getKineticSpeed()) / 1024f));

            if (!level.isClientSide)
                blockEntity.sendData();
        }

        if (!level.isClientSide && runningTicks > CYCLE) {
            finished = true;
            running = false;
            blockEntity.sendData();
            return;
        }

        prevRunningTicks = runningTicks;
        runningTicks += getRunningTickSpeed();
        if (prevRunningTicks < CYCLE / 2 && runningTicks >= CYCLE / 2) {
            runningTicks = CYCLE / 2;
            // Pause the ticks until a packet is received
            if (level.isClientSide && !blockEntity.isVirtual())
                runningTicks = -(CYCLE / 2);
        }
    }

    public float getRenderedHeadOffset(float partialTicks) {
        /*if (!running)
            return 0;
        int runningTicks = Math.abs(this.runningTicks);
        float ticks = Mth.lerp(partialTicks, prevRunningTicks, runningTicks);
        if (runningTicks < (CYCLE * 2) / 3) {
            System.out.println("Going down!");
            return (float) Mth.clamp(Math.pow(ticks / CYCLE * 2, 3), 0, 1);
        }
        System.out.println("Going down!");
        return Mth.clamp((CYCLE - ticks) / CYCLE * 3, 0, 1);*/

        if (!running)
            return 0;
        int runningTicks = Math.abs(this.runningTicks);
        float ticks = Mth.lerp(partialTicks, prevRunningTicks, runningTicks);
        if (runningTicks < (CYCLE * 2) / 3) {
            return (float) Mth.clamp(Math.pow(ticks / CYCLE * 2, 3), 0, 1);
        }
        return Mth.clamp((CYCLE - ticks) / CYCLE * 3, 0, 1);
    }

    public int getRunningTickSpeed() {
        float speed = specifics.getKineticSpeed();
        if (speed == 0)
            return 0;
        return (int) Mth.lerp(Mth.clamp(Math.abs(speed) / 512f, 0, 1), 1, 60);
    }

    public void start(){
        running = true;
        prevRunningTicks = 0;
        runningTicks = 0;
        blockEntity.sendData();
    }
}
