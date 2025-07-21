package net.illuc.kontraption.util

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.Vec3
import net.minecraftforge.items.ItemStackHandler
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.properties.ShipId
import java.lang.Math.toRadians
import kotlin.math.cos
import kotlin.math.sin

object OttUtils {
    /** maybe more than one use, dunnon lol
     * @param center Center Of MultiBLock
     * @param target Your Current possition
     * @return Returns direction from center to target
     * maybe lil inverted lmao
     * */
    fun getDirectionFromPositions(
        center: BlockPos,
        target: BlockPos,
    ): Direction {
        val dx = target.x - center.x
        val dy = target.y - center.y
        val dz = target.z - center.z

        val absDx = kotlin.math.abs(dx)
        val absDy = kotlin.math.abs(dy)
        val absDz = kotlin.math.abs(dz)

        return when {
            absDx >= absDy && absDx >= absDz -> {
                if (dx > 0) Direction.EAST else Direction.WEST
            }

            absDz >= absDx && absDz >= absDy -> {
                if (dz > 0) Direction.SOUTH else Direction.NORTH
            }
            // We RALLY hope that our directions are correct lmao
            else -> {
                if (dy > 0) Direction.UP else Direction.DOWN
            }
        }
    }

    /**
     * @param width Width of ring
     * @param height Ammout of layers
     * @param depth Height of ring
     * @return Returns Array of ByteArrays that represents ion ring structure of gives size*/
    fun generateAllowedLayers(
        width: Int,
        height: Int,
        depth: Int,
    ): Array<Array<ByteArray>> =
        Array(height) { layer ->
            Array(width) { x ->
                ByteArray(depth) { z ->
                    val isCorner = (x == 0 || x == width - 1) && (z == 0 || z == depth - 1)
                    val isEdge = (x == 0 || x == width - 1 || z == 0 || z == depth - 1)
                    val isInner = (x > 0 && x < width - 1) && (z > 0 && z < depth - 1)
                    val isnInner = (x > 1 && x < width - 2) && (z > 1 && z < depth - 2)
                    val isSpecialCell = (x <= 1 || x >= width - 2) && (z == 0 || z == 1 || z == depth - 1 || z == depth - 2) && !isCorner
                    when {
                        layer == height - 1 && isCorner -> 4
                        isSpecialCell && !isInner -> 1
                        layer == height - 1 && isEdge -> 3
                        isCorner && layer == 0 -> 1
                        isCorner -> 2
                        layer > 0 && isEdge -> 1
                        !isnInner && isInner && layer == 0 -> 1
                        !isnInner && isInner && layer > 0 -> 5
                        layer > 0 -> 0
                        else -> if (isEdge) 2 else 0
                    }
                }
            }
        }

    /**
     * Performs a ray trace (line-of-sight/block intersection) from a given block position and orientation.
     *
     * @param distance      The length (in blocks) of the ray trace.
     * @param blockEntity   The BlockEntity from whose world/level the operation is performed.
     * @param start         The BlockPos to start tracing from (the origin/block center).
     * @param facing        The base Direction the trace is aligned to.
     * @param ship          (Optional) The ServerShip context for performing ship-to-world transformations.
     * @param yawDegrees    (Optional) Rotation (in degrees) around the Y axis (left/right), relative to the facing direction. Default: 0.
     * @param pitchDegrees  (Optional) Rotation (in degrees) around the X axis (up/down tilt). Default: 0.
     * @param startOffset   (Optional) How far from the starting block along the facing direction to begin the trace (in blocks). Default: 0.7.
     *
     * @return The first BlockPos hit by the ray, or null if nothing is hit within the given distance.
     */
    // I def didnt use AI to get param :3

