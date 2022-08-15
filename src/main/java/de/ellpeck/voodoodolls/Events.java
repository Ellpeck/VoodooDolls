package de.ellpeck.voodoodolls;

import de.ellpeck.voodoodolls.curses.CurseData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Events {

    @SubscribeEvent
    public static void onJoinWorld(EntityJoinWorldEvent event) {
        var entity = event.getEntity();
        if (entity instanceof Player player) {
            if (!player.level.isClientSide) {
                var data = CurseData.get(player.level);
                Packets.sendTo(player, data.getPacket());
            }
        }
    }

}
