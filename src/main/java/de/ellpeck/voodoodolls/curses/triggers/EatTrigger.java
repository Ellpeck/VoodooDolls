package de.ellpeck.voodoodolls.curses.triggers;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EatTrigger extends CurseTrigger {

    public EatTrigger() {
        super("eat");
    }

    @SubscribeEvent
    public void onEat(LivingEntityUseItemEvent.Finish event) {
        var entity = event.getEntityLiving();
        if (!entity.level.isClientSide && entity instanceof Player player) {
            var item = event.getItem();
            if (item.isEdible())
                this.trigger(player);
        }
    }

}
