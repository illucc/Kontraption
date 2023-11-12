package net.illuc.kontraption.multiblocks.largeHydrogenThruster

import mekanism.common.lib.multiblock.MultiblockData
import net.illuc.kontraption.ThrusterInterface
import net.illuc.kontraption.particles.ThrusterParticleData
import net.illuc.kontraption.util.KontraptionVSUtils
import net.illuc.kontraption.util.toDoubles
import net.illuc.kontraption.util.toJOMLD
import net.illuc.kontraption.util.toMinecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.phys.Vec3
import net.minecraftforge.api.distmarker.Dist
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.Ship


class LiquidFuelThrusterMultiblockData(tile: BlockEntity?) : MultiblockData(tile), ThrusterInterface {

    // :cri:
    val te = tile
    var exhaustDirection: Direction = Direction.NORTH
    var centerExhaust: BlockEntity? = tile
    var exhaustDiameter = 0
    var offset: Vec3 = Vec3(0.0, 0.0, 0.0)
    var center: BlockPos = BlockPos(0, 0, 0)
    var innerVolume = 1

    var particleDir = exhaustDirection.normal.multiply(3+exhaustDiameter).toJOMLD()
    var pos = centerExhaust?.blockPos?.offset(exhaustDirection.normal.multiply(2))

    lateinit var ship: Ship


    //----------------THRUSTER CONTROL-----------------------
    override var enabled = true
    override var thrusterLevel: Level? = centerExhaust?.level
    override var worldPosition: BlockPos? = center
    override var forceDirection: Direction = exhaustDirection.opposite
    override var powered: Boolean = true
    override var thrusterPower: Double = 24.0
    override val basePower: Double = 24.0


    //----------------stuff-----------------------

    override fun onCreated(world: Level?) {


        super.onCreated(world)
        ship = KontraptionVSUtils.getShipObjectManagingPos((thrusterLevel as ServerLevel), center)
                ?: KontraptionVSUtils.getShipManagingPos((thrusterLevel as ServerLevel), center)

        thrusterLevel = centerExhaust?.level
        worldPosition = center
        forceDirection = exhaustDirection.opposite
        pos = centerExhaust?.blockPos?.offset(exhaustDirection.normal.multiply(1))
        thrusterPower = (24*innerVolume).toDouble()
        offset = Vector3d(1.0, 1.0, 1.0)
                .add(exhaustDirection.normal.toJOMLD().normalize().negate())
                .mul(0.25*exhaustDiameter)
                .add(exhaustDirection.normal.toJOMLD()
                        .mul(1.5)).toMinecraft()
        enable()
    }


    override fun tick(world: Level?): Boolean {
        if (powered){
            if (Dist.DEDICATED_SERVER.isDedicatedServer and (thrusterLevel != null)) {
                if (ship == null){
                    particleDir = exhaustDirection.normal.multiply(3+exhaustDiameter).toJOMLD()
                }else{
                    particleDir = ship.transform.shipToWorld.transformDirection(exhaustDirection.normal.toJOMLD())
                }

                thrusterLevel as ServerLevel
                pos?.let { sendParticleData(thrusterLevel as ServerLevel, it.toDoubles(), particleDir) }
            }
        }
        return super.tick(world)
    }


    private fun sendParticleData(level: Level, pos: Vec3, particleDir: Vector3d) {
        if (!isRemote && level is ServerLevel) {

            for (player in level.players()) {
                level.sendParticles(player, ThrusterParticleData(particleDir.x.toDouble(), particleDir.y.toDouble(), particleDir.z.toDouble(), exhaustDiameter.toDouble()), true, pos.x+0.5, pos.y+0.5, pos.z+0.5, 10*exhaustDiameter, offset.x, offset.y, offset.z, 0.0)
            }
        }
    }


}