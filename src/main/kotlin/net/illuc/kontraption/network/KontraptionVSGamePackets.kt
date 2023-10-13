package net.illuc.kontraption.network

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.setAttachment
import org.valkyrienskies.core.impl.networking.simple.register
import org.valkyrienskies.core.impl.networking.simple.registerClientHandler
import org.valkyrienskies.core.impl.networking.simple.registerServerHandler
import org.valkyrienskies.mod.common.entity.handling.VSEntityManager
import net.illuc.kontraption.util.KontraptionVSUtils
import net.illuc.kontraption.controls.KontraptionSeatedControllingPlayer
import net.illuc.kontraption.entity.KontraptionShipMountingEntity
import org.valkyrienskies.mod.common.networking.PacketRestartChunkUpdates
import org.valkyrienskies.mod.common.networking.PacketStopChunkUpdates
import org.valkyrienskies.mod.common.networking.PacketSyncVSEntityTypes
import org.valkyrienskies.mod.common.util.MinecraftPlayer

object KontraptionVSGamePackets { //yoinkered from the vs2 github

    fun register() {
        KontraptionPacketPlayerDriving::class.register()
        /*PacketStopChunkUpdates        ::class.register()
        PacketRestartChunkUpdates     ::class.register()
        */PacketSyncVSEntityTypes       ::class.register()
    }

    fun registerHandlers() {
        KontraptionPacketPlayerDriving::class.registerServerHandler { driving, iPlayer ->
            println("registeringHandlers")
            val player = (iPlayer as MinecraftPlayer).player as ServerPlayer
            val seat = player.vehicle as? KontraptionShipMountingEntity
                    ?: return@registerServerHandler
            if (true) {//(seat.isController) {
                println("seat is controller :thumbsup")
                val ship = KontraptionVSUtils.getShipObjectManagingPos(seat.level, seat.blockPosition()) as? LoadedServerShip
                        ?: return@registerServerHandler

                val attachment: KontraptionSeatedControllingPlayer = ship.getAttachment()
                        ?: KontraptionSeatedControllingPlayer(seat.direction.opposite).apply { ship.setAttachment(this) }

                attachment.forwardImpulse   = driving.impulse.z
                attachment.leftImpulse      = driving.impulse.x
                attachment.upImpulse        = driving.impulse.y
                attachment.pitch            = driving.rotation.x
                attachment.yaw              = driving.rotation.y
                attachment.roll             = driving.rotation.z
            }
        }
        println("AAAAAAAAAAAAAAAAAA")

        // Syncs the entity handlers to the client
        PacketSyncVSEntityTypes::class.registerClientHandler { syncEntities ->
            syncEntities.entity2Handler.forEach { (id, handler) ->
                VSEntityManager.pair(
                        Registry.ENTITY_TYPE.byId(id),
                        ResourceLocation.tryParse(handler)?.let { VSEntityManager.getHandler(it) }
                                ?: throw IllegalStateException("No handler: $handler")
                )
            }
        }
    }
}