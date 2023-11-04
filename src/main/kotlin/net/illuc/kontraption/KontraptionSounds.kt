package net.illuc.kontraption

import mekanism.common.registration.impl.SoundEventDeferredRegister
import mekanism.common.registration.impl.SoundEventRegistryObject
import net.minecraft.sounds.SoundEvent


object KontraptionSounds {
    val SOUND_EVENTS = SoundEventDeferredRegister(Kontraption.MODID)
    val ION_THRUSTER: SoundEventRegistryObject<SoundEvent> = SOUND_EVENTS.register("tile.thruster.ion_thruster")
}