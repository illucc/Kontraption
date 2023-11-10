package net.illuc.kontraption.multiblocks.largeHydrogenThruster

import mekanism.api.AutomationType
import mekanism.common.capabilities.energy.BasicEnergyContainer
import mekanism.common.capabilities.energy.VariableCapacityEnergyContainer
import mekanism.common.capabilities.fluid.BasicFluidTank
import mekanism.common.capabilities.fluid.VariableCapacityFluidTank
import mekanism.common.lib.multiblock.MultiblockData
import mekanism.common.tags.MekanismTags
import net.illuc.kontraption.ThrusterInterface
import net.illuc.kontraption.particles.ThrusterParticleData
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.phys.Vec3
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fluids.FluidStack
import thedarkcolour.kotlinforforge.forge.vectorutil.toVec3
import java.util.function.Predicate


class HydrogenThrusterMultiblockData(tile: BlockEntity?) : MultiblockData(tile), ThrusterInterface {

    // GRRRRRRRRRRRRRRRRRRRR I HATE LATEINIT BALS
    val te = tile
    var exhaustDirection: Direction = Direction.NORTH
    var centerExhaust: BlockEntity? = tile
    var exhaustDiameter = 0
    var offset: Vec3 = Vec3(0.0, 0.0, 0.0)
    var center: BlockPos = BlockPos(0, 0, 0)

    var particleDir = exhaustDirection.normal.multiply(3+exhaustDiameter)
    var pos = centerExhaust?.blockPos?.offset(exhaustDirection.normal.multiply(2))


    //----------------THRUSTER CONTROL-----------------------
    override var enabled = true
    override var thrusterLevel: Level? = centerExhaust?.level
    override val worldPosition: BlockPos? = center
    override val forceDirection: Direction = exhaustDirection
    override var powered: Boolean = true
    override val thrusterPower: Double = 1.0


    //----------------stuff-----------------------

    override fun onCreated(world: Level?) {


        super.onCreated(world)
    }


    override fun tick(world: Level?): Boolean {
        if (Dist.DEDICATED_SERVER.isDedicatedServer and (thrusterLevel != null)) {
            thrusterLevel as ServerLevel
            offset = if (exhaustDirection == Direction.NORTH || exhaustDirection == Direction.SOUTH) Vec3(0.25*exhaustDiameter, 0.25*exhaustDiameter, 1.5) else if (exhaustDirection == Direction.EAST || exhaustDirection == Direction.WEST) Vec3(1.5, 0.25*exhaustDiameter, 0.25*exhaustDiameter) else Vec3(0.25*exhaustDiameter, 1.5, 0.25*exhaustDiameter)
            pos?.let { sendParticleData(thrusterLevel as ServerLevel, it.toVec3(), particleDir) }
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