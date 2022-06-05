package net.immortaldevs.jigsort.mixin;

import net.immortaldevs.jigsort.api.JigsortStructurePoolElement;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StructurePoolElement.class)
public abstract class StructurePoolElementMixin implements JigsortStructurePoolElement {
    @Shadow
    public abstract BlockBox getBoundingBox(StructureManager var1, BlockPos var2, BlockRotation var3);

    @Override
    public BlockBox getCustomBoundingBox(StructureManager structureManager, BlockPos pos, BlockRotation rotation) {
        return this.getBoundingBox(structureManager, pos, rotation);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
