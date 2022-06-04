package net.immortaldevs.jigsort.mixin;

import net.minecraft.structure.Structure;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Structure.class)
public interface StructureAccessor {
    @Invoker
    static BlockBox callCreateBox(BlockPos pos,
                                  BlockRotation rotation,
                                  BlockPos pivot,
                                  BlockMirror mirror,
                                  Vec3i dimensions) {
        throw new Error();
    }
}
