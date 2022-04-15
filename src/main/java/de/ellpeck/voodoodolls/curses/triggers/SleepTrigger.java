package de.ellpeck.voodoodolls.curses.triggers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SleepTrigger extends CurseTrigger {

    public SleepTrigger() {
        super("sleep");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onSleepInBed(PlayerSleepInBedEvent event) {
        PlayerEntity player = event.getPlayer();
        if (!player.level.isClientSide)
            this.trigger(player);
    }
}