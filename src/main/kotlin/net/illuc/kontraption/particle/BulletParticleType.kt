package net.illuc.kontraption.particle

import com.mojang.serialization.Codec
import net.illuc.kontraption.particles.BulletParticleData
import net.illuc.kontraption.particles.ThrusterParticleData
import net.minecraft.core.particles.ParticleType
import javax.annotation.Nonnull

class BulletParticleType : ParticleType<BulletParticleData>(false, BulletParticleData.DESERIALIZER) {
    @Nonnull
    override fun codec(): Codec<BulletParticleData> {
        return BulletParticleData.CODEC
    }
}