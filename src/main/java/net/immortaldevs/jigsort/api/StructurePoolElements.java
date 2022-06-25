package net.immortaldevs.jigsort.api;

import com.mojang.datafixers.util.Either;
import net.minecraft.structure.pool.FeaturePoolElement;
import net.minecraft.structure.pool.ListPoolElement;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePool.Projection;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public final class StructurePoolElements {
    public static BoundedPoolElement bounded(BlockBox boundingBox,
                                             StructurePoolElement element) {
        return bounded(Projection.RIGID, boundingBox, element);
    }

    public static BoundedPoolElement bounded(Projection projection,
                                             BlockBox boundingBox,
                                             StructurePoolElement element) {
        return new BoundedPoolElement(projection, element, boundingBox);
    }

    public static BoxPoolElement box(Vec3i size,
                                     BlockStateProvider stateProvider,
                                     JigsawInfo... connections) {
        return box(BlockBox.create(Vec3i.ZERO, size), stateProvider, connections);
    }

    public static BoxPoolElement box(Projection projection,
                                     Vec3i size,
                                     BlockStateProvider stateProvider,
                                     JigsawInfo... connections) {
        return box(projection, BlockBox.create(Vec3i.ZERO, size), stateProvider, connections);
    }

    public static BoxPoolElement box(Vec3i size,
                                     BlockStateProvider stateProvider,
                                     List<JigsawInfo> connections) {
        return box(BlockBox.create(Vec3i.ZERO, size), stateProvider, connections);
    }

    public static BoxPoolElement box(Projection projection,
                                     Vec3i size,
                                     BlockStateProvider stateProvider,
                                     List<JigsawInfo> connections) {
        return box(projection, BlockBox.create(Vec3i.ZERO, size), stateProvider, connections);
    }

    public static BoxPoolElement box(BlockBox dimensions,
                                     BlockStateProvider stateProvider,
                                     JigsawInfo... connections) {
        return box(Projection.RIGID, dimensions, stateProvider, Arrays.asList(connections));
    }

    public static BoxPoolElement box(Projection projection,
                                     BlockBox dimensions,
                                     BlockStateProvider stateProvider,
                                     JigsawInfo... connections) {
        return box(projection, dimensions, stateProvider, Arrays.asList(connections));
    }

    public static BoxPoolElement box(BlockBox dimensions,
                                     BlockStateProvider stateProvider,
                                     List<JigsawInfo> connections) {
        return box(Projection.RIGID, dimensions, stateProvider, connections);
    }

    public static BoxPoolElement box(Projection projection,
                                     BlockBox dimensions,
                                     BlockStateProvider stateProvider,
                                     List<JigsawInfo> connections) {
        return new BoxPoolElement(projection, dimensions, connections, stateProvider);
    }

    public static ConnectingPoolElement connecting(JigsawInfo... connections) {
        return connecting(Arrays.asList(connections));
    }

    public static ConnectingPoolElement connecting(BlockBox dimensions,
                                                   JigsawInfo... connections) {
        return connecting(dimensions, Arrays.asList(connections));
    }

    public static ConnectingPoolElement connecting(List<JigsawInfo> connections) {
        return connecting(BlockBox.encompassPositions(() -> connections.stream()
                .map(JigsawInfo::pos)
                .iterator()).orElseThrow(), connections);
    }

    public static ConnectingPoolElement connecting(BlockBox dimensions,
                                                   List<JigsawInfo> connections) {
        return new ConnectingPoolElement(Projection.RIGID, dimensions, connections);
    }

    public static FeaturePoolElement feature(RegistryEntry<PlacedFeature> feature) {
        return feature(feature, Projection.RIGID);
    }

    public static FeaturePoolElement feature(RegistryEntry<PlacedFeature> feature,
                                             Projection projection) {
        return new FeaturePoolElement(feature, projection) {};
    }

    public static ListPoolElement list(StructurePoolElement... elements) {
        return list(Arrays.asList(elements));
    }

    public static ListPoolElement list(Projection projection,
                                       StructurePoolElement... elements) {
        return list(projection, Arrays.asList(elements));
    }

    public static ListPoolElement list(List<StructurePoolElement> elements) {
        return list(Projection.RIGID, elements);
    }

    public static ListPoolElement list(Projection projection,
                                       List<StructurePoolElement> elements) {
        return new ListPoolElement(elements, projection);
    }

    public static PrioritisedPoolElement prioritised(StructurePoolElement element,
                                                     int priority) {
        return prioritised(Projection.RIGID, element, priority);
    }

    public static PrioritisedPoolElement prioritised(Projection projection,
                                                     StructurePoolElement element,
                                                     int priority) {
        return new PrioritisedPoolElement(projection, element, priority);
    }

    public static SinglePoolElement single(Identifier id) {
        return single(Projection.RIGID, id, StructureProcessorLists.EMPTY);
    }

    public static SinglePoolElement single(Identifier id,
                                           RegistryEntry<StructureProcessorList> processors) {
        return single(Projection.RIGID, id, processors);
    }

    public static SinglePoolElement single(Projection projection,
                                           Identifier id) {
        return single(projection, id, StructureProcessorLists.EMPTY);
    }

    public static SinglePoolElement single(Projection projection,
                                           Identifier id,
                                           RegistryEntry<StructureProcessorList> processors) {
        return new SinglePoolElement(Either.left(id), processors, projection) {};
    }
}
