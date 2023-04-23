package com.lightdev6.computing.event;

import com.lightdev6.computing.Computing;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ModEvents {
    @Mod.EventBusSubscriber(modid = Computing.MOD_ID)
    public static class ForgeEvents{
        @SubscribeEvent
        public static void ServerStoppingEvent(ServerStoppingEvent event){
            Computing.runningPrograms.clear();
        }
    }
}
