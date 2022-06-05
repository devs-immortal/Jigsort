package net.immortaldevs.jigsort.mixin;

import net.immortaldevs.divineintervention.injection.ModifyOperand;
import net.immortaldevs.jigsort.api.JigsortStructurePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Arrays;
import java.util.Comparator;

// todo optimise this vanilla class, it sucks
@Mixin(StructurePool.class)
public abstract class StructurePoolMixin {
    @ModifyOperand(method = "getElementIndicesInRandomOrder",
            at = @At(value = "INVOKE", //copying your already bloated list twice, very nice thank you mojang
                    target = "Lcom/google/common/collect/ImmutableList;copyOf([Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList;",
                    shift = At.Shift.BEFORE,
                    remap = false),
            allow = 1)
    private static StructurePoolElement[] sort(StructurePoolElement[] elements) {
        Arrays.sort(elements, Comparator.comparingInt(JigsortStructurePoolElement::getPriority));
        return elements;
    }
}
