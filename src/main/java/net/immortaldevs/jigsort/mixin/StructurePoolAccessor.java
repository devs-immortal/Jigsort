package net.immortaldevs.jigsort.mixin;

import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

// todo optimise this vanilla class, it sucks
@Mixin(StructurePool.class)
public interface StructurePoolAccessor {
    @Accessor
    List<StructurePoolElement> getElements();
}
