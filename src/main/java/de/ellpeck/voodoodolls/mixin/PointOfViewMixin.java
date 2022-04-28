package de.ellpeck.voodoodolls.mixin;

import de.ellpeck.voodoodolls.curses.events.TopDownCameraEvent;
import net.minecraft.client.settings.PointOfView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PointOfView.class)
public class PointOfViewMixin {

    @Inject(method = "isFirstPerson", at = @At("RETURN"), cancellable = true)
    private void isFirstPerson(CallbackInfoReturnable<Boolean> callback) {
        callback.setReturnValue(callback.getReturnValue() && !TopDownCameraEvent.isActive());
    }

    @Inject(method = "isMirrored", at = @At("RETURN"), cancellable = true)
    private void isFrontView(CallbackInfoReturnable<Boolean> callback) {
        callback.setReturnValue(callback.getReturnValue() && !TopDownCameraEvent.isActive());
    }
}