package com.lightdev6.computing;

import com.lightdev6.computing.block.computer.ComputerBlockEntity;
import com.lightdev6.computing.block.computer.ComputerRenderer;
import com.simibubi.create.content.contraptions.components.millstone.MillStoneCogInstance;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import static com.lightdev6.computing.Computing.REGISTRATE;
public class AllTileEntities {
    public static final BlockEntityEntry<ComputerBlockEntity> COMPUTER = REGISTRATE
            .tileEntity("computer", ComputerBlockEntity::new)
            .validBlocks(AllBlocks.COMPUTER)
            .renderer(() -> ComputerRenderer::new)
            .register();


    public static void register(){}
}
