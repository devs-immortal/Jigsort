package net.immortaldevs.jigsort.mixin;

import net.immortaldevs.jigsort.impl.JigsortStructureTemplate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(StructureTemplate.class)
public abstract class StructureTemplateMixin implements JigsortStructureTemplate {
    @Unique
    private @Nullable BlockBox customBoundingBox = null;

    @Inject(method = "writeNbt",
            at = @At("TAIL"))
    private void writeNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        nbt.putIntArray("customBoundingBox", this.customBoundingBox == null
                ? new int[0]
                : new int[]{
                        this.customBoundingBox.getMinX(),
                        this.customBoundingBox.getMinY(),
                        this.customBoundingBox.getMinZ(),
                        this.customBoundingBox.getMaxX(),
                        this.customBoundingBox.getMaxY(),
                        this.customBoundingBox.getMaxZ()});
    }

    @Inject(method = "readNbt",
            at = @At("TAIL"))
    private void readNbt(NbtCompound nbt, CallbackInfo ci) {
        int[] conflictSize = nbt.getIntArray("customBoundingBox");
        this.customBoundingBox = conflictSize.length >= 6
                ? new BlockBox(conflictSize[0],
                        conflictSize[1],
                        conflictSize[2],
                        conflictSize[3],
                        conflictSize[4],
                        conflictSize[5])
                : null;
    }

    @ModifyVariable(method = "saveFromWorld",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getBlockEntity(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/entity/BlockEntity;"),
            index = 15,
            allow = 1)
    private BlockState replaceStructureVoids(BlockState value) {
        return value.isOf(Blocks.STRUCTURE_VOID) ? Blocks.AIR.getDefaultState() : value;
    }

    @Override
    public @Nullable BlockBox getCustomBoundingBox() {
        return this.customBoundingBox;
    }

    @Override
    public void setCustomBoundingBox(@Nullable BlockBox boundingBox) {
        this.customBoundingBox = boundingBox;
    }
}
