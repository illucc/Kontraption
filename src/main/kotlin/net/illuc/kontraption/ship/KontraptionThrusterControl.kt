package net.illuc.kontraption.ship

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import net.illuc.kontraption.Kontraption
import net.illuc.kontraption.ThrusterInterface
import net.illuc.kontraption.config.KontraptionConfigs
import net.illuc.kontraption.util.toJOML
import net.illuc.kontraption.util.toJOMLD
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import org.joml.Vector3d
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.*
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.toVector3d
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.toVector3f
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.abs

@JsonIgnoreProperties(ignoreUnknown = true) // FOR MY SANITY
class KontraptionThrusterControl : ShipForcesInducer {
    data class Thruster(
        val position: Vector3i,
        val forceDirection: Vector3d,
        val forceStrength: Double,
        val thruster: ThrusterInterface,
    )

    // Lists
    private val thrusters = CopyOnWriteArrayList<Thruster>()

    // Local config bs
    private val CONFIGSPEEDLIMIT = KontraptionConfigs.kontraption.thrusterSpeedLimit.get()
    private val RESISTSTRENGHT = KontraptionConfigs.kontraption.dampeningStrength.get()

    // vars
    private var dampenerActive = false
    private var lastVelo = Vector3d(0.0, 0.0, 0.0)
    private var accError = Vector3d(0.0, 0.0, 0.0)
    private var playerInput = Vector3d() // have to store it bc we cant just apply force anywhere

    override fun applyForces(physShip: PhysShip) {
        physShip as PhysShipImpl

        val velocity = Vector3d(physShip.poseVel.vel)
        val mass = physShip.inertia.shipMass
        // val airResistance = velocity.mul(-mass * RESISTSTRENGHT)
        // physShip.applyInvariantForce(airResistance)
        val currentVelocity = physShip.poseVel.vel
        val velocityDelta = Vector3d(currentVelocity).sub(lastVelo)
        lastVelo.set(currentVelocity)

        applyThrusterDampener(physShip, velocityDelta)
        thrusters.forEach { (position, forceDirection, _, be) ->
            val forceStrength = be.currentThrust
            if (forceStrength != 0.0) {
                Kontraption.LOGGER.debug("Thruster {} has currentThrust={}", be, forceStrength)
                val tForce = physShip.transform.shipToWorld.transformDirection(forceDirection, Vector3d())
                val tPos = Vector3d(0.0, 0.0, 0.0)

                if (tForce.isFinite) {
                    var forceFinal = forceStrength * be.thrusterPower * 1000
                    Kontraption.LOGGER.debug("Applying force {} * {}", tForce, forceFinal) // InteliJ idea
                    be.powered = true

                    val isPlayerThrust = (
                        playerInput.lengthSquared() > 0.001 &&
                            tForce.dot(physShip.transform.shipToWorld.transformDirection(playerInput, Vector3d())) > 0.5
                    )

                    if (isPlayerThrust) {
                        val thrustBufferRegion = 20
                        val dropOffThreshold = CONFIGSPEEDLIMIT - thrustBufferRegion
                        val dirVelocity = Vector3d(tForce).mul(physShip.poseVel.vel)
                        val dotVelocity = tForce.dot(physShip.poseVel.vel)

                        if (dirVelocity.length() > dropOffThreshold) {
                            var dropoffCoefficient =
                                ((dropOffThreshold - dirVelocity.length()) / thrustBufferRegion + 1)
                                    .coerceIn(0.0, 1.0)

                            if (dotVelocity < 0) {
                                dropoffCoefficient = 2 - dropoffCoefficient
                            }

                            forceFinal *= dropoffCoefficient
                        }
                    }
                    Kontraption.LOGGER.debug("Aplying final force of {} and tForce of {}", forceFinal, tForce)
                    physShip.applyInvariantForceToPos(tForce.mul(forceFinal), tPos)
                }
            } else {
                be.powered = false
            }
        }
    }

    private fun applyThrusterDampener(
        physShip: PhysShipImpl,
        velocityDelta: Vector3d,
    ) {
        val mass = physShip.inertia.shipMass
        val config = KontraptionConfigs.kontraption

        val worldToShip = physShip.transform.worldToShip
        val shipToWorld = physShip.transform.shipToWorld

        val shipError = worldToShip.transformDirection(Vector3d(physShip.poseVel.vel).negate(), Vector3d())

        val newError = shipToWorld.transformDirection(shipError, Vector3d())

        val strengthByDirection =
            thrusters
                .groupBy { it.forceDirection }
                .mapValues { (_, group) -> group.sumOf { it.thruster.thrusterPower } }
        val thrusterStrength = thrusters.sumOf { it.thruster.thrusterPower } // One is total and one is directional ship strenght

        accError.add(Vector3d(newError).mul(0.1))

        val derivative = velocityDelta.negate()

        val dampeningForce =
            Vector3d(newError)
                .mul(config.dampeningP.get() * mass)
                .add(Vector3d(accError).mul(config.dampeningI.get() * mass))
                .add(derivative.mul(config.dampeningD.get() * mass))
        if (abs(playerInput.x) > 0.1) dampeningForce.x = 0.0
        if (abs(playerInput.y) > 0.1) dampeningForce.y = 0.0
        if (abs(playerInput.z) > 0.1) dampeningForce.z = 0.0
        if (abs(playerInput.x) > 0.1) accError.x = 0.0
        if (abs(playerInput.y) > 0.1) accError.y = 0.0
        if (abs(playerInput.z) > 0.1) accError.z = 0.0
        val totalForce = Vector3d(dampeningForce)

        if (playerInput.lengthSquared() > 0.001) {
            playerInput.x = playerInput.x * -1
            playerInput.z = playerInput.z * -1
            totalForce.add(Vector3d(playerInput).normalize().mul(thrusterStrength))
        }

        val requestedThrust = mutableMapOf<Vector3d, Double>()

        Direction.entries.forEach { dir ->
            val dirVecr = dir.normal.toJOMLD()
            val worldDir = shipToWorld.transformDirection(dir.normal.toJOMLD(), Vector3d())
            val dot = worldDir.dot(totalForce)
            val avaibleStrn = strengthByDirection[dirVecr] ?: 0.0
            if (dot > 0 && avaibleStrn > 0) {
                requestedThrust[dir.normal.toJOMLD()] = (dot / avaibleStrn).coerceIn(0.0, 1.0)
            }
        }

        assignThrustPerDirection(requestedThrust, false)
    }

    fun setDampenersState(st: Boolean) {
        dampenerActive = st
        if (!st) {
            accError = Vector3d(0.0, 0.0, 0.0).toVector3f().toVector3d() // DONT EVEN ASK
            lastVelo = Vector3d(0.0, 0.0, 0.0)
        }
    }

    fun setPlayerInput(input: Vector3d) {
        playerInput.set(input)
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
                        Kontraption.LOGGER.debug("Applied Thrust: ${thruster.thruster.currentThrust}")
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
