package net.illuc.kontraption

import net.illuc.kontraption.ship.KontraptionThrusterControl
import net.illuc.kontraption.util.KontraptionVSUtils
import net.illuc.kontraption.util.toJOMLD
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level

interface ThrusterInterface {
    val thrusterLevel: Level?
    var enabled: Boolean
    val worldPosition: BlockPos?
    val forceDirection: Direction
    val thrusterPower: Double
    val basePower: Double
    var powered: Boolean
    var currentThrust: Double

    fun enable(
        level: ServerLevel? = thrusterLevel as? ServerLevel,
        bpos: BlockPos? = worldPosition,
    ) {
        if (level !is ServerLevel) return // INITAL FUCKIN SETUP
        // println("ENABLED")
        if (worldPosition != null) {
            val ship =
                KontraptionVSUtils.getShipObjectManagingPos(level, bpos)
                    ?: KontraptionVSUtils.getShipManagingPos(level, bpos)
                    ?: return

            KontraptionThrusterControl.getOrCreate(ship).let {
                it.stopThruster(bpos!!)
                it.addThruster(
                    bpos,
                    this.forceDirection
                        .normal
                        .toJOMLD(),
                    thrusterPower,
                    this,
                )
            }
        }
    }

    fun disable() {
        // println("DISABLED THRUSTER INTERFACE") // DIS IS FUCKIN NOT DISABLE BUT REMOVE
        if (thrusterLevel !is ServerLevel) return

        enabled = false

        KontraptionThrusterControl
            .getOrCreate(
                KontraptionVSUtils.getShipObjectManagingPos((thrusterLevel as ServerLevel), worldPosition)
                    ?: KontraptionVSUtils.getShipManagingPos((thrusterLevel as ServerLevel), worldPosition)
                    ?: return,
            ).stopThruster(worldPosition!!)
    }
}
