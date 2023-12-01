package net.illuc.kontraption.util

import net.illuc.kontraption.particles.ThrusterParticleData
import net.illuc.kontraption.util.KontraptionVSUtils.getShipObjectManagingPos
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.SmokeParticle
import net.minecraft.client.particle.TotemParticle
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.HitResult
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.Ship


class ShotHandler {


    companion object {
        public fun shoot(direction: Direction, level: Level, blockPos: BlockPos) {
            val lookingTowards = direction

            val startPos = blockPos.toJOMLD()
                    .add(0.5, 0.5, 0.5)
                    .add(direction.normal.toJOMLD().mul(0.5))


            val ship = getShipObjectManagingPos(level, blockPos)


            val clipResult = level.clip(
                    ClipContext(
                            (Vector3d(startPos).let { ship?.shipToWorld?.transformPosition(it) ?: it }).toMinecraft(),
                            (blockPos.toJOMLD()
                                    .add(0.5, 0.5, 0.5)
                                    .add(Vector3d(lookingTowards.normal.toJOMLD()).mul(50.0))
                                    .let {
                                        ship?.shipToWorld?.transformPosition(it) ?: it
                                    }).toMinecraft(),
                            ClipContext.Block.COLLIDER,
                            ClipContext.Fluid.NONE,
                            null
                    )
            )

            if (clipResult.type == HitResult.Type.BLOCK) {

                println("bals")
                //level.addParticle(ParticleTypes.TOTEM_OF_UNDYING, clipResult.blockPos.x.toDouble(),clipResult.blockPos.y.toDouble(),clipResult.blockPos.z.toDouble(), 1.0, 1.0, 1.0)
                if (level is ServerLevel) {
                    for (player in level.players()) {
                        level.sendParticles(player, ParticleTypes.ELECTRIC_SPARK, true, clipResult.blockPos.x.toDouble()+0.5,clipResult.blockPos.y.toDouble()+0.5,clipResult.blockPos.z.toDouble()+0.5, 5, 0.5, 0.5, 0.5, 0.0)
                    //level.sendParticles(player, ParticleTypes.TOTEM_OF_UNDYING, true, blockPos.x.toDouble(), blockPos.x.toDouble(), blockPos.x.toDouble())
                    }
                }

            }


        }
    }
}