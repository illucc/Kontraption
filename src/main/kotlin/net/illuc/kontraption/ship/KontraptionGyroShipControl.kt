package net.illuc.kontraption.ship

import com.fasterxml.jackson.annotation.JsonIgnore
import net.illuc.kontraption.util.toBlockPos
import net.illuc.kontraption.util.toDouble
import net.illuc.kontraption.util.toJOML
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.player.Player
import org.joml.Vector3d
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.api.ships.ShipForcesInducer
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.mod.api.SeatedControllingPlayer
import java.util.concurrent.CopyOnWriteArrayList


//TODO: make the gyro actually gyro
class KontraptionGyroShipControl : ShipForcesInducer {
    private val Spinners = mutableListOf<Pair<Vector3i, Vector3d>>()

    private val spinners = CopyOnWriteArrayList<Pair<Vector3i, Vector3d>>()

    override fun applyForces(physShip: PhysShip) {
        physShip as PhysShipImpl

        Spinners.forEach {
            spinners.add(it)
        }
        spinners.clear()

        spinners.forEach {
            val (_, torque) = it

            val torqueGlobal = physShip.transform.shipToWorldRotation.transform(torque, Vector3d())

            physShip.applyInvariantTorque(torqueGlobal.mul(1000.0))

        }
    }

    fun controlAll(forceDirection: Vector3d, power: Double) {
        println(spinners)
        spinners.forEach {
            println("g" + it.second + " vs " + forceDirection)
            if (it.second == forceDirection){
                println("yoinky")
                val (pos, force) = it
                removeSpinner(pos, force)
                addSpinner(pos, forceDirection.mul(power))
            }

        }
    }

    fun addSpinner(pos: Vector3i, torque: Vector3d) {
        spinners.add(pos to torque)
    }
    fun removeSpinner(pos: Vector3i, torque: Vector3d) {
        spinners.remove(pos to torque)
    }

    companion object {
        fun getOrCreate(ship: ServerShip): KontraptionGyroShipControl {
            return ship.getAttachment<KontraptionGyroShipControl>()
                    ?: KontraptionGyroShipControl().also { ship.saveAttachment(it) }
        }
    }


}
