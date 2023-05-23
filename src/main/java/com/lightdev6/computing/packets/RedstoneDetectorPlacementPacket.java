package com.lightdev6.computing.packets;

import com.lightdev6.computing.block.redstonedetector.RedstoneDetectorBlockEntity;
import com.lightdev6.computing.block.redstonedetector.RedstoneDetectorTargetHandler;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RedstoneDetectorPlacementPacket extends SimplePacketBase {
    private BlockPos pos;
    private BlockPos targetPos;

    public RedstoneDetectorPlacementPacket(BlockPos pos, BlockPos targetPos){
        this.pos = pos;
        this.targetPos = targetPos;
    }

    public RedstoneDetectorPlacementPacket(FriendlyByteBuf buffer){
        pos = buffer.readBlockPos();
        targetPos = buffer.readBlockPos();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeBlockPos(targetPos);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null)
                return;
            Level world = player.level;
            if (world == null || !world.isLoaded(pos))
                return;
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof RedstoneDetectorBlockEntity)
                ((RedstoneDetectorBlockEntity) te).setTargetPos(targetPos);
        });
        return true;
    }


    public static class ClientBoundRequest extends SimplePacketBase {
        BlockPos pos;
        public ClientBoundRequest(BlockPos pos){
            this.pos = pos;
        }
        public ClientBoundRequest(FriendlyByteBuf buffer){
            this.pos = buffer.readBlockPos();
        }

        @Override
        public void write(FriendlyByteBuf buffer) {
            buffer.writeBlockPos(pos);
        }

        @Override
        public boolean handle(NetworkEvent.Context context) {
            context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> RedstoneDetectorTargetHandler.flushSettings(pos)));
            return true;
        }
    }
}
