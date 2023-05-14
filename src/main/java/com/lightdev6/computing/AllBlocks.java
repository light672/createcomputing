package com.lightdev6.computing;

import com.lightdev6.computing.block.computer.Computer;
import com.lightdev6.computing.block.redstonedetector.RedstoneDetector;
import com.lightdev6.computing.block.redstonedetector.RedstoneDetectorItem;
import com.simibubi.create.foundation.block.BlockStressDefaults;
import com.simibubi.create.foundation.data.AssetLookup;
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


    public static final BlockEntry<Computer> COMPUTER = REGISTRATE
            .block("computer", Computer::new)
            .initialProperties(() -> Blocks.STONE)
            .transform(pickaxe())
            .transform(BlockStressDefaults.setImpact(12))
            .simpleItem()
            .properties(BlockBehaviour.Properties::noOcclusion)
            .register();

    public static final BlockEntry<RedstoneDetector> REDSTONE_DETECTOR = REGISTRATE
            .block("redstone_detector", RedstoneDetector::new)
            .initialProperties(() -> Blocks.STONE)
            .transform(pickaxe())
            .properties(BlockBehaviour.Properties::noOcclusion)
            .item(RedstoneDetectorItem::new)
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
