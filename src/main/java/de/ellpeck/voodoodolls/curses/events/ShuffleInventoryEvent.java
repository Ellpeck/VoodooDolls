package de.ellpeck.voodoodolls.curses.events;

import de.ellpeck.voodoodolls.curses.Curse;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShuffleInventoryEvent extends CurseEvent {

    public ShuffleInventoryEvent() {
        super("shuffle_inventory");
    }

    @Override
    public void occur(PlayerEntity player, Curse curse) {
        List<ItemStack> items = new ArrayList<>(player.inventory.items);
        Collections.shuffle(items);
        for (int i = 0; i < items.size(); i++)
            player.inventory.setItem(i, items.get(i));
    }
}