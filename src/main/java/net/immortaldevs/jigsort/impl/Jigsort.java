package net.immortaldevs.jigsort.impl;

import net.fabricmc.api.ModInitializer;
import net.immortaldevs.jigsort.api.JigsortStructurePoolElementTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class Jigsort implements ModInitializer {
    public static final String JIGSORT = "jigsort";

    @Override
    public void onInitialize() {
        Registry.register(Registry.STRUCTURE_POOL_ELEMENT,
                id("prioritised_pool_element"),
                JigsortStructurePoolElementTypes.PRIORITISED_POOL_ELEMENT);
    }

    public static Identifier id(String path) {
        return new Identifier(JIGSORT, path);
    }
}
