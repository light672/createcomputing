package com.lightdev6.computing;

import com.lightdev6.computing.packets.ComputerSendRunPacket;
import com.lightdev6.computing.packets.ConfigureComputerScriptPacket;
import com.lightdev6.computing.packets.ConfigureRedstoneDetectorSignalPacket;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import static net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT;
import static net.minecraftforge.network.NetworkDirection.PLAY_TO_SERVER;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.components.structureMovement.ContraptionBlockChangedPacket;
import com.simibubi.create.content.contraptions.components.structureMovement.ContraptionDisassemblyPacket;
import com.simibubi.create.content.contraptions.components.structureMovement.ContraptionRelocationPacket;
import com.simibubi.create.content.contraptions.components.structureMovement.ContraptionStallPacket;
import com.simibubi.create.content.contraptions.components.structureMovement.TrainCollisionPacket;
import com.simibubi.create.content.contraptions.components.structureMovement.gantry.GantryContraptionUpdatePacket;
import com.simibubi.create.content.contraptions.components.structureMovement.glue.GlueEffectPacket;
import com.simibubi.create.content.contraptions.components.structureMovement.glue.SuperGlueRemovalPacket;
import com.simibubi.create.content.contraptions.components.structureMovement.glue.SuperGlueSelectionPacket;
import com.simibubi.create.content.contraptions.components.structureMovement.interaction.controls.ControlsInputPacket;
import com.simibubi.create.content.contraptions.components.structureMovement.interaction.controls.ControlsStopControllingPacket;
import com.simibubi.create.content.contraptions.components.structureMovement.interaction.controls.HonkPacket;
import com.simibubi.create.content.contraptions.components.structureMovement.interaction.controls.TrainHUDUpdatePacket;
import com.simibubi.create.content.contraptions.components.structureMovement.sync.ClientMotionPacket;
import com.simibubi.create.content.contraptions.components.structureMovement.sync.ContraptionFluidPacket;
import com.simibubi.create.content.contraptions.components.structureMovement.sync.ContraptionInteractionPacket;
import com.simibubi.create.content.contraptions.components.structureMovement.sync.ContraptionSeatMappingPacket;
import com.simibubi.create.content.contraptions.components.structureMovement.sync.LimbSwingUpdatePacket;
import com.simibubi.create.content.contraptions.components.structureMovement.train.CouplingCreationPacket;
import com.simibubi.create.content.contraptions.components.structureMovement.train.capability.MinecartControllerUpdatePacket;
import com.simibubi.create.content.contraptions.fluids.actors.FluidSplashPacket;
import com.simibubi.create.content.contraptions.relays.advanced.sequencer.ConfigureSequencedGearshiftPacket;
import com.simibubi.create.content.contraptions.relays.gauge.GaugeObservedPacket;
import com.simibubi.create.content.curiosities.bell.SoulPulseEffectPacket;
import com.simibubi.create.content.curiosities.symmetry.ConfigureSymmetryWandPacket;
import com.simibubi.create.content.curiosities.symmetry.SymmetryEffectPacket;
import com.simibubi.create.content.curiosities.toolbox.ToolboxDisposeAllPacket;
import com.simibubi.create.content.curiosities.toolbox.ToolboxEquipPacket;
import com.simibubi.create.content.curiosities.tools.BlueprintAssignCompleteRecipePacket;
import com.simibubi.create.content.curiosities.tools.ExtendoGripInteractionPacket;
import com.simibubi.create.content.curiosities.weapons.PotatoCannonPacket;
import com.simibubi.create.content.curiosities.weapons.PotatoProjectileTypeManager;
import com.simibubi.create.content.curiosities.zapper.ZapperBeamPacket;
import com.simibubi.create.content.curiosities.zapper.terrainzapper.ConfigureWorldshaperPacket;
import com.simibubi.create.content.logistics.block.depot.EjectorAwardPacket;
import com.simibubi.create.content.logistics.block.depot.EjectorElytraPacket;
import com.simibubi.create.content.logistics.block.depot.EjectorPlacementPacket;
import com.simibubi.create.content.logistics.block.depot.EjectorTriggerPacket;
import com.simibubi.create.content.logistics.block.display.DisplayLinkConfigurationPacket;
import com.simibubi.create.content.logistics.block.mechanicalArm.ArmPlacementPacket;
import com.simibubi.create.content.logistics.item.LinkedControllerBindPacket;
import com.simibubi.create.content.logistics.item.LinkedControllerInputPacket;
import com.simibubi.create.content.logistics.item.LinkedControllerStopLecternPacket;
import com.simibubi.create.content.logistics.item.filter.FilterScreenPacket;
import com.simibubi.create.content.logistics.packet.ConfigureStockswitchPacket;
import com.simibubi.create.content.logistics.packet.FunnelFlapPacket;
import com.simibubi.create.content.logistics.packet.TunnelFlapPacket;
import com.simibubi.create.content.logistics.trains.TrackGraphRequestPacket;
import com.simibubi.create.content.logistics.trains.TrackGraphRollCallPacket;
import com.simibubi.create.content.logistics.trains.TrackGraphSyncPacket;
import com.simibubi.create.content.logistics.trains.entity.TrainPacket;
import com.simibubi.create.content.logistics.trains.entity.TrainPromptPacket;
import com.simibubi.create.content.logistics.trains.entity.TrainRelocationPacket;
import com.simibubi.create.content.logistics.trains.management.edgePoint.CurvedTrackSelectionPacket;
import com.simibubi.create.content.logistics.trains.management.edgePoint.signal.SignalEdgeGroupPacket;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.StationEditPacket;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.TrainEditPacket;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.TrainEditPacket.TrainEditReturnPacket;
import com.simibubi.create.content.logistics.trains.management.schedule.ScheduleEditPacket;
import com.simibubi.create.content.logistics.trains.track.CurvedTrackDestroyPacket;
import com.simibubi.create.content.logistics.trains.track.PlaceExtendedCurvePacket;
import com.simibubi.create.content.schematics.packet.ConfigureSchematicannonPacket;
import com.simibubi.create.content.schematics.packet.InstantSchematicPacket;
import com.simibubi.create.content.schematics.packet.SchematicPlacePacket;
import com.simibubi.create.content.schematics.packet.SchematicSyncPacket;
import com.simibubi.create.content.schematics.packet.SchematicUploadPacket;
import com.simibubi.create.foundation.command.HighlightPacket;
import com.simibubi.create.foundation.command.SConfigureConfigPacket;
import com.simibubi.create.foundation.config.ui.CConfigureConfigPacket;
import com.simibubi.create.foundation.gui.container.ClearContainerPacket;
import com.simibubi.create.foundation.gui.container.GhostItemSubmitPacket;
import com.simibubi.create.foundation.tileEntity.RemoveTileEntityPacket;
import com.simibubi.create.foundation.tileEntity.behaviour.filtering.FilteringCountUpdatePacket;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.ScrollValueUpdatePacket;
import com.simibubi.create.foundation.utility.ServerSpeedProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PacketDistributor.TargetPoint;
import net.minecraftforge.network.simple.SimpleChannel;
public enum AllPackets {
    CONFIGURE_REDSTONE_DETECTOR_SIGNAL(ConfigureRedstoneDetectorSignalPacket.class, ConfigureRedstoneDetectorSignalPacket::new, PLAY_TO_SERVER),
    CONFIGURE_COMPUTER_SCRIPT(ConfigureComputerScriptPacket.class, ConfigureComputerScriptPacket::new, PLAY_TO_SERVER),
    COMPUTER_SEND_RUN(ComputerSendRunPacket.class, ComputerSendRunPacket::new, PLAY_TO_SERVER)
    ;

