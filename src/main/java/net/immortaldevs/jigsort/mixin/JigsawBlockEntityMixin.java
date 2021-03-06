package net.immortaldevs.jigsort.mixin;

import net.immortaldevs.jigsort.impl.JigsortJigsawBlockEntity;
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
    private int priority = 0;

    @Unique
    private int immediateChance = 0;

    @Unique
    private int cost = 0;

    @Unique
    private ConflictMode conflictMode = ConflictMode.DEFAULT;

    @Inject(method = "writeNbt",
            at = @At("TAIL"))
    private void writeNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("priority", this.priority);
        nbt.putInt("immediate_chance", this.immediateChance);
        nbt.putInt("cost", this.cost);
        nbt.putString("conflict_mode", this.conflictMode.asString());
    }

    @Inject(method = "readNbt",
            at = @At("TAIL"))
    private void readNbt(NbtCompound nbt, CallbackInfo ci) {
        this.priority = nbt.getInt("priority");
        this.immediateChance = nbt.getInt("immediate_chance");
        this.cost = nbt.getInt("cost");
        this.conflictMode = ConflictMode.byName(nbt.getString("conflict_mode"));
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
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
    public int getCost() {
        return this.cost;
    }

    @Override
    public void setCost(int cost) {
        this.cost = cost;
    }

    @Override
    public ConflictMode getConflictMode() {
        return this.conflictMode;
    }

    @Override
    public void setConflictMode(ConflictMode mode) {
        this.conflictMode = mode;
    }
}
