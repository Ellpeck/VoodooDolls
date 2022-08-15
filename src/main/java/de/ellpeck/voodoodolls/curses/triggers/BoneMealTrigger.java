package de.ellpeck.voodoodolls.curses.triggers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BoneMealTrigger extends CurseTrigger {

    public BoneMealTrigger() {
        super("bone_meal");
    }

    @SubscribeEvent
    public void onBonemeal(BonemealEvent event) {
        var player = event.getPlayer();
        if (player != null && !player.level.isClientSide) {
            var state = event.getBlock();
            var block = state.getBlock();
            if (block instanceof BonemealableBlock bonemealable && bonemealable.isValidBonemealTarget(player.level, event.getPos(), state, player.level.isClientSide))
                this.trigger(player);
        }
    }
}
