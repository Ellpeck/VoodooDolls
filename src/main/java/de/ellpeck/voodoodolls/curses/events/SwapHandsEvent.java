package de.ellpeck.voodoodolls.curses.events;

import de.ellpeck.voodoodolls.curses.Curse;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class SwapHandsEvent extends CurseEvent {

    public SwapHandsEvent() {
        super("swap_hands", Badness.BAD, 0.25F, false);
    }

    @Override
    public void occur(Player player, Curse curse) {
        if (player.level.isClientSide)
            return;
        var main = player.getItemInHand(InteractionHand.MAIN_HAND);
        var off = player.getItemInHand(InteractionHand.OFF_HAND);
        player.setItemInHand(InteractionHand.MAIN_HAND, off);
        player.setItemInHand(InteractionHand.OFF_HAND, main);
    }
}
