package net.immortaldevs.jigsort.mixin;

import net.immortaldevs.divineintervention.injection.ModifyOperand;
import net.immortaldevs.jigsort.impl.JigsortJigsawBlockEntity.ConflictMode;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureTemplate.StructureBlockInfo;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.HeightLimitView;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;

import java.util.Deque;

//TODO: completely rewrite this vanilla class. it sucks.
@Mixin(targets = "net/minecraft/structure/pool/StructurePoolBasedGenerator$StructurePoolGenerator")
public abstract class StructurePoolGeneratorMixin {
    @Unique
    private static final Box EMPTY_BOX = new Box(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

    @Unique
    private static final BlockBox EMPTY_BLOCK_BOX = new BlockBox(BlockPos.ORIGIN);

    @Shadow
    @Final
    private Random random;

    @Shadow
    @Final
    private StructureTemplateManager structureTemplateManager;

    @Unique
    private StructureBlockInfo info;

    @Unique
    private int budget = 0;

    @ModifyOperand(method = "generatePiece",
            at = @At(value = "INVOKE",
                    shift = At.Shift.AFTER,
                    target = "Ljava/util/Iterator;next()Ljava/lang/Object;",
                    ordinal = 0))
    private Object next(Object info) {
        return this.info = (StructureBlockInfo) info;
    }

    @ModifyOperand(method = "generatePiece",
            slice = @Slice(
                    from = @At(value = "CONSTANT",
                            args = "doubleValue=0.25")),
            at = @At(value = "INVOKE",
                    shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/util/math/Box;contract(D)Lnet/minecraft/util/math/Box;",
                    ordinal = 0),
            locals = {16, 37})
    private Box check(Box box, StructureBlockInfo self, StructureBlockInfo other) {
        if (ConflictMode.byName(self.nbt.getString("conflict_mode")).check
                && ConflictMode.byName(other.nbt.getString("conflict_mode")).check) return box;
        return EMPTY_BOX;
    }

    @ModifyOperand(method = "generatePiece",
            slice = @Slice(
                    from = @At(value = "INVOKE",
                            target = "Lnet/minecraft/util/math/BlockBox;encompass(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/math/BlockBox;")),
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Box;from(Lnet/minecraft/util/math/BlockBox;)Lnet/minecraft/util/math/Box;",
                    ordinal = 0),
            locals = {30, 32, 39, 47},
            allow = 1)
    private BlockBox checkBounds(BlockBox box,
                                 StructurePoolElement element,
                                 BlockRotation rotation,
                                 BlockPos pos,
                                 int q) {
        BlockBox customBox = element.getCustomBoundingBox(this.structureTemplateManager, pos, rotation);
        return customBox == null ? box : customBox.offset(0, q, 0);
    }

    @ModifyOperand(method = "generatePiece",
            slice = @Slice(
                    from = @At(value = "FIELD",
                            target = "Lnet/minecraft/util/function/BooleanBiFunction;ONLY_SECOND:Lnet/minecraft/util/function/BooleanBiFunction;")),
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Box;from(Lnet/minecraft/util/math/BlockBox;)Lnet/minecraft/util/math/Box;",
                    ordinal = 0),
            locals = {16, 30, 32, 37, 39, 47})
    private BlockBox combineBounds(BlockBox box,
                                   StructureBlockInfo self,
                                   StructurePoolElement element,
                                   BlockRotation rotation,
                                   StructureBlockInfo other,
                                   BlockPos pos,
                                   int q) {
        this.budget -= self.nbt.getInt("cost") + other.nbt.getInt("cost");

        if (ConflictMode.byName(self.nbt.getString("conflict_mode")).combine
                && ConflictMode.byName(other.nbt.getString("conflict_mode")).combine) {
            BlockBox customBox = element.getCustomBoundingBox(this.structureTemplateManager, pos, rotation);
            return customBox == null ? box : customBox.offset(0, q, 0);
        }

        return EMPTY_BLOCK_BOX;
    }

    @Redirect(method = "generatePiece",
            at = @At(value = "INVOKE",
                    target = "Ljava/util/Deque;addLast(Ljava/lang/Object;)V"))
    private <E> void addLast(Deque<E> instance,
                             E e,
                             PoolStructurePiece piece,
                             MutableObject<VoxelShape> pieceShape,
                             int minY,
                             boolean modifyBoundingBox,
                             HeightLimitView world) {
        if (this.random.nextInt(100) >= this.info.nbt.getInt("immediate_chance")) instance.addLast(e);
        else instance.addFirst(e);
    }

    @ModifyVariable(method = "generatePiece",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockBox;getMinY()I",
                    ordinal = 0),
            index = 13)
    private BlockBox modifyBoundingBox(BlockBox value, PoolStructurePiece piece) {
        BlockBox box = piece.getPoolElement().getCustomBoundingBox(this.structureTemplateManager,
                piece.getPos(),
                piece.getRotation());

        return box == null ? value : box;
    }

    @ModifyOperand(method = "generatePiece",
            at = @At(value = "INVOKE",
                    shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/block/JigsawBlock;attachmentMatches(Lnet/minecraft/structure/StructureTemplate$StructureBlockInfo;Lnet/minecraft/structure/StructureTemplate$StructureBlockInfo;)Z"),
            locals = {16, 37},
            allow = 1)
    private boolean attachmentMatches(boolean matches, StructureBlockInfo self, StructureBlockInfo other) {
        if (!matches) return false;
        return self.nbt.getInt("cost") + other.nbt.getInt("cost") <= this.budget;
    }
}
