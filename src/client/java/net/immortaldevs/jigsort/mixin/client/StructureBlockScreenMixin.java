package net.immortaldevs.jigsort.mixin.client;

import net.immortaldevs.divineintervention.injection.ModifyOperand;
import net.immortaldevs.jigsort.impl.JigsortStructureBlockBlockEntity;
import net.immortaldevs.jigsort.impl.JigsortUpdateStructureBlockC2SPacket;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.StructureBlockScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.UpdateStructureBlockC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.MathHelper;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.ToIntFunction;

@Mixin(StructureBlockScreen.class)
public abstract class StructureBlockScreenMixin extends Screen {
    @Shadow
    @Final
    private StructureBlockBlockEntity structureBlock;

    @Unique
    private static final Text BB_MIN_X_TEXT = new TranslatableText("structure_block.custom_bounding_box.min_x");

    @Unique
    private static final Text BB_MIN_Y_TEXT = new TranslatableText("structure_block.custom_bounding_box.min_y");

    @Unique
    private static final Text BB_MIN_Z_TEXT = new TranslatableText("structure_block.custom_bounding_box.min_z");

    @Unique
    private static final Text BB_MAX_X_TEXT = new TranslatableText("structure_block.custom_bounding_box.max_x");

    @Unique
    private static final Text BB_MAX_Y_TEXT = new TranslatableText("structure_block.custom_bounding_box.max_y");

    @Unique
    private static final Text BB_MAX_Z_TEXT = new TranslatableText("structure_block.custom_bounding_box.max_z");

    @Unique
    private TextFieldWidget inputBoundingBoxMinX;

    @Unique
    private TextFieldWidget inputBoundingBoxMinY;

    @Unique
    private TextFieldWidget inputBoundingBoxMinZ;

    @Unique
    private TextFieldWidget inputBoundingBoxMaxX;

    @Unique
    private TextFieldWidget inputBoundingBoxMaxY;

    @Unique
    private TextFieldWidget inputBoundingBoxMaxZ;

