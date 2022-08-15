package de.ellpeck.voodoodolls.mixin;

import de.ellpeck.voodoodolls.curses.events.TopDownCameraEvent;
import net.minecraft.client.CameraType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CameraType.class)
public class CameraTypeMixin {

    @Inject(method = "isFirstPerson()Z", at = @At("RETURN"), cancellable = true)
    private void isFirstPerson(CallbackInfoReturnable<Boolean> callback) {
        callback.setReturnValue(callback.getReturnValue() && !TopDownCameraEvent.isActive());
    }

    @Inject(method = "isMirrored()Z", at = @At("RETURN"), cancellable = true)
    private void isFrontView(CallbackInfoReturnable<Boolean> callback) {
        callback.setReturnValue(callback.getReturnValue() && !TopDownCameraEvent.isActive());
    }
}