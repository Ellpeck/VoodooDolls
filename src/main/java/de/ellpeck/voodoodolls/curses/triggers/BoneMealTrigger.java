package de.ellpeck.voodoodolls.curses.triggers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BoneMealTrigger extends CurseTrigger {

    public BoneMealTrigger() {
        super("bone_meal", 0.1);
    }

    @SubscribeEvent
    public void onBonemeal(BonemealEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player != null && !player.level.isClientSide) {
            BlockState state = event.getBlock();
            Block block = state.getBlock();
            if (block instanceof IGrowable && ((IGrowable) block).isValidBonemealTarget(player.level, event.getPos(), state, player.level.isClientSide))
                this.trigger(player);
        }
    }
}
