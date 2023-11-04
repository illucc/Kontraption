package net.illuc.kontraption.multiblocks.largeHydrogenThruster

import mekanism.common.lib.multiblock.MultiblockData
import mekanism.common.particle.LaserParticleData
import net.illuc.kontraption.KontraptionParticleTypes
import net.illuc.kontraption.particles.ThrusterParticleData
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.phys.Vec3
import net.minecraftforge.api.distmarker.Dist
import thedarkcolour.kotlinforforge.forge.vectorutil.toVec3
import kotlin.properties.Delegates


class HydrogenThrusterMultiblockData(tile: BlockEntity?) : MultiblockData(tile) {

    lateinit var exhaustDirection: Direction
    lateinit var centerExhaust: BlockEntity
    var exhaustDiameter = 0
    lateinit var offset: Vec3
    override fun tick(world: Level?): Boolean {
        val particleDir = exhaustDirection.normal.multiply(3+exhaustDiameter)
        val pos = centerExhaust.blockPos.offset(exhaustDirection.normal.multiply(2))
        val level = centerExhaust.level

        if (Dist.DEDICATED_SERVER.isDedicatedServer and (level != null)) {
            level as ServerLevel
            offset = if (exhaustDirection == Direction.NORTH || exhaustDirection == Direction.SOUTH) Vec3(0.25*exhaustDiameter, 0.25*exhaustDiameter, 1.5) else if (exhaustDirection == Direction.EAST || exhaustDirection == Direction.WEST) Vec3(1.5, 0.25*exhaustDiameter, 0.25*exhaustDiameter) else Vec3(0.25*exhaustDiameter, 1.5, 0.25*exhaustDiameter)
            sendParticleData(level, pos.toVec3(), particleDir)
        }
        return super.tick(world)
    }

    private fun sendParticleData(level: Level, pos: Vec3, particleDir: Vec3i) {
        if (!isRemote && level is ServerLevel) {

            for (player in level.players()) {
                level.sendParticles(player, ThrusterParticleData(particleDir.x.toDouble(), particleDir.y.toDouble(), particleDir.z.toDouble(), exhaustDiameter.toDouble()), true, pos.x+0.5, pos.y+0.5, pos.z+0.5, 10*exhaustDiameter, offset.x, offset.y, offset.z, 0.0)
            }
        }
    }


}