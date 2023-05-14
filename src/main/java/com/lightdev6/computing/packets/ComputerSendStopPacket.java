package com.lightdev6.computing.packets;

import com.lightdev6.computing.Computing;
import com.lightdev6.computing.block.computer.ComputerBlockEntity;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ComputerSendStopPacket extends SimplePacketBase {
    BlockPos pos;
    public ComputerSendStopPacket(BlockPos pos){
        this.pos = pos;
    }
    public ComputerSendStopPacket(FriendlyByteBuf buffer){
        pos = buffer.readBlockPos();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            ServerLevel level = player.getLevel();
            if (level.getBlockEntity(pos) instanceof ComputerBlockEntity computer){
                computer.stop();
            }
        });
        ctx.setPacketHandled(true);
    }
}
