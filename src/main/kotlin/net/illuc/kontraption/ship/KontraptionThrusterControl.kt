package net.illuc.kontraption.ship

import net.illuc.kontraption.ThrusterInterface
import net.illuc.kontraption.config.KontraptionConfigs
import net.illuc.kontraption.util.toJOML
import net.minecraft.core.BlockPos
import org.joml.Vector3d
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.*
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.max
import kotlin.math.min

class KontraptionThrusterControl : ShipForcesInducer {
    data class Thruster(
        val position: Vector3i,
        val forceDirection: Vector3d,
        val forceStrength: Double,
        val thruster: ThrusterInterface,
    )

    private val thrusters = CopyOnWriteArrayList<Thruster>()

    val thrusterStrength = 100000.0

    override fun applyForces(physShip: PhysShip) {
        physShip as PhysShipImpl
        physShip.applyInvariantForce(
            physShip.poseVel.vel
                .negate(Vector3d())
                .mul(physShip.inertia.shipMass)
                .mul(KontraptionConfigs.kontraption.dampeningStrength.get()),
        ) // YEET in future
        // also BRR lambda
        thrusters.forEach { (position, forceDirection, forceStrengthORG, be) ->
            val forceStrength = be.currentThrust
            if (forceStrength != 0.0) {
                val tForce = physShip.transform.shipToWorld.transformDirection(forceDirection, Vector3d())
                val tPos = Vector3d(0.0, 0.0, 0.0) // position.toDouble().add(0.5, 0.5, 0.5).sub(physShip.transform.positionInShip)
                if (forceDirection.isFinite) {
                    var forceFinal = forceStrength * thrusterStrength
                    if (forceFinal > 0) {
                        be.powered = true
                        // credits to cjverycool, username checks out (cjcool1 on disc)
                        // /Artificial Gradient Speed Limit, gradually reduces thrust in the side that exceeds cap by interpolation
                        val thrustBufferRegion = 20
                        val dropOffThreshold = KontraptionConfigs.kontraption.thrusterSpeedLimit.get() - thrustBufferRegion
                        val dirVelocity = Vector3d(tForce).mul(physShip.poseVel.vel)
                        val dotVelocity = tForce.dot(physShip.poseVel.vel)
                        if (dirVelocity.length() > dropOffThreshold) {
                            var dropoffCoefficient =
                                max(0.0, min(1.0, (dropOffThreshold - dirVelocity.length()) / thrustBufferRegion + 1))
                            // Mekanism.logger.info(forceFinal);
                            dropoffCoefficient =
                                if (dotVelocity > 0) {
                                    dropoffCoefficient
                                } else {
                                    2 - (dropoffCoefficient); // set this to 0 if you dont need le boost
                                }
                            forceFinal *= dropoffCoefficient
                        }
                        physShip.applyInvariantForceToPos(tForce.mul(forceFinal), tPos)
                    }
                }
            } else {
                be.powered = false
            }
        }
    }

    fun addThruster(
        pos: BlockPos,
        force: Vector3d,
        tier: Double,
        thruster: ThrusterInterface,
    ) {
        thrusters.add(Thruster(pos.toJOML(), force, tier, thruster))
    }

    fun removeThruster(
        pos: BlockPos,
        force: Vector3d,
        tier: Double,
        thruster: ThrusterInterface,
    ) {
        thrusters.remove(Thruster(pos.toJOML(), force, tier, thruster))
    }

    fun stopThruster(pos: BlockPos) {
        thrusters.removeAll { it.position == pos.toJOML() }
    }

    fun assignThrustPerDirection(
        requestedThrust: Map<Vector3d, Double>,
        setMax: Boolean,
    ) {
        val groups = thrusters.groupBy { it.forceDirection }
        for ((direction, totalThrust) in requestedThrust) {
            val thrustersInDirection = groups[direction] ?: continue
            if (!setMax) {
                val splitThrust = totalThrust / thrustersInDirection.size
                for (thruster in thrustersInDirection) {
                    thruster.thruster.currentThrust = splitThrust.coerceIn(0.0, thruster.thruster.thrusterPower)
                }
            } else {
                if (totalThrust > 0) {
                    for (thruster in thrustersInDirection) {
                        thruster.thruster.currentThrust = thruster.thruster.thrusterPower
                    }
                }
            }
        }
        thrusters
            .filter { it.forceDirection !in requestedThrust.keys }
            .forEach { it.thruster.currentThrust = 0.0 }
    }

    companion object {
        fun getOrCreate(ship: ServerShip): KontraptionThrusterControl =
            ship.getAttachment<KontraptionThrusterControl>()
                ?: KontraptionThrusterControl().also { ship.saveAttachment(it) }
    }
}
