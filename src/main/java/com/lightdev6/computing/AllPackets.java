package com.lightdev6.computing;

import com.lightdev6.computing.packets.*;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import static net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT;
import static net.minecraftforge.network.NetworkDirection.PLAY_TO_SERVER;

public enum AllPackets {
    //Player to server
    CONFIGURE_REDSTONE_DETECTOR_SIGNAL(ConfigureInputSignalPacket.class, ConfigureInputSignalPacket::new, PLAY_TO_SERVER),
    CONFIGURE_COMPUTER_SCRIPT(ConfigureComputerScriptPacket.class, ConfigureComputerScriptPacket::new, PLAY_TO_SERVER),
    COMPUTER_SEND_RUN(ComputerSendRunPacket.class, ComputerSendRunPacket::new, PLAY_TO_SERVER),
    COMPUTER_SEND_STOP(ComputerSendStopPacket.class, ComputerSendStopPacket::new, PLAY_TO_SERVER),
    COMPUTER_SEND_TERMINAL(ComputerSendTerminalPacket.class, ComputerSendTerminalPacket::new, PLAY_TO_SERVER),
    COMPUTER_REQUEST_TERMINAL_UPDATE(ComputerRequestUpdatePacket.class, ComputerRequestUpdatePacket::new, PLAY_TO_SERVER),
    PLACE_REDSTONE_DETECTOR(RedstoneDetectorPlacementPacket.class, RedstoneDetectorPlacementPacket::new, PLAY_TO_SERVER),
    PLACE_SCANNER(ScannerPlacementPacket.class, ScannerPlacementPacket::new, PLAY_TO_SERVER),

    //Server to player
    S_PLACE_REDSTONE_DETECTOR(RedstoneDetectorPlacementPacket.ClientBoundRequest.class, RedstoneDetectorPlacementPacket.ClientBoundRequest::new, PLAY_TO_CLIENT),
    S_PLACE_SCANNER(ScannerPlacementPacket.ClientBoundRequest.class, ScannerPlacementPacket.ClientBoundRequest::new, PLAY_TO_CLIENT)
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
            handler = (packet, contextSupplier) -> {
                NetworkEvent.Context context = contextSupplier.get();
                if (packet.handle(context)) {
                    context.setPacketHandled(true);
                }
            };
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
