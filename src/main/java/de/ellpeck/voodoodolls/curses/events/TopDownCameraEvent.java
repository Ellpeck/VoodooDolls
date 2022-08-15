package de.ellpeck.voodoodolls.curses.events;

import de.ellpeck.voodoodolls.curses.Curse;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class TopDownCameraEvent extends TimedEvent {

    // this can be here since it's client-only
    private static double stopTime;

    public TopDownCameraEvent() {
        super("top_down_camera", Badness.WORSE, 0.05F, false, 1, 5);
    }

    @Override
    public void occur(Player player, Curse curse) {
        if (player.level.isClientSide && player.level.getGameTime() >= TopDownCameraEvent.stopTime)
            TopDownCameraEvent.stopTime = player.level.getGameTime() + this.getRandomMinutes(player) * 20 * 60;
    }

    public static boolean isActive() {
        return Minecraft.getInstance().level.getGameTime() < TopDownCameraEvent.stopTime;
    }
}
