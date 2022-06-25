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
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.UpdateJigsawC2SPacket;
import net.minecraft.text.*;
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
    private static final Text PRIORITY_TEXT = Text.translatable("jigsaw_block.priority");

    @Unique
    private static final Text IMMEDIATE_CHANCE_TEXT = Text.translatable("jigsaw_block.immediate_chance");

    @Unique
    private static final Text PERCENT_TEXT = Text.literal("%");

    @Unique
    private static final Text UNUSED_TEXT = Text.literal("WIP");

    @Unique
    private static final Text CONFLICT_MODE_TEXT = Text.translatable("jigsaw_block.conflict_mode");

    @Unique
    private TextFieldWidget priorityField;

    @Unique
    private TextFieldWidget immediateChanceField;

    @Unique
    private TextFieldWidget unusedField;

    @Unique
    private CyclingButtonWidget<ConflictMode> conflictModeButton;

    @Unique
    private CyclingButtonWidget<Boolean> unusedButton;

    private JigsawBlockScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "<clinit>",
            at = @At("TAIL"))
    private static void clinit(CallbackInfo ci) {
        JOINT_LABEL_TEXT = Text.translatable("jigsaw_block.jigsort_joint_label");
    }

    @Inject(method = "tick",
            at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        this.priorityField.tick();
    }

    @ModifyOperand(method = "updateServer",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V"))
    private UpdateJigsawC2SPacket updateServer(UpdateJigsawC2SPacket packet) {
        JigsortUpdateJigsawC2SPacket jigsortPacket = ((JigsortUpdateJigsawC2SPacket) packet);
        jigsortPacket.setPriority(parseInt(this.priorityField.getText()));
        jigsortPacket.setImmediateChance(parseInt(this.immediateChanceField.getText()));
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
                100,
                20,
                PRIORITY_TEXT);
        this.priorityField.setMaxLength(9);
        this.priorityField.setText(String.valueOf(jigsortJigsaw.getPriority()));
        this.priorityField.setChangedListener(text -> this.updateDoneButtonState());
        this.addSelectableChild(this.priorityField);

        this.immediateChanceField = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 52,
                160,
                100,
                20,
                IMMEDIATE_CHANCE_TEXT);
        this.immediateChanceField.setMaxLength(9);
        this.immediateChanceField.setText(String.valueOf(jigsortJigsaw.getImmediateChance()));
        this.immediateChanceField.setChangedListener(text -> this.updateDoneButtonState());
        this.addSelectableChild(this.immediateChanceField);

        this.unusedField = new TextFieldWidget(this.textRenderer,
                this.width / 2 + 48,
                160,
                100,
                20,
                UNUSED_TEXT);
        this.unusedField.active = false;
        this.addSelectableChild(this.immediateChanceField);

        this.unusedButton = this.addDrawableChild(
                CyclingButtonWidget.onOffBuilder(false)
                        .build(this.width / 2 - 50, 185, 100, 20, UNUSED_TEXT));
        this.unusedButton.active = false;

        this.conflictModeButton = this.addDrawableChild(CyclingButtonWidget.builder(ConflictMode::asText)
                .values(ConflictMode.values())
                .initially(jigsortJigsaw.getConflictMode())
                .build(this.width / 2 + 54, 185, 100, 20, CONFLICT_MODE_TEXT));
    }

    @ModifyOperand(method = "updateDoneButtonState",
            slice = @Slice(
                    from = @At(value = "FIELD",
                            target = "Lnet/minecraft/client/gui/screen/ingame/JigsawBlockScreen;poolField:Lnet/minecraft/client/gui/widget/TextFieldWidget;")),
            at = @At(value = "INVOKE",
                    shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/util/Identifier;isValid(Ljava/lang/String;)Z",
                    ordinal = 0))
    private boolean updateDoneButtonState(boolean valid) {
        if (!valid) return false;
        try {
            Integer.parseInt(this.priorityField.getText());
            Integer.parseInt(this.immediateChanceField.getText());
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    @Redirect(method = "resize",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/JigsawBlockScreen;init(Lnet/minecraft/client/MinecraftClient;II)V"))
    private void init(JigsawBlockScreen instance, MinecraftClient client, int width, int height) {
        String priority = this.priorityField.getText();
        String immediateChance = this.immediateChanceField.getText();
        String unused = this.unusedField.getText();
        Boolean unused0 = this.unusedButton.getValue();
        ConflictMode mode = this.conflictModeButton.getValue();
        instance.init(client, width, height);
        this.priorityField.setText(priority);
        this.immediateChanceField.setText(immediateChance);
        this.unusedField.setText(unused);
        this.unusedButton.setValue(unused0);
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

        JigsawBlockScreen.drawTextWithShadow(matrices,
                this.textRenderer,
                IMMEDIATE_CHANCE_TEXT,
                this.width / 2 - 53,
                150,
                0xA0A0A0);

        this.immediateChanceField.render(matrices, mouseX, mouseY, delta);

        JigsawBlockScreen.drawTextWithShadow(matrices,
                this.textRenderer,
                PERCENT_TEXT,
                this.width / 2 + 39,
                166,
                0x707070);

        JigsawBlockScreen.drawTextWithShadow(matrices,
                this.textRenderer,
                UNUSED_TEXT,
                this.width / 2 + 47,
                150,
                0x7FA0A0A0);

        this.unusedField.render(matrices, mouseX, mouseY, delta);
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
                    ordinal = 0))
    private boolean makeVisible(boolean value) {
        return true;
    }

    @ModifyOperand(method = "render",
            at = @At(value = "INVOKE",
                    shift = At.Shift.AFTER,
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
