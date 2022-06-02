package net.immortaldevs.jigsort;

import net.immortaldevs.jigsort.JigsortJigsawBlockEntity.ConflictMode;

public interface JigsortUpdateJigsawC2SPacket {
    int getImmediateChance();

    void setImmediateChance(int chance);

    ConflictMode getConflictMode();

    void setConflictMode(ConflictMode conflictMode);

    int getPriority();

    void setPriority(int priority);
}
