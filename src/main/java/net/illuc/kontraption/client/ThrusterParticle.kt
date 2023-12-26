package net.illuc.kontraption.client

import net.illuc.kontraption.particles.ThrusterParticleData
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.*
import javax.annotation.Nonnull
import kotlin.random.Random

class ThrusterParticle protected constructor(world: ClientLevel?, posX: Double, posY: Double, posZ: Double, velX: Double, velY: Double, velZ: Double, scale: Double, spriteSet: SpriteSet?) : TextureSheetParticle(world, posX, posY, posZ, velX, velY, velZ) {
    private var spriteset: SpriteSet? = null
    init {
        lifetime = 20
        friction = 0.85f
        setSpriteFromAge(spriteSet)
        spriteset = spriteSet
        //yo = 1.0
        xd = velX
        yd = velY
        zd = velZ
        scale((14f+scale).toFloat())
    }

    public override fun getLightColor(partialTick: Float): Int {
        return 190 + (20f * (1.0f - Minecraft.getInstance().options.gamma().get())).toInt()
    }

    override fun tick() {
        super.tick()
        fadeOut()
        this.setSpriteFromAge(spriteset)
        if (this.onGround){
            yd = 0.0
            //xd = Rand
        }
    }

    private fun fadeOut() {
        alpha = -(0.5f / lifetime.toFloat()) * age + 1
    }

    override fun getRenderType(): ParticleRenderType {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT
    }

    class Factory(private val spriteSet: SpriteSet?) : ParticleProvider<ThrusterParticleData> {
        override fun createParticle(thrusterParticleData: ThrusterParticleData, world: ClientLevel, x: Double, y: Double, z: Double, p_107426_: Double, p_107427_: Double, p_107428_: Double): Particle? {
            return ThrusterParticle(world, x, y, z, thrusterParticleData.posX, thrusterParticleData.posY, thrusterParticleData.posZ, thrusterParticleData.scale, spriteSet)
        }
    }
}