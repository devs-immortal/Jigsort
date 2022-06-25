package net.immortaldevs.jigsort.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static net.minecraft.block.Block.FORCE_STATE;
import static net.minecraft.block.Block.NOTIFY_LISTENERS;

public class BoxPoolElement extends ConnectingPoolElement {
    public static final Codec<BoxPoolElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    projectionGetter(),
                    boundingBoxGetter(),
                    jigsawsGetter(),
                    stateProviderGetter())
            .apply(instance, BoxPoolElement::new));

    protected final BlockStateProvider stateProvider;

    protected static <E extends BoxPoolElement> RecordCodecBuilder<E, BlockStateProvider> stateProviderGetter() {
        return BlockStateProvider.TYPE_CODEC.fieldOf("state_provider")
                .forGetter(e -> e.stateProvider);
    }

    public BoxPoolElement(StructurePool.Projection projection,
                          BlockBox boundingBox,
                          List<JigsawInfo> jigsaws,
                          BlockStateProvider stateProvider) {
        super(projection, boundingBox, jigsaws);
        this.stateProvider = stateProvider;
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
        BlockBox area = this.getBoundingBox(structureTemplateManager, pos, rotation);
        int maxX = min(area.getMaxX(), box.getMaxX());
        int maxY = min(area.getMaxY(), box.getMaxY());
        int maxZ = min(area.getMaxZ(), box.getMaxZ());
        BlockPos.Mutable position = new BlockPos.Mutable();
        boolean changed = false;
        for (int x = max(area.getMinX(), box.getMinX()); x <= maxX; x++) {
            for (int y = max(area.getMinY(), box.getMinY()); y <= maxY; y++) {
                for (int z = max(area.getMinZ(), box.getMinZ()); z <= maxZ; z++) {
                    BlockState state = this.stateProvider.getBlockState(random, position.set(x, y, z));
                    if (state.isOf(Blocks.STRUCTURE_VOID)) continue;
                    world.setBlockState(position, state, NOTIFY_LISTENERS | FORCE_STATE);
                    changed = true;
                }
            }
        }

        return changed;
    }

    @Override
    public StructurePoolElementType<?> getType() {
        return JigsortStructurePoolElementTypes.BOX_POOL_ELEMENT;
    }

    @Override
    public String toString() {
        return "Box[" + this.stateProvider + "]";
    }
}
