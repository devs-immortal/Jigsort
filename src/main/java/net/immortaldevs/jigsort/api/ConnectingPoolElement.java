package net.immortaldevs.jigsort.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.immortaldevs.jigsort.impl.Util;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.StructureTemplate.StructureBlockInfo;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ConnectingPoolElement extends StructurePoolElement {
    public static final Codec<ConnectingPoolElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    projectionGetter(),
                    boundingBoxGetter(),
                    jigsawsGetter())
            .apply(instance, ConnectingPoolElement::new));

    protected final BlockBox boundingBox;
    protected final List<JigsawInfo> jigsaws;

    protected static <E extends ConnectingPoolElement> RecordCodecBuilder<E, BlockBox> boundingBoxGetter() {
        return BlockBox.CODEC.fieldOf("bounding_box")
                .forGetter(e -> e.boundingBox);
    }

    protected static <E extends ConnectingPoolElement> RecordCodecBuilder<E, List<JigsawInfo>> jigsawsGetter() {
        return JigsawInfo.CODEC.listOf().fieldOf("jigsaws")
                .forGetter(e -> e.jigsaws);
    }

    public ConnectingPoolElement(StructurePool.Projection projection,
                                 BlockBox boundingBox,
                                 List<JigsawInfo> jigsaws) {
        super(projection);
        this.boundingBox = boundingBox;
        this.jigsaws = jigsaws;
    }

    @Override
    public Vec3i getStart(StructureTemplateManager structureTemplateManager, BlockRotation rotation) {
        return switch (rotation) {
            case CLOCKWISE_90, COUNTERCLOCKWISE_90 -> new Vec3i(this.boundingBox.getBlockCountZ(),
                    this.boundingBox.getBlockCountY(),
                    this.boundingBox.getBlockCountX());
            default -> new Vec3i(this.boundingBox.getBlockCountX(),
                    this.boundingBox.getBlockCountY(),
                    this.boundingBox.getBlockCountZ());
        };
    }

    @Override
    public List<StructureBlockInfo> getStructureBlockInfos(StructureTemplateManager structureTemplateManager,
                                                           BlockPos pos,
                                                           BlockRotation rotation,
                                                           Random random) {
        StructureBlockInfo[] infos = new StructureBlockInfo[this.jigsaws.size()];
        for (int i = 0; i < this.jigsaws.size(); i++) {
            JigsawInfo connection = this.jigsaws.get(i);
            infos[i] = new StructureBlockInfo(connection.pos().rotate(rotation).add(pos),
                    Blocks.JIGSAW.getDefaultState()
                            .with(Properties.ORIENTATION, connection.orientation())
                            .rotate(rotation),
                    connection.nbt());
        }

        Util.shuffle(infos, random);
        Arrays.sort(infos, Comparator.comparingInt(info -> info.nbt.getInt("priority")));

        return Arrays.asList(infos);
    }

    @Override
    public BlockBox getBoundingBox(StructureTemplateManager structureTemplateManager, BlockPos pos, BlockRotation rotation) {
        return Util.transformBox(this.boundingBox, pos, rotation);
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
        return true;
    }

    @Override
    public StructurePoolElementType<?> getType() {
        return JigsortStructurePoolElementTypes.CONNECTING_POOL_ELEMENT;
    }

    @Override
    public String toString() {
        return "Connecting[" + this.jigsaws + "]";
    }
}
