package net.immortaldevs.jigsort.api;

import net.minecraft.structure.pool.StructurePoolElement;

@SuppressWarnings("unused")
public interface JigsortStructurePoolElement {
    default int getPriority() {
        throw new NoSuchMethodError();
    }

    default StructurePoolElement setPriority(int priority) {
        throw new NoSuchMethodError();
    }
}
