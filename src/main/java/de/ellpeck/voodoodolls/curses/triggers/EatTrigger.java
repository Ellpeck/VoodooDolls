package de.ellpeck.voodoodolls.curses.triggers;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EatTrigger extends CurseTrigger {

    public EatTrigger() {
        super("eat");
    }

    @SubscribeEvent
    public void onEat(LivingEntityUseItemEvent.Finish event) {
        LivingEntity entity = event.getEntityLiving();
        if (!entity.level.isClientSide && entity instanceof PlayerEntity) {
            ItemStack item = event.getItem();
            if (item.isEdible())
                this.trigger((PlayerEntity) entity);
        }
    }

}
