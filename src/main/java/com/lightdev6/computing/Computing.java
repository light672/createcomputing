package com.lightdev6.computing;

import com.lightdev6.computing.block.computer.ComputerBlockEntity;
import com.lightdev6.zinc.Zinc;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.CompletableFuture;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(Computing.MOD_ID)
public class Computing {
    public static final String MOD_ID = "computing";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);



    public Computing() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        REGISTRATE.registerEventListeners(modEventBus);
        AllBlocks.register();
        AllBlockEntities.register();
        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }


    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
        AllPackets.registerPackets();
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Loaded Computing");
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }
    }

    public static void runProgram(String source, ComputerBlockEntity computer) {
        Zinc program = new Zinc(computer);
        CompletableFuture.runAsync(() -> program.run(source));
    }
    public static void runFunctionProgram(String function, List<Object> arguments, String source, ComputerBlockEntity computer){
        Zinc program = new Zinc(computer);
        CompletableFuture.runAsync(() -> program.runFunction(source, function, arguments));
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
