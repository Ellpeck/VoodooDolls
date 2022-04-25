package de.ellpeck.voodoodolls.curses.triggers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class KillMobTrigger extends CurseTrigger {

    public KillMobTrigger() {
        super("kill_mob", 0.05);
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        DamageSource source = event.getSource();
        Entity killer = source.getEntity();
        if (killer != null && !killer.level.isClientSide && killer instanceof PlayerEntity)
            this.trigger((PlayerEntity) killer);
    }
}
