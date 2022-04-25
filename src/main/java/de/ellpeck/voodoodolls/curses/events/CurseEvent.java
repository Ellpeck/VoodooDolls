package de.ellpeck.voodoodolls.curses.events;

import de.ellpeck.voodoodolls.VoodooDolls;
import de.ellpeck.voodoodolls.curses.Curse;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.Map;

public abstract class CurseEvent {

    public static final Map<String, CurseEvent> EVENTS = new HashMap<>();

    public final String id;
    public ForgeConfigSpec.ConfigValue<Badness> badness;
    public ForgeConfigSpec.ConfigValue<Boolean> disabled;

    private final Badness defaultBadness;

    public CurseEvent(String id, Badness defaultBadness) {
        this.id = id;
        this.defaultBadness = defaultBadness;
    }

    public abstract void occur(PlayerEntity player, Curse curse);

    public void setupConfig(ForgeConfigSpec.Builder config) {
        this.disabled = config
                .comment("Whether the " + this.id + " curse event should be disabled.")
                .define("disabled", false);
        this.badness = config
                .comment("The badness of the " + this.id + " curse event. Determines what doll tiers can cause it.")
                .defineEnum("badness", this.defaultBadness);
    }

    public TranslationTextComponent getDisplayName() {
        return new TranslationTextComponent("curse_event." + VoodooDolls.ID + "." + this.id);
    }

    public static void register(CurseEvent event) {
        if (EVENTS.put(event.id, event) != null)
            throw new IllegalArgumentException("An event with id " + event.id + " is already registered");
    }

    public enum Badness {

        BAD(true, true, false),
        WORSE(false, true, true),
        WORST(false, false, true);

        public final boolean allowedInTierOne;
        public final boolean allowedInTierTwo;
        public final boolean allowedInTierThree;

        Badness(boolean allowedInTierOne, boolean allowedInTierTwo, boolean allowedInTierThree) {
            this.allowedInTierOne = allowedInTierOne;
            this.allowedInTierTwo = allowedInTierTwo;
            this.allowedInTierThree = allowedInTierThree;
        }
    }
}
