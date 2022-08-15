package de.ellpeck.voodoodolls.curses.triggers;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class KillMobTrigger extends CurseTrigger {

    public KillMobTrigger() {
        super("kill_mob");
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        var source = event.getSource();
        var killer = source.getEntity();
        if (killer != null && !killer.level.isClientSide && killer instanceof Player player)
            this.trigger(player);
    }
}
