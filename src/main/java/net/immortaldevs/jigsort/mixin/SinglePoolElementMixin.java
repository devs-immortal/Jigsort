package net.immortaldevs.jigsort.mixin;

import net.immortaldevs.divineintervention.injection.ModifyOperand;
import net.immortaldevs.jigsort.api.JigsortStructurePoolElement;
import net.immortaldevs.jigsort.impl.JigsortStructureTemplate;
import net.immortaldevs.jigsort.impl.Util;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplate.StructureBlockInfo;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

@Mixin(SinglePoolElement.class)
public abstract class SinglePoolElementMixin implements JigsortStructurePoolElement {
    @Shadow
    protected abstract StructureTemplate getStructure(StructureTemplateManager structureTemplateManager);

    @ModifyOperand(method = "getStructureBlockInfos",
            at = @At(value = "RETURN"))
    private static List<StructureBlockInfo> sort(List<StructureBlockInfo> list) {
        list.sort(Comparator.comparingInt(info -> info.nbt.getInt("priority")));
        return list;
    }

    @Override
    public @Nullable BlockBox getCustomBoundingBox(StructureTemplateManager structureTemplateManager,
                                                   BlockPos pos,
                                                   BlockRotation rotation) {
        return Util.transformBox(((JigsortStructureTemplate) this.getStructure(structureTemplateManager))
                .getCustomBoundingBox(), pos, rotation);
    }
}
