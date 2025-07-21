package net.illuc.kontraption.util

import it.zerono.mods.zerocore.lib.data.geometry.CuboidBoundingBox
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block

class StructReq(
    private val blockMappings: Map<Byte, List<Block>>,
) {
    var allowedLayers: Array<Array<ByteArray>> = Array(2) { Array(3) { ByteArray(4) } }

    /**
     * Checks if the block at a given position satisfies the structure requirement for this Array of Layers.
     * IF YOU ARE USING AIRC YOU MUST DEFINE 0 BYTE IN THE BLOCKMAP
     * @param world The world in which the multiblock is located(CANNOT BE RELATIVE)
     * @param pos The position of the block
     * @param airc If 0 should by default be Air
     * @param rotY Rotates the cuboid abstract Y by passed ammout of degrees
     * @param rotX Rotates the cuboid abstract X by passed ammout of degrees
     * @param rotZ Rotates the cuboid abstract Z by passed ammout of degrees
     * @return True if the block at the position meets the requirement, false otherwise
     */
    fun isValidBlock(
        world: Level,
        pos: BlockPos,
        airc: Boolean,
        cbb: CuboidBoundingBox,
        allowLayers: Array<Array<ByteArray>>,
        // (0, 90, 180, 270)
        rotY: Int = 0,
        rotX: Int = 0,
        rotZ: Int = 0,
    ): Pair<Boolean, Byte> {
        if (cbb.lengthY != allowLayers.size) return Pair(false, 0.toByte())
        allowedLayers = allowLayers
        val relativePos = pos.subtract(Vec3i(cbb.minX, cbb.minY, cbb.minZ))
        val (h, layer, v) =
            rotate(
                relativePos.x,
                relativePos.y,
                relativePos.z,
                cbb.lengthX,
                cbb.lengthY,
                cbb.lengthZ,
                rotY,
                rotX,
                rotZ,
            )
        if (
            layer !in allowLayers.indices ||
            h !in allowLayers[0].indices ||
            v !in allowLayers[0][0].indices
        ) {
            return Pair(false, 0.toByte())
        }
        val requiredType = allowLayers[layer][h][v]
        val validBlocks = blockMappings[requiredType] ?: return Pair(false, 0.toByte())
        val blockAtPosition = world.getBlockState(pos)

        return if (airc) {
            if (requiredType == 0.toByte()) {
                Pair(blockAtPosition.isAir, requiredType)
            } else {
                Pair(validBlocks.contains(blockAtPosition.block), requiredType)
            }
        } else {
            Pair(validBlocks.contains(blockAtPosition.block), requiredType)
        }
    }

    private fun outlineBlockWithParticles(
        level: ServerLevel,
        pos: BlockPos,
        particle: ParticleOptions,
        step: Double,
    ) {
        // tibia honest had to do some creative stealin with this and my brain hurtss
        val corners =
            arrayOf(
                Triple(0.0, 0.0, 0.0),
                Triple(1.0, 0.0, 0.0),
                Triple(1.0, 1.0, 0.0),
                Triple(0.0, 1.0, 0.0),
                Triple(0.0, 0.0, 1.0),
                Triple(1.0, 0.0, 1.0),
                Triple(1.0, 1.0, 1.0),
                Triple(0.0, 1.0, 1.0), 
                // easiest one
            )
        val edges =
            arrayOf(
                0 to 1,
                1 to 2,
                2 to 3,
                3 to 0,
                4 to 5,
                5 to 6,
                6 to 7,
                7 to 4,
                0 to 4,
                1 to 5,
                2 to 6,
                3 to 7,
            )

        for ((startIdx, endIdx) in edges) {
            val (x0, y0, z0) = corners[startIdx]
            val (x1, y1, z1) = corners[endIdx]

            val dx = x1 - x0
            val dy = y1 - y0
            val dz = z1 - z0
            val dist = Math.sqrt(dx * dx + dy * dy + dz * dz)
            val steps = (dist / step).toInt().coerceAtLeast(2)

            for (i in 0..steps) {
                val t = i.toDouble() / steps
                val px = pos.x + x0 + dx * t
                val py = pos.y + y0 + dy * t
                val pz = pos.z + z0 + dz * t
                level.sendParticles(particle, px, py, pz, 10, 0.0, 0.0, 0.0, 0.0)
            }
        }
    }

    fun previewAllowedBlockOutlines(
        basePos: BlockPos,
        serverLevel: ServerLevel,
        step: Double = 0.25,
    ) {
        for ((y, layer) in allowedLayers.withIndex()) {
            for ((z, row) in layer.withIndex()) {
                for ((x, byte) in row.withIndex()) {
                    if (byte.toInt() == 0) continue

                    val blockPos = basePos.offset(x, y, z)
                    val colorParticle =
                        when (byte) {
                            1.toByte() -> ParticleTypes.END_ROD // casing
                            2.toByte() -> ParticleTypes.FLAME // vlv or cont or casing
                            3.toByte() -> ParticleTypes.PORTAL // casing RENDERER
                            4.toByte() -> ParticleTypes.BUBBLE // casing CORNER
                            5.toByte() -> ParticleTypes.CHERRY_LEAVES // coil
                            else -> ParticleTypes.HAPPY_VILLAGER
                        }
                    outlineBlockWithParticles(serverLevel, blockPos, colorParticle, step)
                }
            }
        }
    }

    fun rotate(
        x: Int,
        y: Int,
        z: Int,
        sizeX: Int,
        sizeY: Int,
        sizeZ: Int,
        rotY: Int,
        rotX: Int,
        rotZ: Int,
    ): Triple<Int, Int, Int> {
        // HONESTLY?? made by chat, prob placeholder
        var (nx, ny, nz) = Triple(x, y, z)
        repeat((rotY % 360) / 90) {
            val oldX = nx
            nx = sizeZ - 1 - nz
            nz = oldX
        }
        repeat((rotX % 360) / 90) {
            val oldY = ny
            ny = sizeZ - 1 - nz
            nz = oldY
        }
        repeat((rotZ % 360) / 90) {
            val oldX = nx
            nx = sizeY - 1 - ny
            ny = oldX
        }
        return Triple(nx, ny, nz)
    }
}