    fun rayTrace(
        distance: Double,
        blockEntity: BlockEntity,
        start: BlockPos,
        facing: Direction,
        ship: ServerShip? = null,
        yawDegrees: Float = 0f,
        pitchDegrees: Float = 0f,
        startOffset: Double = 0.7,
        // anything above like 2 would be a issue(cus dis aint a mortar)
    ): BlockPos? {
        val serverLevel = blockEntity.level as? ServerLevel ?: return null

        val yawRad = toRadians(yawDegrees.toDouble())
        val pitchRad = toRadians(pitchDegrees.toDouble())

        // uhhhhhh had to ask chat abt that bc i frickin suck at math
        val relativeX = sin(yawRad) * cos(pitchRad)
        val relativeY = sin(pitchRad)
        val relativeZ = cos(yawRad) * cos(pitchRad)

        // OUHC?? i hope i didnt make a mistake
        val worldVec =
            when (facing) {
                Direction.NORTH -> Vec3(-relativeX, relativeY, -relativeZ)
                Direction.SOUTH -> Vec3(relativeX, relativeY, relativeZ)
                Direction.WEST -> Vec3(-relativeZ, relativeY, relativeX)
                Direction.EAST -> Vec3(relativeZ, relativeY, -relativeX)
                Direction.UP -> Vec3(relativeX, relativeZ, relativeY)
                Direction.DOWN -> Vec3(relativeX, -relativeZ, -relativeY)
            }.normalize()

        val startVec =
            Vec3
                .atCenterOf(start)
                .add(Vec3.atLowerCornerOf(facing.normal).scale(startOffset))

        val endVec = startVec.add(worldVec.scale(distance))

        val (finalStart, finalEnd) =
            ship?.let {
                transformVecForShip(startVec, endVec, it)
            } ?: (startVec to endVec)

        return serverLevel
            .clip(
                ClipContext(
                    finalStart,
                    finalEnd,
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    null,
                ),
            ).blockPos
    }

    // Stolen from TEConnector
    fun getFacingDirection(blockState: BlockState) = blockState.getValue(BlockStateProperties.FACING)

    fun transformVecForShip(
        startVec: Vec3,
        endVec: Vec3,
        ship: ServerShip,
    ): Pair<Vec3, Vec3> {
        val worldStartVec = ship.transform.shipToWorld.transformPosition(fV3V3d(startVec))
        val worldEndVec = ship.transform.shipToWorld.transformPosition(fV3V3d(endVec))
        return Pair(fV3dV3(worldStartVec), fV3dV3(worldEndVec))
    }

    fun fVdVdc(vector3d: Vector3d): Vector3dc {
        val vectors: Vector3dc = vector3d
        return vectors
    }

    fun fV3V3d(vec3: Vec3): Vector3d {
        val xx = vec3.x
        val xy = vec3.y
        val xz = vec3.z
        return Vector3d(xx, xy, xz)
    }

    fun fV3dV3(vector3d: Vector3d): Vec3 {
        val xx = vector3d.x
        val xy = vector3d.y
        val xz = vector3d.z
        return Vec3(xx, xy, xz)
    } // if i ever need to use those again ima just put them in seperate files, NO IDEA why normal .toVector3d or .toVec3 didnt want to work here

    fun getDimID(serverLevel: ServerLevel): ShipId? {
        val dimID = KontraptionVSUtils.dimensionID(serverLevel)
        val dimshipID = KontraptionVSUtils.getShipObjectWorld(serverLevel).dimensionToGroundBodyIdImmutable[dimID]
        return dimshipID
    }

    fun canInesrtItemStack(
        stack: ItemStack,
        inventory: ItemStackHandler,
    ): Boolean {
        var remaining = stack.count

        for (i in 0 until inventory.slots) {
            val slotStack = inventory.getStackInSlot(i)
            if (slotStack.isEmpty) {
                val space = minOf(remaining, stack.maxStackSize)
                remaining -= space
            } else if (ItemStack.isSameItemSameTags(slotStack, stack) && slotStack.count < slotStack.maxStackSize) {
                val space = minOf(remaining, slotStack.maxStackSize - slotStack.count)
                remaining -= space
            }

            if (remaining <= 0) {
                return true
            }
        }
        return false
    }
}
