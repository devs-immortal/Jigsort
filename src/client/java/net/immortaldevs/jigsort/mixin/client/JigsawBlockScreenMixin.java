package net.immortaldevs.jigsort.mixin.client;

import net.immortaldevs.divineintervention.injection.ModifyOperand;
import net.immortaldevs.jigsort.impl.JigsortJigsawBlockEntity;
import net.immortaldevs.jigsort.impl.JigsortJigsawBlockEntity.ConflictMode;
import net.immortaldevs.jigsort.impl.JigsortUpdateJigsawC2SPacket;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.JigsawBlockScreen;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.UpdateJigsawC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JigsawBlockScreen.class)
public abstract class JigsawBlockScreenMixin extends Screen {
    @Shadow
    @Final
    @Mutable
    private static Text JOINT_LABEL_TEXT;

    @Shadow
    @Final
    private JigsawBlockEntity jigsaw;

    @Shadow
    protected abstract void updateDoneButtonState();

    @Unique
    private static final Text PRIORITY_TEXT = new TranslatableText("jigsaw_block.priority");

    @Unique
    private static final Text CONFLICT_MODE_TEXT = new TranslatableText("jigsaw_block.conflict_mode");

    @Unique
    private TextFieldWidget priorityField;

    @Unique
    private CyclingButtonWidget<ConflictMode> conflictModeButton;

    @Unique
    private double immediateChance = 0.0;

