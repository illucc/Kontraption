package net.illuc.kontraption.util

import net.minecraft.core.BlockPos
import org.joml.Vector2d
import org.joml.Vector3d
import org.joml.Vector3i

fun Vector3i.toDouble() : Vector3d {
    return Vector3d(x.toDouble(), y.toDouble(), z.toDouble())
}

fun Vector3d.to2d() : Vector2d {
    return Vector2d(x, z)
}

fun Vector2d.to3d() : Vector3d {
    return Vector3d(x, 0.0, y)
}

fun Vector3d.toBlock() : BlockPos {
    return BlockPos(x, y, z)
}