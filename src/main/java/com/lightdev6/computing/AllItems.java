package com.lightdev6.computing;
import com.lightdev6.computing.item.AndesitePlate;
import com.lightdev6.computing.item.BrassPlate;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import static com.lightdev6.computing.Computing.REGISTRATE;
public class AllItems {

    static {
        REGISTRATE.creativeModeTab(() -> CreativeModeTab.TAB_REDSTONE);
    }

    public static final ItemEntry<AndesitePlate> ANDESITE_PLATE = REGISTRATE
            .item("andesite_plate", AndesitePlate::new)
            .properties(p -> p.rarity(Rarity.UNCOMMON).stacksTo(1))
            .register();
    public static final ItemEntry<BrassPlate> BRASS_PLATE = REGISTRATE
            .item("brass_plate", BrassPlate::new)
            .properties(p -> p.rarity(Rarity.UNCOMMON).stacksTo(1))
            .register();


    public static void register(){}
}
