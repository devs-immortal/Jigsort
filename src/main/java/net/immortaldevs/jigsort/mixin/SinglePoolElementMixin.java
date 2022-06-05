package net.immortaldevs.jigsort.mixin;

import net.immortaldevs.divineintervention.injection.ModifyOperand;
import net.immortaldevs.jigsort.impl.JigsortStructure;
import net.immortaldevs.jigsort.api.JigsortStructurePoolElement;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Comparator;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Mixin(SinglePoolElement.class)
public abstract class SinglePoolElementMixin implements JigsortStructurePoolElement {
    @Shadow
    protected abstract Structure getStructure(StructureManager structureManager);

    @Shadow
    public abstract BlockBox getBoundingBox(StructureManager structureManager, BlockPos pos, BlockRotation rotation);

    @ModifyOperand(method = "getStructureBlockInfos",
            at = @At(value = "RETURN",
                    shift = At.Shift.BEFORE))
    private static List<Structure.StructureBlockInfo> sort(List<Structure.StructureBlockInfo> list) {
        list.sort(Comparator.comparingInt(info -> info.nbt.getInt("priority")));
        return list;
    }

    @Override
    public BlockBox getCustomBoundingBox(StructureManager structureManager, BlockPos pos, BlockRotation rotation) {
        BlockBox boundingBox = ((JigsortStructure) this.getStructure(structureManager)).getCustomBoundingBox();
        if (boundingBox == null) return this.getBoundingBox(structureManager, pos, rotation);

        int minX = boundingBox.getMinX(), minY = boundingBox.getMinY(), minZ = boundingBox.getMinZ();
        int maxX = boundingBox.getMaxX(), maxY = boundingBox.getMaxY(), maxZ = boundingBox.getMaxZ();

        switch (rotation) {
            case CLOCKWISE_180 -> {
                minX = -minX;
                maxX = -maxX;
                minZ = -minZ;
                maxZ = -maxZ;
            }
            case COUNTERCLOCKWISE_90 -> {
                int a = -minX, b = -maxX;
                minX = minZ;
                maxX = maxZ;
                minZ = a;
                maxZ = b;
            }
            case CLOCKWISE_90 -> {
                int a = minX, b = maxX;
                minX = -minZ;
                maxX = -maxZ;
                minZ = a;
                maxZ = b;
            }
        }

        return new BlockBox(
                min(minX, maxX) + pos.getX(),
                min(minY, maxY) + pos.getY(),
                min(minZ, maxZ) + pos.getZ(),
                max(minX, maxX) + pos.getX(),
                max(minY, maxY) + pos.getY(),
                max(minZ, maxZ) + pos.getZ());
    }
}
