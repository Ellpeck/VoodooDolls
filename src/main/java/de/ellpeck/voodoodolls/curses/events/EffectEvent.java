package de.ellpeck.voodoodolls.curses.events;

import de.ellpeck.voodoodolls.curses.Curse;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;

public class EffectEvent extends TimedEvent {

    private final Effect effect;
    private final int effectLevel;

    public EffectEvent(String id, Badness defaultBadness, double defaultChance, Effect effect, int effectLevel, double defaultMinimumMinutes, double defaultMaximumMinutes) {
        super(id, defaultBadness, defaultChance, false, defaultMinimumMinutes, defaultMaximumMinutes);
        this.effect = effect;
        this.effectLevel = effectLevel;
    }

    @Override
    public void occur(PlayerEntity player, Curse curse) {
        if (player.level.isClientSide)
            return;
        double minutes = this.getRandomMinutes(player);
        if (minutes > 0)
            player.addEffect(new EffectInstance(this.effect, (int) (minutes * 20 * 60), this.effectLevel));
    }

}
