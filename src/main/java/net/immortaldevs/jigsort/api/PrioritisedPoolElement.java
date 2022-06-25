package net.immortaldevs.jigsort.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.structure.pool.StructurePool.Projection;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePoolElementType;

public class PrioritisedPoolElement extends WrappedPoolElement {
    public static final Codec<PrioritisedPoolElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    projectionGetter(),
                    elementGetter(),
                    Codec.INT.fieldOf("priority")
                            .forGetter(e -> e.priority))
            .apply(instance, PrioritisedPoolElement::new));

    protected final int priority;

    public PrioritisedPoolElement(Projection projection,
                                  StructurePoolElement element,
                                  int priority) {
        super(projection, element);
        this.priority = priority;
    }

    @Override
    public StructurePoolElementType<?> getType() {
        return JigsortStructurePoolElementTypes.PRIORITISED_POOL_ELEMENT;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public String toString() {
        return "Prioritised[" + this.element + "]";
    }
}
