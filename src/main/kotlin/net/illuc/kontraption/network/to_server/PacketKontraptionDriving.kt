package net.illuc.kontraption.network.to_server

import mekanism.common.Mekanism
import mekanism.common.network.IMekanismPacket
import mekanism.common.network.to_server.PacketKey
import net.illuc.kontraption.controls.KontraptionSeatedControllingPlayer
import net.illuc.kontraption.entity.KontraptionShipMountingEntity
import net.illuc.kontraption.util.KontraptionVSUtils
import net.illuc.kontraption.util.toJOML
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Player
import net.minecraftforge.network.NetworkEvent
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3f
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.setAttachment
import org.valkyrienskies.core.util.readVec3d
import org.valkyrienskies.core.util.writeVec3AsFloat
import org.valkyrienskies.core.util.writeVec3d


class PacketKontraptionDriving(val impulse: Vector3dc, val rotation: Vector3dc) : IMekanismPacket {


    private val key = 0
    private val add = false

    override fun handle(context: NetworkEvent.Context) {
        val player: Player? = context.sender
        if (player != null) {
            val seat = player.vehicle as? KontraptionShipMountingEntity ?: return
            //fucking please dont do stuff if something is wrong
            if (seat.level() == null){
                return
            }
            val ship = (KontraptionVSUtils.getShipObjectManagingPos(seat.level(), seat.position().toJOML())) ?: return

            val attachment: KontraptionSeatedControllingPlayer = (ship as? LoadedServerShip)?.getAttachment()
                    ?: KontraptionSeatedControllingPlayer(seat.direction.opposite).apply { (ship as? LoadedServerShip)?.setAttachment(this) }

            attachment.forwardImpulse   = impulse.z()
            attachment.leftImpulse      = impulse.x()
            attachment.upImpulse        = impulse.y()
            attachment.pitch            = rotation.x()
            attachment.yaw              = rotation.y()
            attachment.roll             = rotation.z()
        }
    }

    override fun encode(buffer: FriendlyByteBuf) {
        buffer.writeVec3d(impulse)
        buffer.writeVec3d(rotation)
    }

    companion object {
        fun decode(buffer: FriendlyByteBuf) = PacketKontraptionDriving(buffer.readVec3d(), buffer.readVec3d())
    }
}