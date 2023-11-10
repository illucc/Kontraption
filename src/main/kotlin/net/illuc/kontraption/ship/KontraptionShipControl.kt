package net.illuc.kontraption.ship

import net.illuc.kontraption.ThrusterInterface
import net.illuc.kontraption.blockEntities.TileEntityGyro
import net.illuc.kontraption.blockEntities.TileEntityIonThruster
import net.illuc.kontraption.multiblocks.largeHydrogenThruster.HydrogenThrusterMultiblockData
import net.illuc.kontraption.util.toBlockPos
import net.illuc.kontraption.util.toDouble
import net.illuc.kontraption.util.toJOML
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import org.joml.Quaterniond
import org.joml.Vector3d
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.*
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import java.util.concurrent.CopyOnWriteArrayList

class KontraptionShipControl  : ShipForcesInducer {

    data class Controllable<Vector3i, Vector3d, Double, BlockEntity>(val position: Vector3i, val forceDirection: Vector3d, val forceStrength: Double, val be: BlockEntity)
    data class Thruster<Vector3i, Vector3d, Double, BlockEntity>(val position: Vector3i, val forceDirection: Vector3d, val forceStrength: Double, val thruster: ThrusterInterface)


    //actual ship control stuffs

    //Doing the list thingies
    private val thrusters =   CopyOnWriteArrayList<Thruster<Vector3i, Vector3d, Double, ThrusterInterface>>()
    private val gyros =       CopyOnWriteArrayList<Controllable<Vector3i, Vector3d, Double, TileEntityGyro>>()
    private val attachments = CopyOnWriteArrayList<Controllable<Vector3i, Vector3d, Double, BlockEntity>>()

    //Values
    val gyroStrength     = 100000.0
    val thrusterStrength = 100000.0

    override fun applyForces(physShip: PhysShip) {
        physShip as PhysShipImpl
        gyros.forEach {
            val (position, forceDirection, forceStrength, be) = it
            if (forceStrength != 0.0){
                val torqueGlobal = physShip.transform.shipToWorldRotation.transform(forceDirection, Vector3d())
                physShip.applyInvariantTorque(torqueGlobal.mul(forceStrength*gyroStrength))
            }else{
                //val torqueGlobal = physShip.transform.shipToWorldRotation.transform(physShip.poseVel.omega.negate(Vector3d()), Vector3d())
                //physShip.applyInvariantTorque(torqueGlobal.mul(gyroStrength))
            }

        }
        thrusters.forEach {
            val (position, forceDirection, forceStrength, be) = it
            //be.enable()
            if (forceStrength != 0.0){
                be.powered = true
                val tForce = physShip.transform.shipToWorld.transformDirection(forceDirection, Vector3d())
                //val tPos2 = position.toDouble().add(0.5, 0.5, 0.5).sub(physShip.transform.positionInShip)
                val tPos = Vector3d(0.0, 0.0, 0.0) //position.toDouble().add(0.5, 0.5, 0.5).sub(physShip.transform.positionInShip)

                if (forceDirection.isFinite) {
                    val forceFinal = forceStrength*thrusterStrength
                    physShip.applyInvariantForceToPos(tForce.mul(if (forceFinal > 0) forceFinal else 0.0), tPos)
                }
            }else{
                be.powered = false
            }
        }
    }



    //<----------------------------------(THRUSTER STUFF)-------------------------------------->
    /*fun addThruster(pos: BlockPos, force: Vector3d, tier: Double, be: TileEntityThruster) {
        thrusters.add(Controllable(pos.toJOML(), force, tier, be))
    }
    fun removeThruster(pos: BlockPos, force: Vector3d, tier: Double, be: TileEntityThruster) {
        thrusters.remove(Controllable(pos.toJOML(), force, tier, be))
    }
    fun stopThruster(pos: BlockPos) {
        thrusters.removeAll { it.position == pos.toJOML() }
    }*/

    fun addThruster(pos: BlockPos, force: Vector3d, tier: Double, thruster: ThrusterInterface) {
        thrusters.add(Thruster(pos.toJOML(), force, tier, thruster))
    }
    fun removeThruster(pos: BlockPos, force: Vector3d, tier: Double, thruster: ThrusterInterface) {
        thrusters.remove(Thruster(pos.toJOML(), force, tier, thruster))
    }
    fun stopThruster(pos: BlockPos) {
        thrusters.removeAll { it.position == pos.toJOML() }
    }
    fun thrusterControlAll(forceDirection: Vector3d, power: Double) {
        thrusters.forEach {
            if (it.forceDirection == forceDirection){
                val (pos, forceDir, tier, be) = it
                removeThruster(pos.toBlockPos(), forceDir, tier, be)
                addThruster(pos.toBlockPos(), forceDir, power, be)
            }
        }
    }



    //<----------------------------------(GYRO STUFF)------------------------------------------>
    fun addGyro(pos: BlockPos, force: Vector3d, tier: Double, be: TileEntityGyro) {
        gyros.add(Controllable(pos.toJOML(), force, tier, be))
    }
    fun removeGyro(pos: BlockPos, force: Vector3d, tier: Double, be: TileEntityGyro) {
        gyros.remove(Controllable(pos.toJOML(), force, tier, be))
    }
    fun stopGyro(pos: BlockPos) {
        gyros.removeAll { it.position == pos.toJOML() }
    }
    fun gyroControlAll(forceDirection: Vector3d, power: Double) {
        gyros.forEach {
            val (pos, direction, tier, be) = it
            stopGyro(pos.toBlockPos())
            addGyro(pos.toBlockPos(), forceDirection, power, be)
        }
    }



    companion object {
        fun getOrCreate(ship: ServerShip): KontraptionShipControl {
            return ship.getAttachment<KontraptionShipControl>()
                    ?: KontraptionShipControl().also { ship.saveAttachment(it) }
        }
    }
}