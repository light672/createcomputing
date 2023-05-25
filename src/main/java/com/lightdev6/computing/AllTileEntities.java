package com.lightdev6.computing;

import com.lightdev6.computing.block.computer.ComputerBlockEntity;
import com.lightdev6.computing.block.computer.ComputerInstance;
import com.lightdev6.computing.block.computer.ComputerRenderer;
import com.lightdev6.computing.block.inputs.redstonedetector.RedstoneDetectorBlockEntity;
import com.lightdev6.computing.block.inputs.scanner.ScannerBlockEntity;
import com.lightdev6.computing.block.inputs.scanner.ScannerInstance;
import com.lightdev6.computing.block.inputs.scanner.ScannerRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import static com.lightdev6.computing.Computing.REGISTRATE;
public class AllTileEntities {
    public static final BlockEntityEntry<ComputerBlockEntity> COMPUTER = REGISTRATE
            .blockEntity("computer", ComputerBlockEntity::new)
            .instance(() -> ComputerInstance::new)
            .validBlocks(AllBlocks.COMPUTER)
            .renderer(() -> ComputerRenderer::new)
            .register();

    public static final BlockEntityEntry<RedstoneDetectorBlockEntity> REDSTONE_DETECTOR = REGISTRATE
            .blockEntity("redstone_detector", RedstoneDetectorBlockEntity::new)
            .validBlocks(AllBlocks.REDSTONE_DETECTOR)
            .register();
    public static final BlockEntityEntry<ScannerBlockEntity> SCANNER = REGISTRATE
            .blockEntity("scanner", ScannerBlockEntity::new)
            .instance(() -> ScannerInstance::new)
            .validBlocks(AllBlocks.SCANNER)
            .renderer(() -> ScannerRenderer::new)
            .register();
    public static void register(){}
}