    public static final ResourceLocation CHANNEL_NAME = Computing.asResource("main");
    public static final int NETWORK_VERSION = 2;
    public static final String NETWORK_VERSION_STR = String.valueOf(NETWORK_VERSION);
    public static SimpleChannel channel;

    private LoadedPacket<?> packet;

    <T extends SimplePacketBase> AllPackets(Class<T> type, Function<FriendlyByteBuf, T> factory,
                                            NetworkDirection direction) {
        packet = new LoadedPacket<>(type, factory, direction);
    }

    public static void registerPackets() {
        channel = NetworkRegistry.ChannelBuilder.named(CHANNEL_NAME)
                .serverAcceptedVersions(NETWORK_VERSION_STR::equals)
                .clientAcceptedVersions(NETWORK_VERSION_STR::equals)
                .networkProtocolVersion(() -> NETWORK_VERSION_STR)
                .simpleChannel();
        for (AllPackets packet : values())
            packet.packet.register();
    }

    /*public static void sendToNear(Level world, BlockPos pos, int range, Object message) {
        channel.send(
                PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(pos.getX(), pos.getY(), pos.getZ(), range, world.dimension())),
                message);
    }*/

    private static class LoadedPacket<T extends SimplePacketBase> {
        private static int index = 0;

        private BiConsumer<T, FriendlyByteBuf> encoder;
        private Function<FriendlyByteBuf, T> decoder;
        private BiConsumer<T, Supplier<NetworkEvent.Context>> handler;
        private Class<T> type;
        private NetworkDirection direction;

        private LoadedPacket(Class<T> type, Function<FriendlyByteBuf, T> factory, NetworkDirection direction) {
            encoder = T::write;
            decoder = factory;
            handler = T::handle;
            this.type = type;
            this.direction = direction;
        }

        private void register() {
            channel.messageBuilder(type, index++, direction)
                    .encoder(encoder)
                    .decoder(decoder)
                    .consumerNetworkThread(handler)
                    .add();


        }
    }
}
