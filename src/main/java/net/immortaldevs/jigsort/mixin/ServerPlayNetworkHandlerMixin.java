package net.immortaldevs.jigsort.mixin;

import net.immortaldevs.divineintervention.injection.ModifyOperand;
import net.immortaldevs.jigsort.JigsortJigsawBlockEntity;
import net.immortaldevs.jigsort.JigsortUpdateJigsawC2SPacket;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.network.packet.c2s.play.UpdateJigsawC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @ModifyOperand(method = "onUpdateJigsaw",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/block/entity/JigsawBlockEntity;markDirty()V",
                    shift = At.Shift.BEFORE))
    private static JigsawBlockEntity updateJigsaw(JigsawBlockEntity jigsaw, UpdateJigsawC2SPacket packet) {
        JigsortUpdateJigsawC2SPacket jigsortPacket = ((JigsortUpdateJigsawC2SPacket) packet);
        JigsortJigsawBlockEntity jigsortJigsaw = ((JigsortJigsawBlockEntity) jigsaw);

        jigsortJigsaw.setImmediateChance(jigsortPacket.getImmediateChance());
        jigsortJigsaw.setConflictMode(jigsortPacket.getConflictMode());
        jigsortJigsaw.setPriority(jigsortPacket.getPriority());
        return jigsaw;
    }
}
