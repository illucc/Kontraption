package net.illuc.kontraption.mixin;

import mekanism.api.MekanismAPI;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(SoundHandler.class)
public class MixinSoundHandler {
/* i dont know
    @ModifyArg(method = "playSound(Lnet/minecraft/client/resources/sounds/SoundInstance;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundManager;play(Lnet/minecraft/client/resources/sounds/SoundInstance;)V"), index = 0)
    private static SoundInstance injected(SoundInstance soundInstance) {
        Level level = Minecraft.getInstance().level;
        final Ship ship = VSGameUtilsKt.getShipManagingPos(level, soundInstance.getX(),soundInstance.getY(),soundInstance.getZ());
        Mekanism.logger.info("X: " + soundInstance.getX());
        Mekanism.logger.info("Y: " + soundInstance.getY());
        Mekanism.logger.info("Z: " + soundInstance.getZ());
        if (ship == null){
            return soundInstance;
        }else{
            Vector3d pos = VSGameUtilsKt.toWorldCoordinates(ship, soundInstance.getX(),soundInstance.getY(),soundInstance.getZ());
            return new SimpleSoundInstance(soundInstance.getLocation(), soundInstance.getSource(), soundInstance.getPitch(), soundInstance.getVolume(), soundInstance.isLooping(), soundInstance.getDelay(), soundInstance.getAttenuation(), pos.x, pos.y, pos.z, soundInstance.isRelative());
        }
        //SimpleSoundInstance nSoundInstance = new SimpleSoundInstance(soundInstance.getLocation(), soundInstance.getSource(), soundInstance.getPitch(), soundInstance.getVolume(), soundInstance.isLooping(), soundInstance.getDelay(), soundInstance.getAttenuation(), soundInstance.getX(), soundInstance.getY(), soundInstance.getZ(), soundInstance.isRelative());
        //return nSoundInstance;
    }*/
}
