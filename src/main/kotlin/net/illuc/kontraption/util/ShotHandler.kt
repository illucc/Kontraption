package net.illuc.kontraption.util

import net.illuc.kontraption.util.KontraptionVSUtils.getShipObjectManagingPos
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.Ship


class ShotHandler {




    companion object {
        public fun shoot(direction: Vector3d, level: Level, position: Vector3d, distance: Double, func: (hitRes: BlockHitResult) -> Unit) {
            val lookingTowards = direction

            val startPos = position
                    .add(0.5, 0.5, 0.5)
                    .add(direction.mul(0.5))


            val ship = getShipObjectManagingPos(level, position)


            val clipResult = shootRaycast(level, startPos, ship, lookingTowards, distance)

            /*if (level is ServerLevel) {
                for (player in level.players()) {
                    level.sendParticles(player, BulletParticleData(lookingTowards.normal.toJOMLD().x,lookingTowards.normal.toJOMLD().y ,lookingTowards.normal.toJOMLD().z, 1.0), true, blockPos.x.toDouble()+0.5,blockPos.y.toDouble()+0.5,blockPos.z.toDouble()+0.5, 1, 0.5, 0.5, 0.5, 0.0)
                    //level.sendParticles(player, ParticleTypes.TOTEM_OF_UNDYING, true, blockPos.x.toDouble(), blockPos.x.toDouble(), blockPos.x.toDouble())
                }
            }*/

            if (clipResult.type == HitResult.Type.BLOCK) {
                func(clipResult)
                println("bals")
                //level.addParticle(ParticleTypes.TOTEM_OF_UNDYING, clipResult.blockPos.x.toDouble(),clipResult.blockPos.y.toDouble(),clipResult.blockPos.z.toDouble(), 1.0, 1.0, 1.0)
                /*if (level is ServerLevel) {
                    for (player in level.players()) {
                        level.sendParticles(player, ParticleTypes.ASH, true, clipResult.blockPos.x.toDouble()+0.5,clipResult.blockPos.y.toDouble()+0.5,clipResult.blockPos.z.toDouble()+0.5, 1, 0.001, 0.001, 0.001, 0.0)
                    //level.sendParticles(player, ParticleTypes.TOTEM_OF_UNDYING, true, blockPos.x.toDouble(), blockPos.x.toDouble(), blockPos.x.toDouble())
                    }
                }*/

            }


        }

        private fun shootRaycast(level: Level, startPos: Vector3d, ship: Ship?, direction: Vector3d, distance: Double): BlockHitResult {
            return level.clip(
                    ClipContext(
                            (Vector3d(startPos).let { ship?.shipToWorld?.transformPosition(it) ?: it }).toMinecraft(),
                            (startPos
                                    .add(0.5, 0.5, 0.5)
                                    .add(Vector3d(direction).mul(distance)) //distance
                                    .let {
                                        ship?.shipToWorld?.transformPosition(it) ?: it
                                    }).toMinecraft(),
                            ClipContext.Block.COLLIDER,
                            ClipContext.Fluid.NONE,
                            null
                    )
            )
        }
    }
}