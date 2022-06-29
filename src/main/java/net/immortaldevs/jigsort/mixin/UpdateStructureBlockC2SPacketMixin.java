package net.immortaldevs.jigsort.mixin;

import net.immortaldevs.jigsort.impl.JigsortUpdateStructureBlockC2SPacket;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.UpdateStructureBlockC2SPacket;
import net.minecraft.util.math.BlockBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

import static net.minecraft.util.math.MathHelper.clamp;

@Mixin(UpdateStructureBlockC2SPacket.class)
public abstract class UpdateStructureBlockC2SPacketMixin implements JigsortUpdateStructureBlockC2SPacket {
    @Unique
    private @Nullable BlockBox customBoundingBox;

    @Unique
    private boolean invertVoids;

    @Inject(method = "<init>(Lnet/minecraft/network/PacketByteBuf;)V",
            at = @At("TAIL"))
    private void init(PacketByteBuf buf, CallbackInfo ci) {
        if (buf.readBoolean()) this.customBoundingBox = new BlockBox(
                clamp(buf.readByte(), -48, 48),
                clamp(buf.readByte(), -48, 48),
                clamp(buf.readByte(), -48, 48),
                clamp(buf.readByte(), -48, 48),
                clamp(buf.readByte(), -48, 48),
                clamp(buf.readByte(), -48, 48));
        else this.customBoundingBox = null;
        this.invertVoids = buf.readBoolean();
    }

    @Inject(method = "write",
            at = @At("TAIL"))
    private void write(PacketByteBuf buf, CallbackInfo ci) {
        if (this.customBoundingBox != null) {
            buf.writeBoolean(true);
            buf.writeByte(this.customBoundingBox.getMinX());
            buf.writeByte(this.customBoundingBox.getMinY());
            buf.writeByte(this.customBoundingBox.getMinZ());
            buf.writeByte(this.customBoundingBox.getMaxX());
            buf.writeByte(this.customBoundingBox.getMaxY());
            buf.writeByte(this.customBoundingBox.getMaxZ());
        } else buf.writeBoolean(false);
        buf.writeBoolean(this.invertVoids);
    }

    @Override
    public @Nullable BlockBox getCustomBoundingBox() {
        return this.customBoundingBox;
    }

    @Override
    public void setCustomBoundingBox(@Nullable BlockBox boundingBox) {
        this.customBoundingBox = boundingBox;
    }

    @Override
    public boolean getInvertVoids() {
        return this.invertVoids;
    }

    @Override
    public void setInvertVoids(boolean invert) {
        this.invertVoids = invert;
    }
}
