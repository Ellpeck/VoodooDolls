package de.ellpeck.voodoodolls.curses.events;

import de.ellpeck.voodoodolls.curses.Curse;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

public class EffectEvent extends TimedEvent {

    private final MobEffect effect;
    private final int effectLevel;

    public EffectEvent(String id, Badness defaultBadness, double defaultChance, MobEffect effect, int effectLevel, double defaultMinimumMinutes, double defaultMaximumMinutes) {
        super(id, defaultBadness, defaultChance, false, defaultMinimumMinutes, defaultMaximumMinutes);
        this.effect = effect;
        this.effectLevel = effectLevel;
    }

    @Override
    public void occur(Player player, Curse curse) {
        if (player.level.isClientSide)
            return;
        var minutes = this.getRandomMinutes(player);
        if (minutes > 0)
            player.addEffect(new MobEffectInstance(this.effect, (int) (minutes * 20 * 60), this.effectLevel));
    }

}
