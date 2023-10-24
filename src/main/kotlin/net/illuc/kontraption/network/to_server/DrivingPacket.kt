package net.illuc.kontraption.network.to_server

import mekanism.common.network.IMekanismPacket
import net.illuc.kontraption.controls.KontraptionSeatedControllingPlayer
import net.illuc.kontraption.entity.KontraptionShipMountingEntity
import net.illuc.kontraption.network.KontraptionPacketPlayerDriving
import net.illuc.kontraption.util.KontraptionVSUtils
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.network.NetworkEvent
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.setAttachment
import org.valkyrienskies.core.impl.networking.simple.registerServerHandler
import org.valkyrienskies.mod.common.util.MinecraftPlayer

class DrivingPacket : IMekanismPacket {
    override fun handle(context: NetworkEvent.Context?) {
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
                println(attachment.yaw)
            }
        }
    }

    override fun encode(buffer: FriendlyByteBuf?) {
        TODO("Not yet implemented")
    }
}