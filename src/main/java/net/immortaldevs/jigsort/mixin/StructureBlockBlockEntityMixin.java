package net.immortaldevs.jigsort.mixin;

import net.immortaldevs.jigsort.impl.JigsortStructure;
import net.immortaldevs.jigsort.impl.JigsortStructureBlockBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.Structure;
import net.minecraft.util.math.BlockBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

import static net.minecraft.util.math.MathHelper.clamp;

@Mixin(StructureBlockBlockEntity.class)
public abstract class StructureBlockBlockEntityMixin implements JigsortStructureBlockBlockEntity {
    @Unique
    private @Nullable BlockBox customBoundingBox = null;

    @Inject(method = "writeNbt",
            at = @At("TAIL"))
    private void writeNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putIntArray("customBoundingBox", this.customBoundingBox == null ? new int[0] : new int[]{
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
        int[] customBoundingBox = nbt.getIntArray("customBoundingBox");
        if (customBoundingBox.length < 6) this.customBoundingBox = null;
        else this.customBoundingBox = new BlockBox(clamp(customBoundingBox[0], -48, 48),
                clamp(customBoundingBox[1], -48, 48),
                clamp(customBoundingBox[2], -48, 48),
                clamp(customBoundingBox[3], -48, 48),
                clamp(customBoundingBox[4], -48, 48),
                clamp(customBoundingBox[5], -48, 48));
    }

    @Inject(method = "detectStructureSize",
            at = @At("RETURN"))
    private void detectStructureSize(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) this.customBoundingBox = null;
    }

    @ModifyVariable(method = "saveStructure(Z)Z",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/structure/Structure;setAuthor(Ljava/lang/String;)V"),
            index = 5)
    private Structure saveStructure(Structure structure) {
        ((JigsortStructure) structure).setCustomBoundingBox(this.customBoundingBox);
        return structure;
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
