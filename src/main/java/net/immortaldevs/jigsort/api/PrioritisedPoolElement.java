package net.immortaldevs.jigsort.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.List;
import java.util.Random;

public class PrioritisedPoolElement extends StructurePoolElement {
    public static final Codec<PrioritisedPoolElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    StructurePoolElement.CODEC.fieldOf("element")
                            .forGetter(PrioritisedPoolElement::getElement),
                    Codec.INT.fieldOf("priority")
                            .forGetter(PrioritisedPoolElement::getPriority))
            .apply(instance, PrioritisedPoolElement::new));

    protected final StructurePoolElement element;
    protected final int priority;

    public PrioritisedPoolElement(StructurePoolElement element, int priority) {
        super(element.getProjection());
        this.element = element;
        this.priority = priority;
    }

    public StructurePoolElement getElement() {
        return this.element;
    }

    @Override
    public Vec3i getStart(StructureManager structureManager, BlockRotation rotation) {
        return this.element.getStart(structureManager, rotation);
    }

    @Override
    public List<Structure.StructureBlockInfo> getStructureBlockInfos(StructureManager structureManager,
                                                                     BlockPos pos,
                                                                     BlockRotation rotation,
                                                                     Random random) {
        return this.element.getStructureBlockInfos(structureManager, pos, rotation, random);
    }

    @Override
    public BlockBox getBoundingBox(StructureManager structureManager, BlockPos pos, BlockRotation rotation) {
        return this.element.getBoundingBox(structureManager, pos, rotation);
    }

    @Override
    public boolean generate(StructureManager structureManager,
                            StructureWorldAccess world,
                            StructureAccessor structureAccessor,
                            ChunkGenerator chunkGenerator,
                            BlockPos pos,
                            BlockPos blockPos,
                            BlockRotation rotation,
                            BlockBox box,
                            Random random,
                            boolean keepJigsaws) {
        return this.element.generate(structureManager,
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
    public StructurePoolElementType<?> getType() {
        return JigsortStructurePoolElementTypes.PRIORITISED_POOL_ELEMENT;
    }

    @Override
    public StructurePoolElement setProjection(StructurePool.Projection projection) {
        super.setProjection(projection);
        this.element.setProjection(projection);
        return this;
    }

    @Override
    public StructurePool.Projection getProjection() {
        return this.element.getProjection();
    }

    @Override
    public int getGroundLevelDelta() {
        return this.element.getGroundLevelDelta();
    }

    @Override
    public BlockBox getCustomBoundingBox(StructureManager structureManager, BlockPos pos, BlockRotation rotation) {
        return this.element.getCustomBoundingBox(structureManager, pos, rotation);
    }

    @Override
    public int getPriority() {
        return this.priority;
    }
}
