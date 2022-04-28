package de.ellpeck.voodoodolls.curses.events;

import de.ellpeck.voodoodolls.curses.Curse;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;

public class TopDownCameraEvent extends TimedEvent {

    // this can be here since it's client-only
    private static double stopTime;

    public TopDownCameraEvent() {
        super("top_down_camera", Badness.WORSE, 0.05F, false, 1, 5);
    }

    @Override
    public void occur(PlayerEntity player, Curse curse) {
        if (player.level.isClientSide && player.level.getGameTime() >= stopTime)
            stopTime = player.level.getGameTime() + this.getRandomMinutes(player) * 20 * 60;
    }

    public static boolean isActive() {
        return Minecraft.getInstance().level.getGameTime() < stopTime;
    }
}
