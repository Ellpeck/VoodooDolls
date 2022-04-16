package de.ellpeck.voodoodolls.curses.triggers;

import de.ellpeck.voodoodolls.curses.Curse;
import de.ellpeck.voodoodolls.curses.CurseData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.Map;

public abstract class CurseTrigger {

    public static final Map<String, CurseTrigger> TRIGGERS = new HashMap<>();

    public final String id;
    public ForgeConfigSpec.ConfigValue<Double> chance;

    private final double defaultChance;

    public CurseTrigger(String id, double defaultChance) {
        this.id = id;
        this.defaultChance = defaultChance;
    }

    public void setupConfig(ForgeConfigSpec.Builder config) {
        this.chance = config
                .comment("The chance of the " + this.id + " trigger causing an event when it is triggered. Set to 0 to disable.")
                .define(this.id + "_chance", this.defaultChance);
    }

    protected void trigger(PlayerEntity player) {
        if (this.chance.get() <= 0)
            return;
        CurseData data = CurseData.get(player.level);
        for (Curse curse : data.getCurses(player.getUUID())) {
            if (curse.trigger == this && player.getRandom().nextFloat() <= this.chance.get())
                curse.occur();
        }
    }

    public static void register(CurseTrigger trigger) {
        if (TRIGGERS.put(trigger.id, trigger) != null)
            throw new IllegalArgumentException("A trigger with id " + trigger.id + " is already registered");
    }
}
