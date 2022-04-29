package de.ellpeck.voodoodolls.curses.events;

import de.ellpeck.voodoodolls.curses.Curse;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShuffleInventoryEvent extends CurseEvent {

    public ShuffleInventoryEvent() {
        super("shuffle_inventory", Badness.BAD, 0.1F, false);
    }

    @Override
    public void occur(PlayerEntity player, Curse curse) {
        if (player.level.isClientSide)
            return;
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < player.inventory.getContainerSize(); i++)
            items.add(player.inventory.getItem(i));
        Collections.shuffle(items);
        for (int i = 0; i < items.size(); i++)
            player.inventory.setItem(i, items.get(i));
    }
}
