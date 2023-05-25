package com.lightdev6.computing.packets;

import com.lightdev6.computing.block.inputs.scanner.ScannerBlockEntity;
import com.lightdev6.computing.block.inputs.scanner.ScannerTargetHandler;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class ScannerPlacementPacket extends SimplePacketBase {
    private BlockPos pos;
    private BlockPos targetPos;

    public ScannerPlacementPacket(BlockPos pos, BlockPos targetPos){
        this.pos = pos;
        this.targetPos = targetPos;
    }

    public ScannerPlacementPacket(FriendlyByteBuf buffer){
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
            Level level = player.level;
            if (level == null || !level.isLoaded(pos))
                return;
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ScannerBlockEntity scanner)
                scanner.setTargetPos(targetPos);

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
            context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ScannerTargetHandler.flushSettings(pos)));
            return true;
        }
    }
}
