package net.immortaldevs.jigsort.mixin;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.StructureVoidBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(StructureVoidBlock.class)
public abstract class StructureVoidBlockMixin {
    /**
     * @author Solly Watkins <solly@sollyw.com>
     * @reason Use a model instead of being invisible
     */
    @Overwrite
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
