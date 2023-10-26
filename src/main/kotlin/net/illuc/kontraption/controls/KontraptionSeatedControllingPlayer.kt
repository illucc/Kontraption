package net.illuc.kontraption.controls

import net.minecraft.core.Direction

class KontraptionSeatedControllingPlayer(val seatInDirection: Direction) {
    var forwardImpulse: Double = 0.0
    var leftImpulse: Double = 0.0
    var upImpulse: Double = 0.0
    var pitch: Double = 0.0
    var yaw: Double = 0.0
    var roll: Double = 0.0
}