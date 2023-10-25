package net.illuc.kontraption.multiblocks.largeHydrogenThruster

import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import mekanism.common.content.blocktype.BlockType
import mekanism.common.lib.math.voxel.VoxelCuboid
import mekanism.common.lib.multiblock.CuboidStructureValidator
import mekanism.common.lib.multiblock.FormationProtocol
import mekanism.common.lib.multiblock.FormationProtocol.CasingType
import mekanism.common.lib.multiblock.FormationProtocol.FormationResult
import mekanism.common.tile.TileEntityPressureDisperser
import mekanism.common.util.WorldUtils
import net.illuc.kontraption.KontraptionBlockTypes
import net.illuc.kontraption.KontraptionLang
import net.illuc.kontraption.blockEntities.TileEntityHydrogenThrusterExhaust
import net.illuc.kontraption.util.toJOMLD
import net.minecraft.core.BlockPos
import net.minecraft.core.BlockPos.MutableBlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.ChunkAccess


class HydrogenThrusterValidator : CuboidStructureValidator<HydrogenThrusterMultiblockData>(VoxelCuboid(3, 3, 3), VoxelCuboid(17, 18, 17)) {





    override fun getCasingType(state: BlockState?): FormationProtocol.CasingType {
        val block: Block = state!!.block
        if (BlockType.`is`(block, KontraptionBlockTypes.HYDROGEN_THRUSTER_CASING)) {
            return CasingType.FRAME
        } else if (BlockType.`is`(block, KontraptionBlockTypes.HYDROGEN_THRUSTER_VALVE)) {
            return CasingType.VALVE
        } else if (BlockType.`is`(block, KontraptionBlockTypes.HYDROGEN_THRUSTER_EXHAUST)) {
            return CasingType.OTHER
        }
        return CasingType.INVALID

    }

    override fun postcheck(structure: HydrogenThrusterMultiblockData?, chunkMap: Long2ObjectMap<ChunkAccess?>?): FormationResult? {
        if (structure!!.length() % 2 !== 1 || structure!!.width() % 2 !== 1) {
            //uneven
            //return FormationResult.fail(KontraptionLang.DESCRIPTION_ION_THRUSTER)
        }
        val centerX = structure!!.minPos.x + (structure!!.length() - 1) / 2
        val centerY = structure!!.minPos.y + (structure!!.height() - 1) / 2
        val centerZ = structure!!.minPos.z + (structure!!.width() - 1) / 2

        val innerRadius = (Math.min(structure.length(), structure.width()) - 3) / 2

        val innerX = (structure.width() - 3)/2
        val innerY = (structure.height() - 3)/2
        val innerZ = (structure.length() - 3)/2

        val exhausts: Set<BlockPos> = ObjectOpenHashSet()


        //Get Direction
        var centerExhaust :TileEntityHydrogenThrusterExhaust? = null
        var direction: Direction? = null

        val mutablePos = MutableBlockPos()
        for (dir in Direction.values()){
            var dir2 = dir.normal.toJOMLD() //.add((structure.width()/2).toDouble(), (structure.height()/2).toDouble(), (structure.length()/2).toDouble())
            //val pos = Vec3((centerX+dir2.x*((structure.width()-1)/2)), (centerY+dir2.y*((structure.height()-1)/2)), (centerZ+dir2.z*((structure.length()-1)/2)))
            mutablePos.set((centerX+dir2.x*((structure.width()-1)/2)), (centerY+dir2.y*((structure.height()-1)/2)), (centerZ+dir2.z*((structure.length()-1)/2)))
            var tile = WorldUtils.getTileEntity(TileEntityHydrogenThrusterExhaust::class.java, world, chunkMap!!, mutablePos)
            if (tile != null){
                centerExhaust = tile
                direction = dir
            }
        }
        if (centerExhaust != null) {
            if (direction == Direction.UP || direction == Direction.DOWN) {
                for (x in centerExhaust.blockPos.getX() - innerX..centerExhaust.blockPos.getX() + innerX) {
                    for (z in centerExhaust.blockPos.getZ() - innerZ..centerExhaust.blockPos.getZ() + innerZ) {
                        if (x != centerX || z != centerZ) {
                            mutablePos.set(x, centerExhaust.blockPos.getY(), z)
                            val tile = WorldUtils.getTileEntity(TileEntityHydrogenThrusterExhaust::class.java, world, chunkMap!!, mutablePos)
                            if (tile == null){
                                return FormationResult.fail(KontraptionLang.DESCRIPTION_ION_THRUSTER, mutablePos);
                            }
                        }
                    }
                }
            }

            if (direction == Direction.EAST || direction == Direction.WEST) {
                for (z in centerExhaust.blockPos.getZ() - innerZ ..centerExhaust.blockPos.getZ() + innerZ) {
                    for (y in centerExhaust.blockPos.getY() - innerY..centerExhaust.blockPos.getY() + innerY) {
                        if (z != centerZ || y != centerY) {
                            mutablePos.set(centerExhaust.blockPos.getX(), y, z)
                            val tile = WorldUtils.getTileEntity(TileEntityHydrogenThrusterExhaust::class.java, world, chunkMap!!, mutablePos)
                            if (tile == null){
                                return FormationResult.fail(KontraptionLang.DESCRIPTION_ION_THRUSTER, mutablePos);
                            }
                        }
                    }
                }
            }

            if (direction == Direction.NORTH || direction == Direction.SOUTH) {
                for (x in centerExhaust.blockPos.getX() - innerX ..centerExhaust.blockPos.getX() + innerX) {
                    for (y in centerExhaust.blockPos.getY() - innerY..centerExhaust.blockPos.getY() + innerY) {
                        if (x != centerX || y != centerY) {
                            mutablePos.set(x, y, centerExhaust.blockPos.z)
                            val tile = WorldUtils.getTileEntity(TileEntityHydrogenThrusterExhaust::class.java, world, chunkMap!!, mutablePos)
                            if (tile == null){
                                return FormationResult.fail(KontraptionLang.DESCRIPTION_ION_THRUSTER, mutablePos);
                            }
                        }
                    }
                }
            }
        } else {
            return FormationResult.fail(KontraptionLang.DESCRIPTION_ION_THRUSTER, mutablePos)
        }





        //:sob:
        //val mutablePos = MutableBlockPos()
        mutablePos.set(centerX, structure.minPos.y, centerZ);
        var tile = WorldUtils.getTileEntity(TileEntityHydrogenThrusterExhaust::class.java, world, chunkMap!!, mutablePos)
        if (tile == null){
            //return FormationResult.fail(KontraptionLang.DESCRIPTION_ION_THRUSTER, mutablePos);

        }
        println("yahhooo")
        return FormationResult.SUCCESS
    }
}