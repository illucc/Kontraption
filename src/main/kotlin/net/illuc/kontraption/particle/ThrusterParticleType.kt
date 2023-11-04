package net.illuc.kontraption.particle

import com.mojang.serialization.Codec
import mekanism.common.particle.LaserParticleData
import net.illuc.kontraption.particles.ThrusterParticleData
import net.minecraft.core.particles.ParticleType
import javax.annotation.Nonnull


class ThrusterParticleType : ParticleType<ThrusterParticleData>(false, ThrusterParticleData.DESERIALIZER) {
    @Nonnull
    override fun codec(): Codec<ThrusterParticleData> {
        return ThrusterParticleData.CODEC
    }
}