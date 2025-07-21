package net.illuc.kontraption.multiblocks.largeHydrogenThruster

import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import mekanism.common.content.blocktype.BlockType
import mekanism.common.lib.math.voxel.VoxelCuboid
import mekanism.common.lib.multiblock.CuboidStructureValidator
import mekanism.common.lib.multiblock.FormationProtocol
import mekanism.common.lib.multiblock.FormationProtocol.CasingType
import mekanism.common.lib.multiblock.FormationProtocol.FormationResult
import mekanism.common.util.WorldUtils
import net.illuc.kontraption.KontraptionBlockTypes
import net.illuc.kontraption.KontraptionLang
import net.illuc.kontraption.blockEntities.largehydrogen.TileEntityLiquidFuelThrusterExhaust
import net.illuc.kontraption.util.toJOMLD
import net.minecraft.core.BlockPos
import net.minecraft.core.BlockPos.MutableBlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.ChunkAccess

class LiquidFuelThrusterValidator :
    CuboidStructureValidator<LiquidFuelThrusterMultiblockData>(
        VoxelCuboid(3, 3, 3),
        VoxelCuboid(17, 18, 17),
    ) {
    override fun getCasingType(state: BlockState?): FormationProtocol.CasingType {
        val block: Block = state!!.block
        if (BlockType.`is`(block, KontraptionBlockTypes.LIQUID_FUEL_THRUSTER_CASING)) {
            return CasingType.FRAME
        } else if (BlockType.`is`(block, KontraptionBlockTypes.LIQUID_FUEL_THRUSTER_VALVE)) {
            return CasingType.VALVE
        } else if (BlockType.`is`(block, KontraptionBlockTypes.LIQUID_FUEL_THRUSTER_EXHAUST)) {
            return CasingType.OTHER
        }
        return CasingType.INVALID
    }

    override fun postcheck(
        structure: LiquidFuelThrusterMultiblockData?,
        chunkMap: Long2ObjectMap<ChunkAccess?>?,
    ): FormationResult? {
        if (structure!!.length() % 2 !== 1 || structure!!.width() % 2 !== 1) {
            // uneven
            // return FormationResult.fail(KontraptionLang.DESCRIPTION_ION_THRUSTER)
        }
        val centerX = structure!!.minPos.x + (structure!!.length() - 1) / 2
        val centerY = structure!!.minPos.y + (structure!!.height() - 1) / 2
        val centerZ = structure!!.minPos.z + (structure!!.width() - 1) / 2

        structure.center = BlockPos(centerX, centerY, centerZ)

        val innerRadius = (Math.min(structure.length(), structure.width()) - 3) / 2

        // thats the exhaust center thing i think
        val innerX = (structure.width() - 3) / 2
        val innerY = (structure.height() - 3) / 2
        val innerZ = (structure.length() - 3) / 2

        structure.innerVolume = ((structure.width() - 2) + (structure.length() - 2) + (structure.height() - 2)) / 3

        var exhausts: MutableSet<BlockPos> = ObjectOpenHashSet()
        var validExhausts: MutableSet<BlockPos> = ObjectOpenHashSet()

        // Get Direction
        var centerExhaust: TileEntityLiquidFuelThrusterExhaust? = null
        var exhaustDirection: Direction? = null

        val mutablePos = MutableBlockPos()
        for (dir in Direction.values()) {
            var dir2 = dir.normal.toJOMLD()
            mutablePos.set(
                (centerX + dir2.x * ((structure.width() - 1) / 2)),
                (centerY + dir2.y * ((structure.height() - 1) / 2)),
                (centerZ + dir2.z * ((structure.length() - 1) / 2)),
            )
            var tile = WorldUtils.getTileEntity(TileEntityLiquidFuelThrusterExhaust::class.java, world, chunkMap!!, mutablePos)
            // check if we have a exhaust at that position
            if (tile != null) {
                // check if we already have a side picked
                if (centerExhaust != null) {
                    return FormationResult.fail(KontraptionLang.PLACEHOLDER, mutablePos)
                }
                validExhausts.add(mutablePos.immutable())
                centerExhaust = tile
                structure.centerExhaust = centerExhaust
                exhaustDirection = dir
                structure.exhaustDirection = exhaustDirection
            }
        }
        if (centerExhaust != null) {
            if (exhaustDirection == Direction.UP || exhaustDirection == Direction.DOWN) {
                for (x in centerExhaust.blockPos.getX() - innerX..centerExhaust.blockPos.getX() + innerX) {
                    for (z in centerExhaust.blockPos.getZ() - innerZ..centerExhaust.blockPos.getZ() + innerZ) {
                        // if (x != centerX || z != centerZ) {
                        mutablePos.set(x, centerExhaust.blockPos.getY(), z)
                        val tile =
                            WorldUtils.getTileEntity(
                                TileEntityLiquidFuelThrusterExhaust::class.java,
                                world,
                                chunkMap!!,
                                mutablePos,
                            )
                        validExhausts.add(mutablePos.immutable())
                        structure.exhaustDiameter = structure.height() - 2
                        if (tile == null) {
                            return FormationResult.fail(KontraptionLang.DESCRIPTION_ION_THRUSTER, mutablePos)
                        }
                        // }
                    }
                }
            }

            if (exhaustDirection == Direction.EAST || exhaustDirection == Direction.WEST) {
                for (z in centerExhaust.blockPos.getZ() - innerZ..centerExhaust.blockPos.getZ() + innerZ) {
                    for (y in centerExhaust.blockPos.getY() - innerY..centerExhaust.blockPos.getY() + innerY) {
                        // if (z != centerZ || y != centerY) {
                        mutablePos.set(centerExhaust.blockPos.getX(), y, z)
                        val tile =
                            WorldUtils.getTileEntity(
                                TileEntityLiquidFuelThrusterExhaust::class.java,
                                world,
                                chunkMap!!,
                                mutablePos,
                            )
                        structure.exhaustDiameter = structure.width() - 2
                        validExhausts.add(mutablePos.immutable())
                        if (tile == null) {
                            return FormationResult.fail(KontraptionLang.DESCRIPTION_ION_THRUSTER, mutablePos)
                        }
                        // }
                    }
                }
            }

            if (exhaustDirection == Direction.NORTH || exhaustDirection == Direction.SOUTH) {
                for (x in centerExhaust.blockPos.getX() - innerX..centerExhaust.blockPos.getX() + innerX) {
                    for (y in centerExhaust.blockPos.getY() - innerY..centerExhaust.blockPos.getY() + innerY) {
                        // if (x != centerX || y != centerY) {
                        mutablePos.set(x, y, centerExhaust.blockPos.z)
                        val tile =
                            WorldUtils.getTileEntity(
                                TileEntityLiquidFuelThrusterExhaust::class.java,
                                world,
                                chunkMap!!,
                                mutablePos,
                            )
                        validExhausts.add(mutablePos.immutable())
                        structure.exhaustDiameter = structure.length() - 2
                        if (tile == null) {
                            return FormationResult.fail(KontraptionLang.DESCRIPTION_ION_THRUSTER, mutablePos)
                        }
                        // }
                    }
                }
            }
        } else {
            return FormationResult.fail(KontraptionLang.DESCRIPTION_ION_THRUSTER, mutablePos)
        }

        for (pos in structure.locations) {
            val tile = WorldUtils.getTileEntity(TileEntityLiquidFuelThrusterExhaust::class.java, world, chunkMap!!, pos)
            if (tile != null) {
                exhausts.add(pos)
            }
        }

        if (exhausts != validExhausts) {
            // println(exhausts)
            // println(validExhausts)
            return FormationResult.fail(KontraptionLang.PLACEHOLDER, mutablePos)
        }

        // :sob:
        // val mutablePos = MutableBlockPos()
        mutablePos.set(centerX, structure.minPos.y, centerZ)
        var tile = WorldUtils.getTileEntity(TileEntityLiquidFuelThrusterExhaust::class.java, world, chunkMap!!, mutablePos)
        if (tile == null) {
            // return FormationResult.fail(KontraptionLang.DESCRIPTION_ION_THRUSTER, mutablePos);
        }

        return FormationResult.SUCCESS
    }
}
