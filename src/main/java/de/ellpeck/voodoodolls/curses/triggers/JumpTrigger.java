package de.ellpeck.voodoodolls.curses.triggers;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class JumpTrigger extends CurseTrigger {

    public JumpTrigger() {
        super("jump");
    }

    @SubscribeEvent
    public void onJump(LivingEvent.LivingJumpEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (!entity.level.isClientSide && entity instanceof PlayerEntity)
            this.trigger((PlayerEntity) entity);
    }
}
