package de.ellpeck.voodoodolls.curses.triggers;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.function.Function;

public class BreakBlockTrigger extends CurseTrigger {

    private final Function<BlockState, Boolean> isBlockAllowed;

    public BreakBlockTrigger(String id, double defaultChance, Function<BlockState, Boolean> isBlockAllowed) {
        super(id, defaultChance);
        this.isBlockAllowed = isBlockAllowed;
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player != null && !player.level.isClientSide && this.isBlockAllowed.apply(event.getState()))
            this.trigger(player);
    }
}
