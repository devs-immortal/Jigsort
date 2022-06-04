package net.immortaldevs.jigsort.mixin.client;

import net.immortaldevs.jigsort.JigsortStructureBlockBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.StructureBlockBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.structure.Structure;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructureBlockBlockEntityRenderer.class)
public abstract class StructureBlockBlockEntityRendererMixin {
    @Inject(method = "render(Lnet/minecraft/block/entity/StructureBlockBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At("TAIL"))
    private void render(StructureBlockBlockEntity structureBlock,
                        float tickDelta,
                        MatrixStack matrices,
                        VertexConsumerProvider vertexConsumers,
                        int light,
                        int overlay,
                        CallbackInfo ci) {
        BlockBox customBoundingBox = ((JigsortStructureBlockBlockEntity) structureBlock).getCustomBoundingBox();
        if (structureBlock.getMode() == StructureBlockMode.SAVE && customBoundingBox != null) {
            BlockPos offset = structureBlock.getOffset();
            int offsetX = offset.getX();
            int offsetY = offset.getY();
            int offsetZ = offset.getZ();

            Vec3d min = Structure.transformAround(new Vec3d(customBoundingBox.getMinX(),
                            customBoundingBox.getMinY(),
                            customBoundingBox.getMinZ()),
                    structureBlock.getMirror(),
                    structureBlock.getRotation(),
                    BlockPos.ORIGIN).add(offsetX, offsetY, offsetZ);

            Vec3d max = Structure.transformAround(new Vec3d(customBoundingBox.getMaxX() + 1.0,
                            customBoundingBox.getMaxY() + 1.0,
                            customBoundingBox.getMaxZ() + 1.0),
                    structureBlock.getMirror(),
                    structureBlock.getRotation(),
                    BlockPos.ORIGIN).add(offsetX, offsetY, offsetZ);

            WorldRenderer.drawBox(matrices,
                    vertexConsumers.getBuffer(RenderLayer.getLines()),
                    min.x,
                    min.y,
                    min.z,
                    max.x,
                    max.y,
                    max.z,
                    0.9f,
                    0.9f,
                    0f,
                    1.0f,
                    0.9f,
                    0.9f,
                    0f);

        }
    }
}
