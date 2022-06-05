package net.immortaldevs.jigsort.mixin;

import net.immortaldevs.jigsort.api.JigsortStructurePoolElement;
import net.minecraft.structure.pool.StructurePoolElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(StructurePoolElement.class)
public abstract class StructurePoolElementMixin implements JigsortStructurePoolElement {
    @Unique
    private int priority = 0;

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }
}
