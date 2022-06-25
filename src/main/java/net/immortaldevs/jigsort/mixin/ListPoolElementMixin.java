package net.immortaldevs.jigsort.mixin;

import net.immortaldevs.jigsort.api.JigsortStructurePoolElement;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.pool.EmptyPoolElement;
import net.minecraft.structure.pool.ListPoolElement;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(ListPoolElement.class)
public abstract class ListPoolElementMixin implements JigsortStructurePoolElement {
    @Shadow
    @Final
    private List<StructurePoolElement> elements;

    @Override
    public @Nullable BlockBox getCustomBoundingBox(StructureTemplateManager structureTemplateManager,
                                                   BlockPos pos,
                                                   BlockRotation rotation) {
        return BlockBox.encompass(this.elements.stream()
                        .filter(element -> element != EmptyPoolElement.INSTANCE)
                        .map(element -> {
                            BlockBox box = element.getCustomBoundingBox(structureTemplateManager, pos, rotation);
                            return box == null ? element.getBoundingBox(structureTemplateManager, pos, rotation) : box;
                        })::iterator)
                .orElse(null);
    }
}
