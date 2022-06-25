package net.immortaldevs.jigsort.api;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.structure.StructureTemplate.StructureBlockInfo;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.pool.StructurePool.Projection;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.List;

public abstract class WrappedPoolElement extends StructurePoolElement {
    protected final StructurePoolElement element;

    protected static <E extends WrappedPoolElement> RecordCodecBuilder<E, StructurePoolElement> elementGetter() {
        return StructurePoolElement.CODEC.fieldOf("element")
                .forGetter(e -> e.element);
    }

    protected WrappedPoolElement(Projection projection, StructurePoolElement element) {
        super(projection);
        this.element = element;
    }

    @Override
    public Vec3i getStart(StructureTemplateManager structureTemplateManager, BlockRotation rotation) {
        return this.element.getStart(structureTemplateManager, rotation);
    }

    @Override
    public List<StructureBlockInfo> getStructureBlockInfos(StructureTemplateManager structureTemplateManager,
                                                           BlockPos pos,
                                                           BlockRotation rotation,
                                                           Random random) {
        return this.element.getStructureBlockInfos(structureTemplateManager, pos, rotation, random);
    }

    @Override
    public BlockBox getBoundingBox(StructureTemplateManager structureTemplateManager,
                                   BlockPos pos,
                                   BlockRotation rotation) {
        return this.element.getBoundingBox(structureTemplateManager, pos, rotation);
    }

    @Override
    public boolean generate(StructureTemplateManager structureTemplateManager,
                            StructureWorldAccess world,
                            StructureAccessor structureAccessor,
                            ChunkGenerator chunkGenerator,
                            BlockPos pos,
                            BlockPos blockPos,
                            BlockRotation rotation,
                            BlockBox box,
                            Random random,
                            boolean keepJigsaws) {
        return this.element.generate(structureTemplateManager,
                world,
                structureAccessor,
                chunkGenerator,
                pos,
                blockPos,
                rotation,
                box,
                random,
                keepJigsaws);
    }

    @Override
    public StructurePoolElement setProjection(Projection projection) {
        this.element.setProjection(projection);
        return super.setProjection(projection);
    }

    @Override
    public int getGroundLevelDelta() {
        return this.element.getGroundLevelDelta();
    }

    @Override
    public BlockBox getCustomBoundingBox(StructureTemplateManager structureTemplateManager,
                                         BlockPos pos,
                                         BlockRotation rotation) {
        return this.element.getCustomBoundingBox(structureTemplateManager, pos, rotation);
    }

    @Override
    public int getPriority() {
        return this.element.getPriority();
    }
}
