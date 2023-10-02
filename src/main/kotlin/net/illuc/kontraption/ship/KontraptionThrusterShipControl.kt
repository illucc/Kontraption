package net.illuc.kontraption.ship

import com.fasterxml.jackson.annotation.JsonIgnore
import net.illuc.kontraption.util.toDouble
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.player.Player
import org.joml.Vector3d
import org.joml.Vector3i
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.mod.api.SeatedControllingPlayer
import java.util.concurrent.CopyOnWriteArrayList
import org.valkyrienskies.mod.common.util.*
import org.valkyrienskies.*
import org.valkyrienskies.core.api.ships.*
import org.valkyrienskies.core.impl.api.ServerShipUser
import org.valkyrienskies.core.impl.api.Ticked
import org.valkyrienskies.core.impl.api.shipValue
import org.valkyrienskies.core.impl.api.ShipForcesInducer


class KontraptionThrusterShipControl : ShipForcesInducer, ServerShipUser, Ticked {

    private val Thrusters = mutableListOf<Triple<Vector3i, Vector3d, Double>>()

    private val thrusters = CopyOnWriteArrayList<Triple<Vector3i, Vector3d, Double>>()


    override var ship: ServerShip? = null

    private val controllingPlayer by shipValue<SeatedControllingPlayer>()

    private var controlData: ControlData? = null

    @JsonIgnore
    var seatedPlayer: Player? = null

    private data class ControlData(
            val seatInDirection: Direction,
            var forwardImpulse: Float = 0.0f,
            var leftImpulse: Float = 0.0f,
            var upImpulse: Float = 0.0f,
            var sprintOn: Boolean = false
    ) {
        companion object {
            fun create(player: SeatedControllingPlayer): ControlData {
                return ControlData(
                        player.seatInDirection,
                        player.forwardImpulse,
                        player.leftImpulse,
                        player.upImpulse,
                        player.sprintOn
                )
            }
        }
    }



    override fun applyForces(physShip: PhysShip) {
        physShip as PhysShipImpl

        Thrusters.forEach {
            thrusters.add(it)
        }
        Thrusters.clear()

        thrusters.forEach {
            val (pos, force, tier) = it

            val tForce = physShip.transform.shipToWorld.transformDirection(force, Vector3d())
            val tPos = pos.toDouble().add(0.5, 0.5, 0.5).sub(physShip.transform.positionInShip)

            if (force.isFinite){
                physShip.applyInvariantForceToPos(tForce.mul(1 * tier), tPos)
            }
        }

    }


    fun addThruster(pos: BlockPos, tier: Double, force: Vector3d) {
        thrusters.add(Triple(Vector3i(pos.x, pos.y, pos.z), force, tier))
    }

    fun removeThruster(pos: BlockPos, tier: Double, force: Vector3d) {
        thrusters.remove(Triple(Vector3i(pos.x, pos.y, pos.z), force, tier))
    }

    fun stopThruster(pos: BlockPos) {
        thrusters.removeAll { it.first == Vector3i(pos.x, pos.y, pos.z)}
    }

    companion object {
        fun getOrCreate(ship: ServerShip): KontraptionThrusterShipControl {
            return ship.getAttachment<KontraptionThrusterShipControl>()
                    ?: KontraptionThrusterShipControl().also { ship.saveAttachment(it) }
        }
    }

    override fun tick() {
        TODO("Not yet implemented")
    }


}