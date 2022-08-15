package de.ellpeck.voodoodolls.curses.events;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeConfigSpec;

public abstract class TimedEvent extends CurseEvent {

    private final double defaultMinimumMinutes;
    private final double defaultMaximumMinutes;

    private ForgeConfigSpec.ConfigValue<Double> minimumMinutes;
    private ForgeConfigSpec.ConfigValue<Double> maximumMinutes;

    public TimedEvent(String id, Badness defaultBadness, double defaultChance, boolean subscribeEvents, double defaultMinimumMinutes, double defaultMaximumMinutes) {
        super(id, defaultBadness, defaultChance, subscribeEvents);
        this.defaultMinimumMinutes = defaultMinimumMinutes;
        this.defaultMaximumMinutes = defaultMaximumMinutes;
    }

    @Override
    public void setupConfig(ForgeConfigSpec.Builder config) {
        super.setupConfig(config);
        this.minimumMinutes = config
                .comment("The minimum amount of minutes that the " + this.id + " event lasts for.")
                .define("minimum_minutes", this.defaultMinimumMinutes);
        this.maximumMinutes = config
                .comment("The maximum amount of minutes that the " + this.id + " event lasts for.")
                .define("maximum_minutes", this.defaultMaximumMinutes);
    }

    protected double getRandomMinutes(Player player) {
        return Mth.nextDouble(player.getRandom(), this.minimumMinutes.get(), this.maximumMinutes.get());

    }
}
