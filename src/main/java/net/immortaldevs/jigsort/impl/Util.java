package net.immortaldevs.jigsort.impl;

import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;

public final class Util {
    public static void shuffle(Object[] array, Random random) {
        for (int i = array.length - 1; i > 0; i--) {
            int target = random.nextInt(i + 1);
            Object temp = array[i];
            array[i] = array[target];
            array[target] = temp;
        }
    }

    @SuppressWarnings("deprecation") //BlockBox::move is unsafe
    @Contract("null, _, _ -> null; !null, _, _ -> !null")
    public static @Nullable BlockBox transformBox(@Nullable BlockBox box, Vec3i translation, BlockRotation rotation) {
        return box == null ? null : switch (rotation) {
            default -> box.offset(translation.getX(), translation.getY(), translation.getZ());
            case CLOCKWISE_90 -> new BlockBox(-box.getMaxZ(),
                    box.getMinY(),
                    box.getMinX(),
                    -box.getMinZ(),
                    box.getMaxY(),
                    box.getMaxX()).move(translation);
            case CLOCKWISE_180 -> new BlockBox(-box.getMaxX(),
                    box.getMinY(),
                    -box.getMaxZ(),
                    -box.getMinX(),
                    box.getMaxY(),
                    -box.getMinZ()).move(translation);
            case COUNTERCLOCKWISE_90 -> new BlockBox(box.getMinZ(),
                    box.getMinY(),
                    -box.getMaxX(),
                    box.getMaxZ(),
                    box.getMaxY(),
                    -box.getMinX()).move(translation);
        };
    }
}
