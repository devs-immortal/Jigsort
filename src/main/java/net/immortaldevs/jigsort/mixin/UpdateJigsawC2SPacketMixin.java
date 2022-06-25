package net.immortaldevs.jigsort.mixin;

import net.immortaldevs.jigsort.impl.JigsortJigsawBlockEntity.ConflictMode;
import net.immortaldevs.jigsort.impl.JigsortUpdateJigsawC2SPacket;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.UpdateJigsawC2SPacket;
import net.minecraft.util.math.MathHelper;
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
        this.priority = buf.readInt();
        this.immediateChance = MathHelper.clamp(buf.readByte(), 0, 100);
        this.conflictMode = buf.readEnumConstant(ConflictMode.class);
    }

    @Inject(method = "write",
            at = @At("TAIL"))
    private void write(PacketByteBuf buf, CallbackInfo ci) {
        buf.writeInt(this.priority);
        buf.writeByte(this.immediateChance);
        buf.writeEnumConstant(this.conflictMode);
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
        this.immediateChance = MathHelper.clamp(chance, 0, 100);
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
