package com.lightdev6.computing.block.inputs.scanner;

import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;

public class BeltScannerCallbacks {
    public static ProcessingResult onItemReceived(TransportedItemStack s, TransportedItemStackHandlerBehaviour i, ScannerBehaviour behaviour){
        if (behaviour.specifics.getKineticSpeed() == 0)
            return ProcessingResult.PASS;
        if (behaviour.running)
            return ProcessingResult.PASS;
        behaviour.start();
        return ProcessingResult.HOLD;
    }

    public static ProcessingResult whenItemHeld(TransportedItemStack s, TransportedItemStackHandlerBehaviour i, ScannerBehaviour behaviour){
        if (behaviour.specifics.getKineticSpeed() == 0)
            return ProcessingResult.PASS;
        if (!behaviour.running)
            return ProcessingResult.PASS;
        if (behaviour.runningTicks != PressingBehaviour.CYCLE / 2)
            return ProcessingResult.HOLD;

        behaviour.specifics.scanOnBelt(s);

        return ProcessingResult.HOLD;
    }
}