    private JigsawBlockScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "<clinit>",
            at = @At("TAIL"))
    private static void clinit(CallbackInfo ci) {
        JOINT_LABEL_TEXT = new TranslatableText("jigsaw_block.jigsort_joint_label");
    }

    @Inject(method = "tick",
            at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        this.priorityField.tick();
    }

    @ModifyOperand(method = "updateServer",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V",
                    shift = At.Shift.BEFORE))
    private UpdateJigsawC2SPacket updateServer(UpdateJigsawC2SPacket packet) {
        JigsortUpdateJigsawC2SPacket jigsortPacket = ((JigsortUpdateJigsawC2SPacket) packet);
        jigsortPacket.setImmediateChance((int) Math.round(this.immediateChance * 100.0));
        jigsortPacket.setPriority(parseInt(this.priorityField.getText()));
        jigsortPacket.setConflictMode(this.conflictModeButton.getValue());
        return packet;
    }

    @Inject(method = "init",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/JigsawBlockScreen;setInitialFocus(Lnet/minecraft/client/gui/Element;)V"))
    private void init(CallbackInfo ci) {
        JigsortJigsawBlockEntity jigsortJigsaw = (JigsortJigsawBlockEntity) this.jigsaw;

        this.priorityField = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 152,
                160,
                300,
                20,
                PRIORITY_TEXT);

        this.priorityField.setMaxLength(9);
        this.priorityField.setText(String.valueOf(jigsortJigsaw.getPriority()));
        this.priorityField.setChangedListener(name -> this.updateDoneButtonState());
        this.addSelectableChild(this.priorityField);

        this.conflictModeButton = this.addDrawableChild(CyclingButtonWidget.builder(ConflictMode::asText)
                .values(ConflictMode.values())
                .initially(jigsortJigsaw.getConflictMode())
                .build(this.width / 2 + 54, 185, 100, 20, CONFLICT_MODE_TEXT));

        this.addDrawableChild(new SliderWidget(this.width / 2 - 50,
                185,
                100,
                20,
                LiteralText.EMPTY,
                jigsortJigsaw.getImmediateChance() / 100.0) {
            {
                this.updateMessage();
                this.applyValue();
            }

            @Override
            protected void updateMessage() {
                this.setMessage(new TranslatableText("jigsaw_block.immediate_chance",
                        Math.round(this.value * 100.0)));
            }

            @Override
            protected void applyValue() {
                JigsawBlockScreenMixin.this.immediateChance = this.value;
            }
        });
    }

    @ModifyOperand(method = "updateDoneButtonState",
            slice = @Slice(
                    from = @At(value = "FIELD",
                            target = "Lnet/minecraft/client/gui/screen/ingame/JigsawBlockScreen;poolField:Lnet/minecraft/client/gui/widget/TextFieldWidget;")),
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/Identifier;isValid(Ljava/lang/String;)Z",
                    ordinal = 0))
    private boolean updateDoneButtonState(boolean valid) {
        if (!valid) return false;
        try {
            Integer.parseInt(this.priorityField.getText());
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    @Redirect(method = "resize",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/JigsawBlockScreen;init(Lnet/minecraft/client/MinecraftClient;II)V"))
    private void init(JigsawBlockScreen instance, MinecraftClient client, int width, int height) {
        double immediateChance = this.immediateChance;
        String priority = this.priorityField.getText();
        ConflictMode mode = this.conflictModeButton.getValue();
        instance.init(client, width, height);
        this.immediateChance = immediateChance;
        this.priorityField.setText(priority);
        this.conflictModeButton.setValue(mode);
    }

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V"))
    private void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        JigsawBlockScreen.drawTextWithShadow(matrices,
                this.textRenderer,
                PRIORITY_TEXT,
                this.width / 2 - 153,
                150,
                0xA0A0A0);

        this.priorityField.render(matrices, mouseX, mouseY, delta);
    }

    @ModifyArg(method = "init",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/JigsawBlockScreen$1;<init>(Lnet/minecraft/client/gui/screen/ingame/JigsawBlockScreen;IIIILnet/minecraft/text/Text;D)V"),
            index = 2,
            allow = 1)
    private int lowerSlider(int y) {
        return y + 35;
    }

    @ModifyArg(method = "init",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/widget/CyclingButtonWidget$Builder;build(IIIILnet/minecraft/text/Text;Lnet/minecraft/client/gui/widget/CyclingButtonWidget$UpdateCallback;)Lnet/minecraft/client/gui/widget/CyclingButtonWidget;"),
            index = 1,
            allow = 2)
    private int lowerCyclingButton(int y) {
        return y + 35;
    }

    @ModifyArg(method = "init",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/widget/ButtonWidget;<init>(IIIILnet/minecraft/text/Text;Lnet/minecraft/client/gui/widget/ButtonWidget$PressAction;)V"),
            index = 1,
            allow = 3)
    private int lowerButton(int y) {
        return y + 35;
    }

    @ModifyArg(method = "init",
            slice = @Slice(
                    from = @At(value = "FIELD",
                            target = "Lnet/minecraft/client/gui/screen/ingame/JigsawBlockScreen;JOINT_LABEL_TEXT:Lnet/minecraft/text/Text;")),
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/widget/CyclingButtonWidget$Builder;build(IIIILnet/minecraft/text/Text;Lnet/minecraft/client/gui/widget/CyclingButtonWidget$UpdateCallback;)Lnet/minecraft/client/gui/widget/CyclingButtonWidget;",
                    ordinal = 0),
            index = 0)
    private int moveJointButton(int x) {
        return this.width / 2 - 154;
    }

    @ModifyArg(method = "init",
            slice = @Slice(
                    from = @At(value = "FIELD",
                            target = "Lnet/minecraft/client/gui/screen/ingame/JigsawBlockScreen;JOINT_LABEL_TEXT:Lnet/minecraft/text/Text;")),
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/widget/CyclingButtonWidget$Builder;build(IIIILnet/minecraft/text/Text;Lnet/minecraft/client/gui/widget/CyclingButtonWidget$UpdateCallback;)Lnet/minecraft/client/gui/widget/CyclingButtonWidget;",
                    ordinal = 0),
            index = 2)
    private int resizeJointButton(int width) {
        return 100;
    }

    @Redirect(method = "init",
            slice = @Slice(
                    from = @At(value = "FIELD",
                            target = "Lnet/minecraft/client/gui/screen/ingame/JigsawBlockScreen;JOINT_LABEL_TEXT:Lnet/minecraft/text/Text;")),
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/widget/CyclingButtonWidget$Builder;omitKeyText()Lnet/minecraft/client/gui/widget/CyclingButtonWidget$Builder;",
                    ordinal = 0))
    private <T> CyclingButtonWidget.Builder<T> omitKeyText(CyclingButtonWidget.Builder<T> instance) {
        return instance;
    }

    @ModifyOperand(method = "init",
            slice = @Slice(
                    from = @At(value = "FIELD",
                            target = "Lnet/minecraft/client/gui/screen/ingame/JigsawBlockScreen;JOINT_LABEL_TEXT:Lnet/minecraft/text/Text;")),
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/client/gui/widget/CyclingButtonWidget;visible:Z",
                    ordinal = 0,
                    shift = At.Shift.BEFORE))
    private boolean makeVisible(boolean value) {
        return true;
    }

    @ModifyOperand(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Direction$Axis;isVertical()Z"),
            allow = 1)
    private static boolean isVertical(boolean value) {
        return false;
    }

    @Unique
    private int parseInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }
}
