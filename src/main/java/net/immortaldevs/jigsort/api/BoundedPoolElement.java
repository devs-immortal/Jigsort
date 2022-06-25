package net.immortaldevs.jigsort.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.immortaldevs.jigsort.impl.Util;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.pool.StructurePool.Projection;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

public class BoundedPoolElement extends WrappedPoolElement {
    public static final Codec<BoundedPoolElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    projectionGetter(),
                    elementGetter(),
                    BlockBox.CODEC.fieldOf("bounding_box")
                            .forGetter(element -> element.boundingBox))
            .apply(instance, BoundedPoolElement::new));

    protected final BlockBox boundingBox;

    public BoundedPoolElement(Projection projection,
                              StructurePoolElement element,
                              BlockBox boundingBox) {
        super(projection, element);
        this.boundingBox = boundingBox;
    }

    @Override
    public StructurePoolElementType<?> getType() {
        return JigsortStructurePoolElementTypes.BOUNDED_POOL_ELEMENT;
    }

    @Override
    public BlockBox getCustomBoundingBox(StructureTemplateManager structureTemplateManager,
                                         BlockPos pos,
                                         BlockRotation rotation) {
        return Util.transformBox(this.boundingBox, pos, rotation);
    }

    @Override
    public String toString() {
        return "Bounded[" + this.element + "]";
    }
}
