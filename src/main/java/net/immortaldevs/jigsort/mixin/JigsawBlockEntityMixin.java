package net.immortaldevs.jigsort.mixin;

import net.immortaldevs.jigsort.JigsortJigsawBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JigsawBlockEntity.class)
public abstract class JigsawBlockEntityMixin implements JigsortJigsawBlockEntity {
    @Unique
    private int immediateChance = 0;

    @Unique
    private ConflictMode conflictMode = ConflictMode.DEFAULT;

    @Unique
    private int priority = 0;

    @Inject(method = "writeNbt",
            at = @At("TAIL"))
    private void writeNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("immediate_chance", this.immediateChance);
        nbt.putString("conflict_mode", this.conflictMode.asString());
        nbt.putInt("priority", this.priority);
    }

    @Inject(method = "readNbt",
            at = @At("TAIL"))
    private void readNbt(NbtCompound nbt, CallbackInfo ci) {
        this.immediateChance = nbt.getInt("immediate_chance");
        this.conflictMode = ConflictMode.byName(nbt.getString("conflict_mode"));
        this.priority = nbt.getInt("priority");
    }

    @Override
    public int getImmediateChance() {
        return this.immediateChance;
    }

    @Override
    public void setImmediateChance(int chance) {
        this.immediateChance = chance;
    }

    @Override
    public ConflictMode getConflictMode() {
        return this.conflictMode;
    }

    @Override
    public void setConflictMode(ConflictMode mode) {
        this.conflictMode = mode;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }
}
