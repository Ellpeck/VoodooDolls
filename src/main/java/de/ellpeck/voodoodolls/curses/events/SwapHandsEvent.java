package de.ellpeck.voodoodolls.curses.events;

import de.ellpeck.voodoodolls.curses.Curse;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class SwapHandsEvent extends CurseEvent {

    public SwapHandsEvent() {
        super("swap_hands", Badness.BAD, 0.25F);
    }

    @Override
    public void occur(PlayerEntity player, Curse curse) {
        ItemStack main = player.getItemInHand(Hand.MAIN_HAND);
        ItemStack off = player.getItemInHand(Hand.OFF_HAND);
        player.setItemInHand(Hand.MAIN_HAND, off);
        player.setItemInHand(Hand.OFF_HAND, main);
    }
}
