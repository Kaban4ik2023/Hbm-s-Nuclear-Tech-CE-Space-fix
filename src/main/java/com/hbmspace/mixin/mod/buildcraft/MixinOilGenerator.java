package com.hbmspace.mixin.mod.buildcraft;

import com.hbmspace.dim.orbit.WorldProviderOrbit;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "buildcraft.energy.generation.OilGenerator", remap = false)
public class MixinOilGenerator {
    @Inject(method = "onPopulatePre", at = @At("HEAD"), cancellable = true)
    private static void keepOrbitEmpty(PopulateChunkEvent.Pre event, CallbackInfo ci) {
        if (event.getWorld().provider instanceof WorldProviderOrbit) ci.cancel();
    }
}
