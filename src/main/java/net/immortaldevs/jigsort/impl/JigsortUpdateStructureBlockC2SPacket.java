package net.immortaldevs.jigsort.impl;

import net.minecraft.util.math.BlockBox;

import javax.annotation.Nullable;

public interface JigsortUpdateStructureBlockC2SPacket {
    @Nullable BlockBox getCustomBoundingBox();

    void setCustomBoundingBox(@Nullable BlockBox boundingBox);
}
