package net.immortaldevs.jigsort.impl;

import net.minecraft.util.math.BlockBox;

import javax.annotation.Nullable;

public interface JigsortStructureBlockBlockEntity {
    @Nullable BlockBox getCustomBoundingBox();

    void setCustomBoundingBox(@Nullable BlockBox size);
}
