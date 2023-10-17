package net.illuc.kontraption.ship


import net.illuc.kontraption.util.toBlockPos
import net.illuc.kontraption.util.toDouble
import net.illuc.kontraption.util.toJOML
import net.minecraft.core.BlockPos
import org.joml.Vector3d
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.api.ships.ShipForcesInducer
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import java.util.concurrent.CopyOnWriteArrayList


//TODO: make the gyro actually gyro
class KontraptionGyroShipControl : ShipForcesInducer {

    //thank vs tournament for inspiration :heart:
    private val gyros = CopyOnWriteArrayList<Triple<Vector3i, Vector3d, Double>>()

    val forcee = 100000.0

    override fun applyForces(physShip: PhysShip) {
        physShip as PhysShipImpl
        gyros.forEach {
            val (_, torque, power) = it

            val torqueGlobal = physShip.transform.shipToWorldRotation.transform(torque, Vector3d())

            println("yoinkers: " + power*forcee)

            physShip.applyInvariantTorque(torqueGlobal.mul(power*forcee))

        }
    }

    fun controlAll(forceDirection: Vector3d, power: Double) {
        gyros.forEach {
            val (pos, direction, tier) = it
            stopGyro(pos.toBlockPos())
            addGyro(pos.toBlockPos(), power, forceDirection)
        }
    }


    fun addGyro(pos: BlockPos, tier: Double, direction: Vector3d) {
        gyros.add(Triple(pos.toJOML(), direction, tier))
    }

    fun removeGyro(pos: BlockPos, tier: Double, direction: Vector3d) {
        gyros.removeAll { it.first == pos.toJOML() }
        //gyros.remove(Triple(pos.toJOML(), direction, tier))
    }

    fun stopGyro(pos: BlockPos) {
        gyros.removeAll { it.first == pos.toJOML() }
    }

    companion object {
        fun getOrCreate(ship: ServerShip): KontraptionGyroShipControl {
            return ship.getAttachment<KontraptionGyroShipControl>()
                    ?: KontraptionGyroShipControl().also { ship.saveAttachment(it) }
        }
    }


}
