package de.ellpeck.voodoodolls.curses.triggers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.function.Function;

public class BreakBlockTrigger extends CurseTrigger {

    private final Function<BlockState, Boolean> isBlockAllowed;

    public BreakBlockTrigger(String id, Function<BlockState, Boolean> isBlockAllowed) {
        super(id);
        this.isBlockAllowed = isBlockAllowed;
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        var player = event.getPlayer();
        if (player != null && !player.level.isClientSide && this.isBlockAllowed.apply(event.getState()))
            this.trigger(player);
    }
}
