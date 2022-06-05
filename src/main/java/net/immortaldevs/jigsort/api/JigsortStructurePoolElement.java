package net.immortaldevs.jigsort.api;

import org.apache.commons.lang3.NotImplementedException;

@SuppressWarnings("unused")
public interface JigsortStructurePoolElement {
    default int getPriority() {
        throw new NotImplementedException();
    }

    default void setPriority(int priority) {
        throw new NotImplementedException();
    }
}
