package com.lightdev6.computing;

import com.lightdev6.computing.block.computer.Computer;
import com.simibubi.create.content.AllSections;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import static com.lightdev6.computing.Computing.REGISTRATE;

public class AllBlocks {
    static {
        REGISTRATE.creativeModeTab(() -> CreativeModeTab.TAB_REDSTONE);
    }


    public static final BlockEntry<Computer> COMPUTER = REGISTRATE
            .block("computer", Computer::new)
            .initialProperties(() -> Blocks.STONE)
            .transform(pickaxe())
            .simpleItem()
            .register();

    public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> pickaxe() {
        return b -> b.tag(BlockTags.MINEABLE_WITH_PICKAXE);
    }

    public static void register(){}
}
