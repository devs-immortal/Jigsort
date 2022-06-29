package net.immortaldevs.jigsort.mixin;

import net.immortaldevs.divineintervention.injection.ModifyOperand;
import net.immortaldevs.jigsort.impl.JigsortJigsawBlockEntity;
import net.immortaldevs.jigsort.impl.JigsortStructureBlockBlockEntity;
import net.immortaldevs.jigsort.impl.JigsortUpdateJigsawC2SPacket;
import net.immortaldevs.jigsort.impl.JigsortUpdateStructureBlockC2SPacket;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.network.packet.c2s.play.UpdateJigsawC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateStructureBlockC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @ModifyOperand(method = "onUpdateStructureBlock",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/block/entity/StructureBlockBlockEntity;hasStructureName()Z"))
    private static StructureBlockBlockEntity onUpdateStructureBlock(StructureBlockBlockEntity structureBlock,
                                                                    UpdateStructureBlockC2SPacket packet) {
        JigsortStructureBlockBlockEntity jigsortStructureBlock = (JigsortStructureBlockBlockEntity) structureBlock;
        JigsortUpdateStructureBlockC2SPacket jigsortPacket = (JigsortUpdateStructureBlockC2SPacket) packet;

        jigsortStructureBlock.setCustomBoundingBox(jigsortPacket.getCustomBoundingBox());
        jigsortStructureBlock.setInvertVoids(((JigsortUpdateStructureBlockC2SPacket) packet).getInvertVoids());
        return structureBlock;
    }

    @ModifyOperand(method = "onUpdateJigsaw",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/block/entity/JigsawBlockEntity;markDirty()V"))
    private static JigsawBlockEntity onUpdateJigsaw(JigsawBlockEntity jigsaw, UpdateJigsawC2SPacket packet) {
        JigsortUpdateJigsawC2SPacket jigsortPacket = ((JigsortUpdateJigsawC2SPacket) packet);
        JigsortJigsawBlockEntity jigsortJigsaw = ((JigsortJigsawBlockEntity) jigsaw);

        jigsortJigsaw.setPriority(jigsortPacket.getPriority());
        jigsortJigsaw.setImmediateChance(jigsortPacket.getImmediateChance());
        jigsortJigsaw.setConflictMode(jigsortPacket.getConflictMode());
        return jigsaw;
    }
}
