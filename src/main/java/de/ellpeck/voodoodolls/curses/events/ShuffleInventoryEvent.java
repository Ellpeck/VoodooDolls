package de.ellpeck.voodoodolls.curses.events;

import de.ellpeck.voodoodolls.curses.Curse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShuffleInventoryEvent extends CurseEvent {

    public ShuffleInventoryEvent() {
        super("shuffle_inventory", Badness.BAD, 0.1F, false);
    }

    @Override
    public void occur(Player player, Curse curse) {
        if (player.level.isClientSide)
            return;
        List<ItemStack> items = new ArrayList<>();
        for (var i = 0; i < player.getInventory().getContainerSize(); i++)
            items.add(player.getInventory().getItem(i));
        Collections.shuffle(items);
        for (var i = 0; i < items.size(); i++)
            player.getInventory().setItem(i, items.get(i));
    }
}
