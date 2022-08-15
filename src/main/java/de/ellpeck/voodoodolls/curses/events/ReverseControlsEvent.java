package de.ellpeck.voodoodolls.curses.events;

import de.ellpeck.voodoodolls.curses.Curse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ReverseControlsEvent extends TimedEvent {

    // this can be here since it's client-only
    private double inputReversedStopTime;

    public ReverseControlsEvent() {
        super("reverse_controls", Badness.WORSE, 0.1F, true, 0.5, 2);
    }

    @SubscribeEvent
    public void onInputUpdate(MovementInputUpdateEvent event) {
        var mc = Minecraft.getInstance();
        if (mc.level.getGameTime() >= this.inputReversedStopTime)
            return;
        var input = event.getInput();

        var left = input.left;
        input.left = input.right;
        input.right = left;

        var up = input.up;
        input.up = input.down;
        input.down = up;

        input.forwardImpulse *= -1;
        input.leftImpulse *= -1;
    }

    @Override
    public void occur(Player player, Curse curse) {
        if (player.level.isClientSide && player.level.getGameTime() >= this.inputReversedStopTime)
            this.inputReversedStopTime = player.level.getGameTime() + this.getRandomMinutes(player) * 20 * 60;
    }
}
