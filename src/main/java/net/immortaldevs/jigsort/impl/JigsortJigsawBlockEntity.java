package net.immortaldevs.jigsort.impl;

import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

import java.util.Locale;

public interface JigsortJigsawBlockEntity {
    int getPriority();

    void setPriority(int priority);

    int getImmediateChance();

    void setImmediateChance(int chance);

    ConflictMode getConflictMode();

    void setConflictMode(ConflictMode mode);

    enum ConflictMode implements StringIdentifiable {
        DEFAULT(true, true),
        QUICK(true, false),
        FORCE(false, true),
        GHOST(false, false);

        public final boolean check;
        public final boolean combine;
        private final String name = this.toString().toLowerCase(Locale.ROOT);

        ConflictMode(boolean check, boolean combine) {
            this.check = check;
            this.combine = combine;
        }

        public Text asText() {
            return Text.translatable("jigsaw_block.conflict_mode." + this.name);
        }

        @Override
        public String asString() {
            return this.name;
        }

        public static ConflictMode byName(String name) {
            return switch (name) {
                default -> DEFAULT;
                case "quick" -> QUICK;
                case "force" -> FORCE;
                case "ghost" -> GHOST;
            };
        }
    }
}
