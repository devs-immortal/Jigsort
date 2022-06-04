package net.immortaldevs.jigsort;

import com.mojang.datafixers.util.Pair;
import net.immortaldevs.jigsort.mixin.StructurePoolAccessor;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class SortedStructurePool extends StructurePool {
    public SortedStructurePool(Identifier id,
                               Identifier terminatorsId,
                               List<Function<Projection, ? extends StructurePoolElement>> elementCounts,
                               Projection projection) {
        super(id, terminatorsId, elementCounts.stream()
                .map(function -> Pair.<StructurePoolElement, Integer>of(function.apply(projection), 1))
                .toList());
    }

    @Override
    public List<StructurePoolElement> getElementIndicesInRandomOrder(Random random) {
        return ((StructurePoolAccessor) this).getElements();
    }
}
