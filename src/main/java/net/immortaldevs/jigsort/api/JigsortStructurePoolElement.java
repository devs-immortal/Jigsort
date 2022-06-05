package net.immortaldevs.jigsort.api;

import net.minecraft.structure.StructureManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

public interface JigsortStructurePoolElement {
    default BlockBox getCustomBoundingBox(StructureManager structureManager, BlockPos pos, BlockRotation rotation) {
        throw new Error();
    }

    default int getPriority() {
        throw new Error();
    }
}
