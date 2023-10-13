package net.illuc.kontraption.network

import org.joml.Vector3f
import org.valkyrienskies.core.impl.networking.simple.SimplePacket

data class KontraptionPacketPlayerDriving(
        val impulse: Vector3f,
        val rotation: Vector3f,
) : SimplePacket