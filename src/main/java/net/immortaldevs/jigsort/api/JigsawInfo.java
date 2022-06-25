package net.immortaldevs.jigsort.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.immortaldevs.jigsort.impl.JigsortJigsawBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.enums.JigsawOrientation;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@SuppressWarnings("unused")
public record JigsawInfo(BlockPos pos, JigsawOrientation orientation, NbtCompound nbt) {
    public static final Codec<JigsawInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    BlockPos.CODEC.fieldOf("pos")
                            .forGetter(connection -> connection.pos),
                    Properties.ORIENTATION.getCodec().fieldOf("orientation")
                            .forGetter(connection -> connection.orientation),
                    NbtCompound.CODEC.fieldOf("nbt")
                            .forGetter(connection -> connection.nbt))
            .apply(instance, JigsawInfo::new));

    public static JigsawInfo of(int x, int y, int z, JigsawOrientation orientation) {
        return of(new BlockPos(x, y, z), orientation);
    }

    public static JigsawInfo of(BlockPos pos, JigsawOrientation orientation) {
        return new JigsawInfo(pos, orientation, new NbtCompound());
    }

    public JigsawInfo name(Identifier name) {
        this.nbt.putString(JigsawBlockEntity.NAME_KEY, name.toString());
        return this;
    }

    public JigsawInfo target(Identifier target) {
        this.nbt.putString(JigsawBlockEntity.TARGET_KEY, target.toString());
        return this;
    }

    public JigsawInfo pool(Identifier pool) {
        this.nbt.putString(JigsawBlockEntity.POOL_KEY, pool.toString());
        return this;
    }

    public JigsawInfo joint(JigsawBlockEntity.Joint joint) {
        this.nbt.putString(JigsawBlockEntity.JOINT_KEY, joint.asString());
        return this;
    }

    public JigsawInfo priority(int priority) {
        this.nbt.putInt("priority", priority);
        return this;
    }

    public JigsawInfo immediateChance(int chance) {
        this.nbt.putInt("immediate_chance", chance);
        return this;
    }

    public JigsawInfo conflictMode(JigsortJigsawBlockEntity.ConflictMode mode) {
        this.nbt.putString("conflict_mode", mode.asString());
        return this;
    }
}
