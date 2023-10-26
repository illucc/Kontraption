package net.illuc.kontraption.network.to_server

import mekanism.common.Mekanism
import mekanism.common.network.IMekanismPacket
import mekanism.common.network.to_server.PacketKey
import net.illuc.kontraption.controls.KontraptionSeatedControllingPlayer
import net.illuc.kontraption.entity.KontraptionShipMountingEntity
import net.illuc.kontraption.util.KontraptionVSUtils
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


public class PacketKontraptionDrivinggg : IMekanismPacket {

    lateinit var impulse: Vector3dc
    lateinit var rotation: Vector3dc


    private val key = 0
    private val add = false

    public fun PacketKontraptionDriving(imp: Vector3dc, rot: Vector3dc) {
        this.impulse = imp
        this.rotation = rot
    }

    override fun handle(context: NetworkEvent.Context) {
        val player: Player? = context.sender
        if (player != null) {
            val seat = player.vehicle as? KontraptionShipMountingEntity
            println("seat is controller :thumbsup")
            val ship = KontraptionVSUtils.getShipObjectManagingPos(seat!!.level, seat.blockPosition()) as? LoadedServerShip
                    ?: return

            val attachment: KontraptionSeatedControllingPlayer = ship.getAttachment()
                    ?: KontraptionSeatedControllingPlayer(seat.direction.opposite).apply { ship.setAttachment(this) }

            attachment.forwardImpulse   = impulse.z()
            attachment.leftImpulse      = impulse.x()
            attachment.upImpulse        = impulse.y()
            attachment.pitch            = rotation.x()
            attachment.yaw              = rotation.y()
            attachment.roll             = rotation.z()
            println(attachment.yaw)
        }
    }

    override fun encode(buffer: FriendlyByteBuf) {
        buffer.writeVec3d(impulse)
        buffer.writeVec3d(rotation)
    }

    fun decode(buffer: FriendlyByteBuf) {
        return PacketKontraptionDriving(buffer.readVec3d(), buffer.readVec3d())
    }

}