package net.immortaldevs.jigsort.impl;

import net.immortaldevs.jigsort.impl.JigsortJigsawBlockEntity.ConflictMode;

public interface JigsortUpdateJigsawC2SPacket {
    int getPriority();

    void setPriority(int priority);

    int getImmediateChance();

    void setImmediateChance(int chance);

    ConflictMode getConflictMode();

    void setConflictMode(ConflictMode conflictMode);
}
