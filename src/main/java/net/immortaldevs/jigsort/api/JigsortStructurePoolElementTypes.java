package net.immortaldevs.jigsort.api;

import net.minecraft.structure.pool.StructurePoolElementType;

public interface JigsortStructurePoolElementTypes {
    StructurePoolElementType<BoundedPoolElement> BOUNDED_POOL_ELEMENT = () -> BoundedPoolElement.CODEC;
    StructurePoolElementType<BoxPoolElement> BOX_POOL_ELEMENT = () -> BoxPoolElement.CODEC;
    StructurePoolElementType<ConnectingPoolElement> CONNECTING_POOL_ELEMENT = () -> ConnectingPoolElement.CODEC;
    StructurePoolElementType<PrioritisedPoolElement> PRIORITISED_POOL_ELEMENT = () -> PrioritisedPoolElement.CODEC;
}
