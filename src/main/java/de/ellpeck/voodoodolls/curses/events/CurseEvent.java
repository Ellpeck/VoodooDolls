package de.ellpeck.voodoodolls.curses.events;

import de.ellpeck.voodoodolls.VoodooDolls;
import de.ellpeck.voodoodolls.curses.Curse;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import java.util.Map;

public abstract class CurseEvent {

    public static final Map<String, CurseEvent> EVENTS = new HashMap<>();

    public final String id;
    public ForgeConfigSpec.ConfigValue<Badness> badness;
    public ForgeConfigSpec.ConfigValue<Double> chance;

    private final Badness defaultBadness;
    private final double defaultChance;

    public CurseEvent(String id, Badness defaultBadness, double defaultChance, boolean subscribeEvents) {
        this.id = id;
        this.defaultBadness = defaultBadness;
        this.defaultChance = defaultChance;
        if (subscribeEvents)
            MinecraftForge.EVENT_BUS.register(this);
    }

    public abstract void occur(PlayerEntity player, Curse curse);

    public void setupConfig(ForgeConfigSpec.Builder config) {
        this.chance = config
                .comment("The chance of the " + this.id + " event being triggered. Set to 0 to disable.")
                .define("chance", this.defaultChance);
        this.badness = config
                .comment("The badness of the " + this.id + " curse event. Determines what doll tiers can cause it.")
                .defineEnum("badness", this.defaultBadness);
    }

    public boolean isEnabled() {
        return this.chance.get() > 0;
    }

    public TranslationTextComponent getDisplayName() {
        return new TranslationTextComponent("curse_event." + VoodooDolls.ID + "." + this.id);
    }

    public static void register(CurseEvent event) {
        if (EVENTS.put(event.id, event) != null)
            throw new IllegalArgumentException("An event with id " + event.id + " is already registered");
    }

    protected static void teleportFancy(PlayerEntity player, BlockPos pos) {
        player.teleportTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        player.level.broadcastEntityEvent(player, (byte) 46);
        player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1, 1);
        player.level.playSound(null, player.xOld, player.yOld, player.zOld, SoundEvents.ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1, 1);
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
