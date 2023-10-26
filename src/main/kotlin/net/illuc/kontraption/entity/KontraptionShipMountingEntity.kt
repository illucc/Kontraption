package net.illuc.kontraption.entity

import mekanism.common.Mekanism
import mekanism.common.network.to_server.PacketKey
import net.illuc.kontraption.Kontraption
import net.illuc.kontraption.config.KontraptionKeyBindings
import net.illuc.kontraption.network.to_server.PacketKontraptionDriving
import net.illuc.kontraption.util.KontraptionVSUtils.getShipObjectManagingPos
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.core.api.ships.setAttachment
import org.valkyrienskies.mod.api.SeatedControllingPlayer


open class KontraptionShipMountingEntity(type: EntityType<KontraptionShipMountingEntity>, level: Level) : Entity(type, level) {

    // Decides if this entity controlls the ship it is in.
    // Only needs to be set serverside
    var isController = false

    init {
        // Don't prevent blocks colliding with this entity from being placed
        blocksBuilding = false
        // Don't collide with terrain
        noPhysics = true
    }

    override fun tick() {
        super.tick()
        if (!level.isClientSide && passengers.isEmpty()) {
            // Kill this entity if nothing is riding it
            kill()
            return
        }
        if (getShipObjectManagingPos(level, blockPosition()) != null)
            println("sending packets :D")
            sendDrivingPacket()
    }

    override fun readAdditionalSaveData(compound: CompoundTag?) {
    }

    override fun addAdditionalSaveData(compound: CompoundTag?) {
    }

    override fun defineSynchedData() {
    }

    override fun remove(removalReason: RemovalReason) {
        if (this.isController && !level.isClientSide)
            (getShipObjectManagingPos(level, blockPosition()) as LoadedServerShip?)
                    ?.setAttachment<SeatedControllingPlayer>(null)
        super.remove(removalReason)
    }

    private fun sendDrivingPacket() {
        if (!level.isClientSide) return
        // qhar
        val opts = Minecraft.getInstance().options
        val forward   = opts.keyUp.isDown
        val backward  = opts.keyDown.isDown
        val left      = opts.keyLeft.isDown
        val right     = opts.keyRight.isDown
        val up        = KontraptionKeyBindings.shipUp.get().isDown
        val down      = KontraptionKeyBindings.shipDown.get().isDown
        val pitchUp   = KontraptionKeyBindings.pitchUp.get().isDown
        val pitchDown = KontraptionKeyBindings.pitchDown.get().isDown
        val yawUp     = KontraptionKeyBindings.yawUp.get().isDown
        val yawDown   = KontraptionKeyBindings.yawDown.get().isDown
        val rollUp    = KontraptionKeyBindings.rollUp.get().isDown
        val rollDown  = KontraptionKeyBindings.rollDown.get().isDown


        val impulse = Vector3d()
        impulse.z = if (forward == backward) 0.0 else if (forward) 1.0 else -1.0
        impulse.x = if (left == right) 0.0 else if (left) -1.0 else 1.0
        impulse.y = if (up == down) 0.0 else if (up) 1.0 else -1.0

        val rotation = Vector3d()
        rotation.x = if (pitchUp == pitchDown) 0.0 else if (pitchUp) 1.0 else -1.0
        rotation.y = if (yawUp == yawDown) 0.0 else if (yawUp) 1.0 else -1.0
        rotation.z = if (rollUp == rollDown) 0.0 else if (rollUp) 1.0 else -1.0



        Kontraption.packetHandler().sendToServer(PacketKontraptionDriving(impulse.x(), impulse.x(), impulse.x(), impulse.x(), impulse.x(), impulse.x()))


        //KontraptionPacketPlayerDriving(impulse, rotation).sendToServer()
    }

    override fun getControllingPassenger(): Entity? {
        return if (isController) this.passengers.getOrNull(0) else null
    }

    override fun getAddEntityPacket(): Packet<*> {
        return ClientboundAddEntityPacket(this)
    }
}