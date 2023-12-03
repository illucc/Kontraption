package net.illuc.kontraption

import mekanism.common.registration.impl.ParticleTypeDeferredRegister
import net.illuc.kontraption.client.ThrusterParticle
import net.illuc.kontraption.particle.BulletParticleType
import net.illuc.kontraption.particle.ThrusterParticleType
import net.illuc.kontraption.particles.BulletParticleData
import net.illuc.kontraption.particles.ThrusterParticleData
import net.minecraft.core.particles.ParticleType
import net.minecraft.core.particles.SimpleParticleType
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries


object KontraptionParticleTypes {
    val PARTICLE_TYPES = ParticleTypeDeferredRegister(Kontraption.MODID)

    //val PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Kontraption.MODID)

    val THRUSTER = PARTICLE_TYPES.register("thruster") { ThrusterParticleType() } //was SimpleParticleType(true)
    val BULLET = PARTICLE_TYPES.register("bullet") { BulletParticleType() }
    /*val LASER = PARTICLE_TYPES.register("laser") { LaserParticleType() }
    val JETPACK_FLAME = PARTICLE_TYPES.registerBasicParticle("jetpack_flame")
    val JETPACK_SMOKE = PARTICLE_TYPES.registerBasicParticle("jetpack_smoke")
    val SCUBA_BUBBLE = PARTICLE_TYPES.registerBasicParticle("scuba_bubble")
    val RADIATION = PARTICLE_TYPES.registerBasicParticle("radiation")*/

    fun register(eventBus: IEventBus?) {
        PARTICLE_TYPES.register(eventBus)
    }
}