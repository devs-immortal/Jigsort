package net.immortaldevs.jigsort.mixin;

import net.immortaldevs.jigsort.impl.JigsortJigsawBlockEntity.ConflictMode;
import net.immortaldevs.jigsort.impl.JigsortUpdateJigsawC2SPacket;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.UpdateJigsawC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(UpdateJigsawC2SPacket.class)
public abstract class UpdateJigsawC2SPacketMixin implements JigsortUpdateJigsawC2SPacket {
    @Unique
    private int immediateChance;

    @Unique
    private ConflictMode conflictMode;

    @Unique
    private int priority;

    @Inject(method = "<init>(Lnet/minecraft/network/PacketByteBuf;)V",
            at = @At("TAIL"))
    private void init(PacketByteBuf buf, CallbackInfo ci) {
        this.immediateChance = buf.readInt();
        this.conflictMode = buf.readEnumConstant(ConflictMode.class);
        this.priority = buf.readInt();
    }

    @Inject(method = "write",
            at = @At("TAIL"))
    private void write(PacketByteBuf buf, CallbackInfo ci) {
        buf.writeInt(this.immediateChance);
        buf.writeEnumConstant(this.conflictMode);
        buf.writeInt(this.priority);
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
