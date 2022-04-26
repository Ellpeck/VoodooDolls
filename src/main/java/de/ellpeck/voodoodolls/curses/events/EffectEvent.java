package de.ellpeck.voodoodolls.curses.events;

import de.ellpeck.voodoodolls.curses.Curse;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ForgeConfigSpec;

public class EffectEvent extends CurseEvent {

    private final Effect effect;
    private final int effectLevel;
    private final int defaultMinimumMinutes;
    private final int defaultMaximumMinutes;

    private ForgeConfigSpec.ConfigValue<Integer> minimumMinutes;
    private ForgeConfigSpec.ConfigValue<Integer> maximumMinutes;

    public EffectEvent(String id, Badness defaultBadness, double defaultChance, Effect effect, int effectLevel, int defaultMinimumMinutes, int defaultMaximumMinutes) {
        super(id, defaultBadness, defaultChance);
        this.effect = effect;
        this.effectLevel = effectLevel;
        this.defaultMinimumMinutes = defaultMinimumMinutes;
        this.defaultMaximumMinutes = defaultMaximumMinutes;
    }

    @Override
    public void occur(PlayerEntity player, Curse curse) {
        int minutes = MathHelper.nextInt(player.getRandom(), this.minimumMinutes.get(), this.maximumMinutes.get());
        if (minutes > 0)
            player.addEffect(new EffectInstance(this.effect, minutes * 20 * 60, this.effectLevel));
    }

    @Override
    public void setupConfig(ForgeConfigSpec.Builder config) {
        super.setupConfig(config);
        this.minimumMinutes = config
                .comment("The minimum amount of minutes of the effect that the " + this.id + " event causes.")
                .define("minimum_minutes", this.defaultMinimumMinutes);
        this.maximumMinutes = config
                .comment("The maximum amount of minutes of the effect that the " + this.id + " event causes.")
                .define("maximum_minutes", this.defaultMaximumMinutes);
    }
}
