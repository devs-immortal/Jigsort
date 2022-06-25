package net.immortaldevs.jigsort.mixin;

import net.immortaldevs.jigsort.api.JigsortStructurePoolElement;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.Nullable;

@Mixin(StructurePoolElement.class)
public abstract class StructurePoolElementMixin implements JigsortStructurePoolElement {
    @Override
    public @Nullable BlockBox getCustomBoundingBox(StructureTemplateManager structureTemplateManager,
                                                   BlockPos pos,
                                                   BlockRotation rotation) {
        return null;
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
