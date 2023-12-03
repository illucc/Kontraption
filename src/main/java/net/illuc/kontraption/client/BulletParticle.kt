package net.illuc.kontraption.client

import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Quaternion
import com.mojang.math.Vector3f
import net.illuc.kontraption.particles.BulletParticleData
import net.minecraft.client.Camera
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.*
import net.minecraft.util.Mth
import org.joml.Vector3d
import team.lodestar.lodestone.systems.client.ClientTickCounter


class BulletParticle protected constructor(world: ClientLevel?, posX: Double, posY: Double, posZ: Double, velX: Double, velY: Double, velZ: Double, scale: Double, spriteSet: SpriteSet?) : TextureSheetParticle(world, posX, posY, posZ, velX, velY, velZ) {
    private var spriteset: SpriteSet? = null
    private var vel: Vector3d = Vector3d(velX, velY, velZ)
    init {
        spriteset = spriteSet
        lifetime = 200
        friction = 1f
        setSpriteFromAge(spriteSet)

        xd = velX*10
        yd = velY*10
        zd = velZ*10
        scale(3f)
    }

    public override fun getLightColor(partialTick: Float): Int {
        return 190 + (20f * (1.0f - Minecraft.getInstance().options.gamma)).toInt()
    }

    override fun tick() {
        super.tick()
        fadeOut()
        //this.setSpriteFromAge(spriteset)
    }

    //bals
    override fun render(builder: VertexConsumer, camera: Camera, partialTicks: Float) {
        val cameraPos = camera.position
        val originX = (Mth.lerp(partialTicks.toDouble(), xo, x) - cameraPos.x()).toFloat()
        val originY = (Mth.lerp(partialTicks.toDouble(), yo, y) - cameraPos.y()).toFloat()
        val originZ = (Mth.lerp(partialTicks.toDouble(), zo, z) - cameraPos.z()).toFloat()
        val vertices: Array<Vector3f> = arrayOf<Vector3f>(
                Vector3f(-1.0f, -1.0f, 0.0f),
                Vector3f(-1.0f, 1.0f, 0.0f),
                Vector3f(1.0f, 1.0f, 0.0f),
                Vector3f(1.0f, -1.0f, 0.0f)
        )
        val scale = getQuadSize(partialTicks)
        val rotation: Quaternion = Quaternion(camera.rotation())
        if (roll != 0.0f) {
            val angle = Mth.lerp(ClientTickCounter.partialTicks, oRoll, roll)
            rotation.mul(Vector3f.ZP.rotation(angle))
        }

        //val rotation = Vector3f.XP.rotationDegrees(90f);//Quaternion((Math.ceil(vel.x.absoluteValue)*camera.xRot).toFloat(), (Math.ceil(vel.y.absoluteValue)*-camera.yRot).toFloat(), 0f, true)

        if (roll != 0.0f) {
            val angle = Mth.lerp(ClientTickCounter.partialTicks, oRoll, roll)
            rotation.mul(Vector3f.ZP.rotation(angle))
        }

        for (i in 0..3) {
            val vertex: Vector3f = vertices[i]
            vertex.transform(rotation)
            vertex.mul(scale)
            vertex.add(originX, originY, originZ)
        }
        val minU = u0
        val maxU = u1
        val minV = v0
        val maxV = v1
        builder.vertex(vertices[0].x().toDouble(), vertices[0].y().toDouble(), vertices[0].z().toDouble()).uv(maxU, maxV).color(rCol, gCol, bCol, alpha).uv2(15).endVertex()
        builder.vertex(vertices[1].x().toDouble(), vertices[1].y().toDouble(), vertices[1].z().toDouble()).uv(maxU, minV).color(rCol, gCol, bCol, alpha).uv2(15).endVertex()
        builder.vertex(vertices[2].x().toDouble(), vertices[2].y().toDouble(), vertices[2].z().toDouble()).uv(minU, minV).color(rCol, gCol, bCol, alpha).uv2(15).endVertex()
        builder.vertex(vertices[3].x().toDouble(), vertices[3].y().toDouble(), vertices[3].z().toDouble()).uv(minU, maxV).color(rCol, gCol, bCol, alpha).uv2(15).endVertex()
    }

    private fun fadeOut() {
        //alpha = -(0.5f / lifetime.toFloat()) * age + 1
    }

    override fun getRenderType(): ParticleRenderType {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT
    }
    class Factory(private val spriteSet: SpriteSet?) : ParticleProvider<BulletParticleData> {
        override fun createParticle(bulletParticleData: BulletParticleData, world: ClientLevel, x: Double, y: Double, z: Double, p_107426_: Double, p_107427_: Double, p_107428_: Double): Particle? {
            return BulletParticle(world, x, y, z, bulletParticleData.posX, bulletParticleData.posY, bulletParticleData.posZ, bulletParticleData.scale, spriteSet)
        }
    }
}