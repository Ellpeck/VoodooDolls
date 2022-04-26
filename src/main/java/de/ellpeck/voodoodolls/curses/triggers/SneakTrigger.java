package de.ellpeck.voodoodolls.curses.triggers;

import de.ellpeck.voodoodolls.VoodooDolls;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SneakTrigger extends CurseTrigger {

    public SneakTrigger() {
        super("sneak");
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (!entity.level.isClientSide && entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            CompoundNBT data = player.getPersistentData();
            boolean isSneaking = player.isCrouching();
            if (isSneaking != data.getBoolean(VoodooDolls.ID + ":was_sneaking")) {
                if (isSneaking)
                    this.trigger(player);
                data.putBoolean(VoodooDolls.ID + ":was_sneaking", isSneaking);
            }
        }
    }
}
