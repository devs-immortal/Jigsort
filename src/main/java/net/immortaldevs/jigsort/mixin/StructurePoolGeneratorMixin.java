package net.immortaldevs.jigsort.mixin;

import net.immortaldevs.divineintervention.injection.ModifyOperand;
import net.immortaldevs.jigsort.JigsortJigsawBlockEntity.ConflictMode;
import net.minecraft.structure.Structure;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.*;

@Mixin(targets = "net/minecraft/structure/pool/StructurePoolBasedGenerator$StructurePoolGenerator")
public abstract class StructurePoolGeneratorMixin {
    @Shadow
    @Final
    private Random random;

    @Unique
    private static final Box EMPTY_BOX = new Box(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

    @Unique
    private Structure.StructureBlockInfo info;

    @ModifyOperand(method = "generatePiece",
            at = @At(value = "INVOKE",
                    target = "Ljava/util/Iterator;next()Ljava/lang/Object;",
                    ordinal = 0,
                    shift = At.Shift.AFTER))
    private Structure.StructureBlockInfo next(Structure.StructureBlockInfo info) {
        return this.info = info;
    }

    @ModifyOperand(method = "generatePiece",
            slice = @Slice(
                    from = @At(value = "CONSTANT",
                            args = "doubleValue=0.25")),
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Box;contract(D)Lnet/minecraft/util/math/Box;",
                    ordinal = 0))
    private Box check(Box box) {
        if (ConflictMode.byName(this.info.nbt.getString("conflict_mode")).check) return box;
        return EMPTY_BOX;
    }

    @ModifyOperand(method = "generatePiece",
            slice = @Slice(
                    from = @At(value = "FIELD",
                            target = "Lnet/minecraft/util/function/BooleanBiFunction;ONLY_SECOND:Lnet/minecraft/util/function/BooleanBiFunction;")),
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Box;from(Lnet/minecraft/util/math/BlockBox;)Lnet/minecraft/util/math/Box;",
                    ordinal = 0),
            allow = 1)
    private Box combine(Box box) {
        if (ConflictMode.byName(this.info.nbt.getString("conflict_mode")).combine) return box;
        return EMPTY_BOX;
    }

    @Redirect(method = "generatePiece",
            at = @At(value = "INVOKE",
                    target = "Ljava/util/Deque;addLast(Ljava/lang/Object;)V"))
    private <E> void addLast(Deque<E> instance, E e) {
        if (this.random.nextInt(100) < this.info.nbt.getInt("immediate_chance")) {
            instance.addFirst(e);
        } else instance.addLast(e);
    }
}
