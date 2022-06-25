package net.immortaldevs.jigsort.mixin;

import net.immortaldevs.divineintervention.injection.ModifyOperand;
import net.immortaldevs.jigsort.api.JigsortStructurePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Comparator;
import java.util.List;

// todo optimise this vanilla class, it sucks
@Mixin(StructurePool.class)
public abstract class StructurePoolMixin {
    @ModifyOperand(method = "getElementIndicesInRandomOrder",
            at = @At(value = "INVOKE",
                    shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/util/Util;copyShuffled(Lit/unimi/dsi/fastutil/objects/ObjectArrayList;Lnet/minecraft/util/math/random/Random;)Ljava/util/List;",
                    remap = false),
            allow = 1)
    private static List<StructurePoolElement> sort(List<StructurePoolElement> elements) {
        elements.sort(Comparator.comparingInt(JigsortStructurePoolElement::getPriority));
        return elements;
    }
}
