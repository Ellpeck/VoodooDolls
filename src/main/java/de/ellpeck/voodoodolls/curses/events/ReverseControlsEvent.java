package de.ellpeck.voodoodolls.curses.events;

import de.ellpeck.voodoodolls.VoodooDolls;
import de.ellpeck.voodoodolls.curses.Curse;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.MovementInput;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ReverseControlsEvent extends TimedEvent {

    // this can be here since it's client-only
    private double inputReversedStopTime;

    public ReverseControlsEvent() {
        super("reverse_controls", Badness.WORSE, 0.1F, true, 0.5, 2);
    }

    @SubscribeEvent
    public void onInputUpdate(InputUpdateEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level.getGameTime() >= this.inputReversedStopTime)
            return;
        MovementInput input = event.getMovementInput();

        boolean left = input.left;
        input.left = input.right;
        input.right = left;

        boolean up = input.up;
        input.up = input.down;
        input.down = up;

        input.forwardImpulse *= -1;
        input.leftImpulse *= -1;
    }

    @Override
    public void occur(PlayerEntity player, Curse curse) {
        if (player.level.isClientSide && player.level.getGameTime() >= this.inputReversedStopTime) {
            this.inputReversedStopTime = player.level.getGameTime() + this.getRandomMinutes(player) * 20 * 60;
            player.displayClientMessage(new TranslationTextComponent("info." + VoodooDolls.ID + ".reverse_controls_start").withStyle(TextFormatting.RED), false);
        }
    }
}
