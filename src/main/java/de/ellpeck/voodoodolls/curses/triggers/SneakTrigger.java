package de.ellpeck.voodoodolls.curses.triggers;

import de.ellpeck.voodoodolls.VoodooDolls;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SneakTrigger extends CurseTrigger {

    public SneakTrigger() {
        super("sneak");
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        var entity = event.getEntityLiving();
        if (!entity.level.isClientSide && entity instanceof Player player) {
            var data = player.getPersistentData();
            var isSneaking = player.isCrouching();
            if (isSneaking != data.getBoolean(VoodooDolls.ID + ":was_sneaking")) {
                if (isSneaking)
                    this.trigger(player);
                data.putBoolean(VoodooDolls.ID + ":was_sneaking", isSneaking);
            }
        }
    }
}
