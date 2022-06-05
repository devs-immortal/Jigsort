package net.immortaldevs.jigsort.api;

import net.minecraft.structure.pool.StructurePoolElementType;

public interface JigsortStructurePoolElementTypes {
    StructurePoolElementType<PrioritisedPoolElement> PRIORITISED_POOL_ELEMENT = () -> PrioritisedPoolElement.CODEC;
}
