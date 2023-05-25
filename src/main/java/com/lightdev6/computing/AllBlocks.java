package com.lightdev6.computing;


import static com.simibubi.create.content.redstone.displayLink.AllDisplayBehaviours.assignDataBehaviour;


import com.lightdev6.computing.block.computer.ComputerBlock;
import com.lightdev6.computing.block.computer.FrequencyDisplaySource;
import com.lightdev6.computing.block.computer.TerminalDisplaySource;
import com.lightdev6.computing.block.inputs.redstonedetector.RedstoneDetectorBlock;
import com.lightdev6.computing.block.inputs.redstonedetector.RedstoneDetectorItem;
import com.lightdev6.computing.block.inputs.scanner.ScannerBlock;
import com.lightdev6.computing.block.inputs.scanner.ScannerItem;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import static com.lightdev6.computing.Computing.REGISTRATE;

public class AllBlocks {
    static {
        REGISTRATE.creativeModeTab(() -> CreativeModeTab.TAB_REDSTONE);
    }


    public static final BlockEntry<ComputerBlock> COMPUTER = REGISTRATE
            .block("computer", ComputerBlock::new)
            .initialProperties(() -> Blocks.STONE)
            .transform(pickaxe())
            .transform(BlockStressDefaults.setImpact(12))
            .onRegister(assignDataBehaviour(new TerminalDisplaySource(), "terminal"))
            .onRegister(assignDataBehaviour(new FrequencyDisplaySource(), "frequency"))
            .simpleItem()
            .properties(BlockBehaviour.Properties::noOcclusion)
            .register();

    public static final BlockEntry<RedstoneDetectorBlock> REDSTONE_DETECTOR = REGISTRATE
            .block("redstone_detector", RedstoneDetectorBlock::new)
            .initialProperties(() -> Blocks.STONE)
            .transform(pickaxe())
            .item(RedstoneDetectorItem::new)
            .transform(customItemModel())
            .register();
    public static final BlockEntry<ScannerBlock> SCANNER = REGISTRATE
            .block("scanner", ScannerBlock::new)
            .initialProperties(() -> Blocks.STONE)
            .transform(pickaxe())
            .properties(BlockBehaviour.Properties::noOcclusion)
            .blockstate(BlockStateGen.horizontalBlockProvider(true))
            .item(ScannerItem::new)
            .transform(customItemModel())
            .register();

    public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> pickaxe() {
        return b -> b.tag(BlockTags.MINEABLE_WITH_PICKAXE);
    }
    public static <I extends BlockItem, P> NonNullFunction<ItemBuilder<I, P>, P> customItemModel() {
        return b -> b.model(AssetLookup::customItemModel)
                .build();
    }



    public static void register(){}



}
