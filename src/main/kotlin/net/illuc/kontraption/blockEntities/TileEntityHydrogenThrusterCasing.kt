package net.illuc.kontraption.blockEntities

import mekanism.api.providers.IBlockProvider
import mekanism.common.lib.multiblock.MultiblockManager
import mekanism.common.tile.interfaces.IHasGasMode
import mekanism.common.tile.prefab.TileEntityMultiblock
import net.illuc.kontraption.Kontraption
import net.illuc.kontraption.KontraptionBlocks
import net.illuc.kontraption.multiblocks.largeHydrogenThruster.HydrogenThrusterMultiblockData
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState


open class TileEntityHydrogenThrusterCasing(blockProvider: IBlockProvider?, pos: BlockPos?, state: BlockState?) : TileEntityMultiblock<HydrogenThrusterMultiblockData?>(blockProvider, pos, state), IHasGasMode{
    constructor(pos: BlockPos?, state: BlockState?) : this(KontraptionBlocks.HYDROGEN_THRUSTER_CASING, pos, state)


    override fun createMultiblock(): HydrogenThrusterMultiblockData {
        return HydrogenThrusterMultiblockData(this)
    }

    override fun getManager(): MultiblockManager<HydrogenThrusterMultiblockData?> {
        return Kontraption.hydrogenThrusterManager
    }

    override fun nextMode(tank: Int) {
        if (tank === 0) {
            val multiblock: HydrogenThrusterMultiblockData? = multiblock
            //multiblock.setDumpMode(multiblock.dumpMode.getNext())
        }

    }
}