    private StructureBlockScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "tick",
            at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        this.inputBoundingBoxMinX.tick();
        this.inputBoundingBoxMinY.tick();
        this.inputBoundingBoxMinZ.tick();
        this.inputBoundingBoxMaxX.tick();
        this.inputBoundingBoxMaxY.tick();
        this.inputBoundingBoxMaxZ.tick();
    }

    @Inject(method = "init",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/StructureBlockScreen;updateRotationButton()V"),
            allow = 1)
    private void init(CallbackInfo ci) {
        this.inputBoundingBoxMinX = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 32,
                80,
                40,
                20,
                BB_MIN_X_TEXT);

        this.inputBoundingBoxMinX.setMaxLength(15);
        this.inputBoundingBoxMinX.setText(this.getInitialText(BlockBox::getMinX));
        this.addSelectableChild(this.inputBoundingBoxMinX);

        this.inputBoundingBoxMinY = new TextFieldWidget(this.textRenderer,
                this.width / 2 + 8,
                80,
                40,
                20,
                BB_MIN_Y_TEXT);

        this.inputBoundingBoxMinY.setMaxLength(15);
        this.inputBoundingBoxMinY.setText(this.getInitialText(BlockBox::getMinY));
        this.addSelectableChild(this.inputBoundingBoxMinY);

        this.inputBoundingBoxMinZ = new TextFieldWidget(this.textRenderer,
                this.width / 2 + 48,
                80,
                40,
                20,
                BB_MIN_Z_TEXT);

        this.inputBoundingBoxMinZ.setMaxLength(15);
        this.inputBoundingBoxMinZ.setText(this.getInitialText(BlockBox::getMinZ));
        this.addSelectableChild(this.inputBoundingBoxMinZ);

        this.inputBoundingBoxMaxX = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 32,
                120,
                40,
                20,
                BB_MAX_X_TEXT);

        this.inputBoundingBoxMaxX.setMaxLength(15);
        this.inputBoundingBoxMaxX.setText(this.getInitialText(BlockBox::getMaxX));
        this.addSelectableChild(this.inputBoundingBoxMaxX);

        this.inputBoundingBoxMaxY = new TextFieldWidget(this.textRenderer,
                this.width / 2 + 8,
                120,
                40,
                20,
                BB_MAX_Y_TEXT);

        this.inputBoundingBoxMaxY.setMaxLength(15);
        this.inputBoundingBoxMaxY.setText(this.getInitialText(BlockBox::getMaxY));
        this.addSelectableChild(this.inputBoundingBoxMaxY);

        this.inputBoundingBoxMaxZ = new TextFieldWidget(this.textRenderer,
                this.width / 2 + 48,
                120,
                40,
                20,
                BB_MAX_Z_TEXT);

        this.inputBoundingBoxMaxZ.setMaxLength(15);
        this.inputBoundingBoxMaxZ.setText(this.getInitialText(BlockBox::getMaxZ));
        this.addSelectableChild(this.inputBoundingBoxMaxZ);
    }

    @Redirect(method = "resize",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/StructureBlockScreen;init(Lnet/minecraft/client/MinecraftClient;II)V"))
    private void init(StructureBlockScreen instance, MinecraftClient client, int width, int height) {
        String conflictPosXText = this.inputBoundingBoxMinX.getText();
        String conflictPosYText = this.inputBoundingBoxMinY.getText();
        String conflictPosZText = this.inputBoundingBoxMinZ.getText();
        String conflictSizeXText = this.inputBoundingBoxMaxX.getText();
        String conflictSizeYText = this.inputBoundingBoxMaxY.getText();
        String conflictSizeZText = this.inputBoundingBoxMaxZ.getText();
        instance.init(client, width, height);
        this.inputBoundingBoxMinX.setText(conflictPosXText);
        this.inputBoundingBoxMinY.setText(conflictPosYText);
        this.inputBoundingBoxMinZ.setText(conflictPosZText);
        this.inputBoundingBoxMaxX.setText(conflictSizeXText);
        this.inputBoundingBoxMaxY.setText(conflictSizeYText);
        this.inputBoundingBoxMaxZ.setText(conflictSizeZText);
    }

    @Inject(method = "updateWidgets",
            at = @At("TAIL"))
    private void updateWidgets(StructureBlockMode mode, CallbackInfo ci) {
        boolean visible = mode == StructureBlockMode.SAVE;
        this.inputBoundingBoxMinX.setVisible(visible);
        this.inputBoundingBoxMinY.setVisible(visible);
        this.inputBoundingBoxMinZ.setVisible(visible);
        this.inputBoundingBoxMaxX.setVisible(visible);
        this.inputBoundingBoxMaxY.setVisible(visible);
        this.inputBoundingBoxMaxZ.setVisible(visible);
    }

    @ModifyOperand(method = "updateStructureBlock",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V",
                    shift = At.Shift.BEFORE))
    private UpdateStructureBlockC2SPacket updateStructureBlock(UpdateStructureBlockC2SPacket packet) {
        try {
            ((JigsortUpdateStructureBlockC2SPacket) packet).setCustomBoundingBox(new BlockBox(
                    getValue(this.inputBoundingBoxMinX),
                    getValue(this.inputBoundingBoxMinY),
                    getValue(this.inputBoundingBoxMinZ),
                    getValue(this.inputBoundingBoxMaxX),
                    getValue(this.inputBoundingBoxMaxY),
                    getValue(this.inputBoundingBoxMaxZ)));
        } catch (NumberFormatException e) {
            ((JigsortUpdateStructureBlockC2SPacket) packet).setCustomBoundingBox(null);
        }

        return packet;
    }

    @Inject(method = "render",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/client/gui/screen/ingame/StructureBlockScreen;DETECT_SIZE_TEXT:Lnet/minecraft/text/Text;",
                    ordinal = 0))
    private void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.inputBoundingBoxMinX.render(matrices, mouseX, mouseY, delta);
        this.inputBoundingBoxMinY.render(matrices, mouseX, mouseY, delta);
        this.inputBoundingBoxMinZ.render(matrices, mouseX, mouseY, delta);
        this.inputBoundingBoxMaxX.render(matrices, mouseX, mouseY, delta);
        this.inputBoundingBoxMaxY.render(matrices, mouseX, mouseY, delta);
        this.inputBoundingBoxMaxZ.render(matrices, mouseX, mouseY, delta);
    }

    @ModifyConstant(method = "init",
            slice = @Slice(
                    from = @At(value = "CONSTANT",
                            args = "stringValue=structure_block.position.x"),
                    to = @At(value = "CONSTANT",
                            args = "stringValue=structure_block.size.y")),
            constant = @Constant(intValue = 72),
            allow = 2)
    private int moveYLeft(int x) {
        return x + 40;
    }

    @ModifyConstant(method = "init",
            slice = @Slice(
                    from = @At(value = "CONSTANT",
                            args = "stringValue=structure_block.position.y"),
                    to = @At(value = "CONSTANT",
                            args = "stringValue=structure_block.size.z")),
            constant = @Constant(intValue = 8),
            allow = 2)
    private int moveZLeft(int x) {
        return x - 80;
    }

    @ModifyArg(method = "init",
            slice = @Slice(
                    from = @At(value = "CONSTANT",
                            args = "stringValue=structure_block.position.x"),
                    to = @At(value = "FIELD",
                            target = "Lnet/minecraft/client/gui/screen/ingame/StructureBlockScreen;inputSizeZ:Lnet/minecraft/client/gui/widget/TextFieldWidget;",
                            opcode = Opcodes.PUTFIELD)),
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;<init>(Lnet/minecraft/client/font/TextRenderer;IIIILnet/minecraft/text/Text;)V"),
            index = 3,
            allow = 6)
    private int resize(int width) {
        return 40;
    }

    @Unique
    private String getInitialText(ToIntFunction<BlockBox> getter) {
        JigsortStructureBlockBlockEntity structureBlock = (JigsortStructureBlockBlockEntity) this.structureBlock;
        return structureBlock.getCustomBoundingBox() == null
                ? "~"
                : String.valueOf(getter.applyAsInt(structureBlock.getCustomBoundingBox()));
    }

    @Unique
    private static int getValue(TextFieldWidget textField) throws NumberFormatException {
        return MathHelper.clamp(Integer.parseInt(textField.getText()), -48, 48);
    }
}
