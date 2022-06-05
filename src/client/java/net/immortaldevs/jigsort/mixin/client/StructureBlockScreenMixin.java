package net.immortaldevs.jigsort.mixin.client;

import net.immortaldevs.divineintervention.injection.ModifyOperand;
import net.immortaldevs.jigsort.impl.JigsortStructureBlockBlockEntity;
import net.immortaldevs.jigsort.impl.JigsortUpdateStructureBlockC2SPacket;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.StructureBlockScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.UpdateStructureBlockC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.math.BlockBox;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static java.lang.Integer.parseInt;
import static net.minecraft.util.math.MathHelper.clamp;

@Mixin(StructureBlockScreen.class)
public abstract class StructureBlockScreenMixin extends Screen {
    @Shadow
    @Final
    private StructureBlockBlockEntity structureBlock;

    @Shadow
    private CyclingButtonWidget<BlockMirror> buttonMirror;

    @Shadow
    private ButtonWidget buttonRotate0;

    @Shadow
    private ButtonWidget buttonRotate90;

    @Shadow
    private ButtonWidget buttonRotate180;

    @Shadow
    private ButtonWidget buttonRotate270;

    @Unique
    private static final Text BB_MIN_X_TEXT = new TranslatableText("structure_block.custom_bounding_box.min_x");

    @Unique
    private static final Text BB_MIN_Y_TEXT = new TranslatableText("structure_block.custom_bounding_box.min_y");

    @Unique
    private static final Text BB_MIN_Z_TEXT = new TranslatableText("structure_block.custom_bounding_box.min_z");

    @Unique
    private static final Text BB_SIZE_X_TEXT = new TranslatableText("structure_block.custom_bounding_box.size_x");

    @Unique
    private static final Text BB_SIZE_Y_TEXT = new TranslatableText("structure_block.custom_bounding_box.size_y");

    @Unique
    private static final Text BB_SIZE_Z_TEXT = new TranslatableText("structure_block.custom_bounding_box.size_z");

    @Unique
    private TextFieldWidget inputBoundingBoxMinX;

    @Unique
    private TextFieldWidget inputBoundingBoxMinY;

    @Unique
    private TextFieldWidget inputBoundingBoxMinZ;

    @Unique
    private TextFieldWidget inputBoundingBoxSizeX;

    @Unique
    private TextFieldWidget inputBoundingBoxSizeY;

    @Unique
    private TextFieldWidget inputBoundingBoxSizeZ;

    private StructureBlockScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "tick",
            at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        this.inputBoundingBoxMinX.tick();
        this.inputBoundingBoxMinY.tick();
        this.inputBoundingBoxMinZ.tick();
        this.inputBoundingBoxSizeX.tick();
        this.inputBoundingBoxSizeY.tick();
        this.inputBoundingBoxSizeZ.tick();
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
        this.inputBoundingBoxMinX.setText("~");
        this.addSelectableChild(this.inputBoundingBoxMinX);

        this.inputBoundingBoxMinY = new TextFieldWidget(this.textRenderer,
                this.width / 2 + 8,
                80,
                40,
                20,
                BB_MIN_Y_TEXT);

        this.inputBoundingBoxMinY.setMaxLength(15);
        this.inputBoundingBoxMinY.setText("~");
        this.addSelectableChild(this.inputBoundingBoxMinY);

        this.inputBoundingBoxMinZ = new TextFieldWidget(this.textRenderer,
                this.width / 2 + 48,
                80,
                40,
                20,
                BB_MIN_Z_TEXT);

        this.inputBoundingBoxMinZ.setMaxLength(15);
        this.inputBoundingBoxMinZ.setText("~");
        this.addSelectableChild(this.inputBoundingBoxMinZ);

