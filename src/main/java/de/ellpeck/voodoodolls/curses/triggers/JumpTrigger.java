package de.ellpeck.voodoodolls.curses.triggers;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class JumpTrigger extends CurseTrigger {

    public JumpTrigger() {
        super("jump");
    }

    @SubscribeEvent
    public void onJump(LivingEvent.LivingJumpEvent event) {
        var entity = event.getEntityLiving();
        if (!entity.level.isClientSide && entity instanceof Player player)
            this.trigger(player);
    }
}
