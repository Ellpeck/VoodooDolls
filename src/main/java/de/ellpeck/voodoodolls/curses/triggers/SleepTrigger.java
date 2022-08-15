package de.ellpeck.voodoodolls.curses.triggers;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SleepTrigger extends CurseTrigger {

    public SleepTrigger() {
        super("sleep");
    }

    @SubscribeEvent
    public void onSleepInBed(PlayerSleepInBedEvent event) {
        var player = event.getPlayer();
        if (!player.level.isClientSide)
            this.trigger(player);
    }
}
