package net.immortaldevs.jigsort.api;

import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public interface JigsortStructurePoolElement {
    default @Nullable BlockBox getCustomBoundingBox(StructureTemplateManager structureTemplateManager,
                                                    BlockPos pos,
                                                    BlockRotation rotation) {
        throw new Error();
    }

    default int getPriority() {
        throw new Error();
    }
}
