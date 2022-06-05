package net.immortaldevs.jigsort.impl;

import net.minecraft.util.math.BlockBox;

import javax.annotation.Nullable;

public interface JigsortStructure {
    @Nullable BlockBox getCustomBoundingBox();

    void setCustomBoundingBox(@Nullable BlockBox boundingBox);
}
