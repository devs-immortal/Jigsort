package net.immortaldevs.jigsort.mixin;

import net.minecraft.world.gen.structure.JigsawStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(JigsawStructure.class)
public abstract class JigsawStructureMixin {
    @ModifyConstant(method = "method_41662",
            constant = @Constant(intValue = 7))
    private static int adjustMaxSize(int constant) {
        return 65535;
    }
}
