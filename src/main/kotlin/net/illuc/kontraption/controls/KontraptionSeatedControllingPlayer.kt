package net.illuc.kontraption.controls

import net.minecraft.core.Direction

class KontraptionSeatedControllingPlayer(val seatInDirection: Direction) {
    var forwardImpulse: Float = 0.0f
    var leftImpulse: Float = 0.0f
    var upImpulse: Float = 0.0f
    var pitch: Float = 0.0f
    var yaw: Float = 0.0f
    var roll: Float = 0.0f
}