        this.inputBoundingBoxSizeX = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 32,
                120,
                40,
                20,
                BB_SIZE_X_TEXT);

        this.inputBoundingBoxSizeX.setMaxLength(15);
        this.inputBoundingBoxSizeX.setText("~");
        this.addSelectableChild(this.inputBoundingBoxSizeX);

        this.inputBoundingBoxSizeY = new TextFieldWidget(this.textRenderer,
                this.width / 2 + 8,
                120,
                40,
                20,
                BB_SIZE_Y_TEXT);

        this.inputBoundingBoxSizeY.setMaxLength(15);
        this.inputBoundingBoxSizeY.setText("~");
        this.addSelectableChild(this.inputBoundingBoxSizeY);

        this.inputBoundingBoxSizeZ = new TextFieldWidget(this.textRenderer,
                this.width / 2 + 48,
                120,
                40,
                20,
                BB_SIZE_Z_TEXT);

        this.inputBoundingBoxSizeZ.setMaxLength(15);
        this.inputBoundingBoxSizeZ.setText("~");
        this.addSelectableChild(this.inputBoundingBoxSizeZ);

        BlockBox box = ((JigsortStructureBlockBlockEntity) this.structureBlock).getCustomBoundingBox();
        if (box != null) {
            this.inputBoundingBoxMinX.setText(String.valueOf(box.getMinX()));
            this.inputBoundingBoxMinY.setText(String.valueOf(box.getMinY()));
            this.inputBoundingBoxMinZ.setText(String.valueOf(box.getMinZ()));
            this.inputBoundingBoxSizeX.setText(String.valueOf(box.getMaxX() - box.getMinX()));
            this.inputBoundingBoxSizeY.setText(String.valueOf(box.getMaxY() - box.getMinY()));
            this.inputBoundingBoxSizeZ.setText(String.valueOf(box.getMaxZ() - box.getMinZ()));
        }
    }

    @Redirect(method = "resize",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/StructureBlockScreen;init(Lnet/minecraft/client/MinecraftClient;II)V"))
    private void init(StructureBlockScreen instance, MinecraftClient client, int width, int height) {
        String minXText = this.inputBoundingBoxMinX.getText();
        String minYText = this.inputBoundingBoxMinY.getText();
        String minZText = this.inputBoundingBoxMinZ.getText();
        String sizeXText = this.inputBoundingBoxSizeX.getText();
        String sizeYText = this.inputBoundingBoxSizeY.getText();
        String sizeZText = this.inputBoundingBoxSizeZ.getText();
        instance.init(client, width, height);
        this.inputBoundingBoxMinX.setText(minXText);
        this.inputBoundingBoxMinY.setText(minYText);
        this.inputBoundingBoxMinZ.setText(minZText);
        this.inputBoundingBoxSizeX.setText(sizeXText);
        this.inputBoundingBoxSizeY.setText(sizeYText);
        this.inputBoundingBoxSizeZ.setText(sizeZText);
    }

    @Inject(method = "updateWidgets",
            at = @At("TAIL"))
    private void updateWidgets(StructureBlockMode mode, CallbackInfo ci) {
        boolean visible = mode == StructureBlockMode.SAVE;
        this.inputBoundingBoxMinX.setVisible(visible);
        this.inputBoundingBoxMinY.setVisible(visible);
        this.inputBoundingBoxMinZ.setVisible(visible);
        this.inputBoundingBoxSizeX.setVisible(visible);
        this.inputBoundingBoxSizeY.setVisible(visible);
        this.inputBoundingBoxSizeZ.setVisible(visible);
        if (visible) {
            this.buttonMirror.visible = true;
            this.buttonRotate0.visible = true;
            this.buttonRotate90.visible = true;
            this.buttonRotate180.visible = true;
            this.buttonRotate270.visible = true;
        }
    }

    @ModifyOperand(method = "updateStructureBlock",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V",
                    shift = At.Shift.BEFORE))
    private UpdateStructureBlockC2SPacket updateStructureBlock(UpdateStructureBlockC2SPacket packet) {
        try {
            int minX = clamp(parseInt(this.inputBoundingBoxMinX.getText()), -48, 48);
            int minY = clamp(parseInt(this.inputBoundingBoxMinY.getText()), -48, 48);
            int minZ = clamp(parseInt(this.inputBoundingBoxMinZ.getText()), -48, 48);
            ((JigsortUpdateStructureBlockC2SPacket) packet).setCustomBoundingBox(new BlockBox(
                    minX,
                    minY,
                    minZ,
                    clamp(parseInt(this.inputBoundingBoxSizeX.getText()), 0, 48) + minX,
                    clamp(parseInt(this.inputBoundingBoxSizeY.getText()), 0, 48) + minY,
                    clamp(parseInt(this.inputBoundingBoxSizeZ.getText()), 0, 48) + minZ));

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
        this.inputBoundingBoxSizeX.render(matrices, mouseX, mouseY, delta);
        this.inputBoundingBoxSizeY.render(matrices, mouseX, mouseY, delta);
        this.inputBoundingBoxSizeZ.render(matrices, mouseX, mouseY, delta);
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
}
