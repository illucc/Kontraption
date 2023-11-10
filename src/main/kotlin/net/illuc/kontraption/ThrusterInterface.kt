package net.illuc.kontraption

import net.illuc.kontraption.ship.KontraptionShipControl
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
    var powered: Boolean
    fun enable() {
        if (thrusterLevel !is ServerLevel) return
        println("ENABLED")
        enabled = true
        if (worldPosition != null) {
            val ship = KontraptionVSUtils.getShipObjectManagingPos((thrusterLevel as ServerLevel), worldPosition)
                    ?: KontraptionVSUtils.getShipManagingPos((thrusterLevel as ServerLevel), worldPosition)
                    ?: return

            KontraptionShipControl.getOrCreate(ship).let {
                it.stopThruster(worldPosition!!)
                it.addThruster(
                        worldPosition!!,
                        this.forceDirection
                                .normal
                                .toJOMLD(),
                        thrusterPower,
                        this


                )
            }
        }
    }

    fun disable() {
        println("DISABLED")
        if (thrusterLevel !is ServerLevel) return

        enabled = false

        KontraptionShipControl.getOrCreate(
                KontraptionVSUtils.getShipObjectManagingPos((thrusterLevel as ServerLevel), worldPosition)
                        ?: KontraptionVSUtils.getShipManagingPos((thrusterLevel as ServerLevel), worldPosition)
                        ?: return
        ).stopThruster(worldPosition!!)
    }

}