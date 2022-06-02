package net.immortaldevs.jigsort.mixin;

import net.immortaldevs.divineintervention.injection.ModifyOperand;
import net.minecraft.structure.Structure;
import net.minecraft.structure.pool.SinglePoolElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Comparator;
import java.util.List;

@Mixin(SinglePoolElement.class)
public abstract class SinglePoolElementMixin {
    @ModifyOperand(method = "getStructureBlockInfos",
            at = @At(value = "RETURN",
                    shift = At.Shift.BEFORE))
    private static List<Structure.StructureBlockInfo> sort(List<Structure.StructureBlockInfo> list) {
        list.sort(Comparator.comparingInt(info -> info.nbt.getInt("priority")));
        return list;
    }
}
