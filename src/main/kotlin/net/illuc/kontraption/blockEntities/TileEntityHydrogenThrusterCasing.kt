package net.illuc.kontraption.blockEntities

import mekanism.api.providers.IBlockProvider
import mekanism.common.lib.multiblock.MultiblockManager
import mekanism.common.tile.interfaces.IHasGasMode
import mekanism.common.tile.prefab.TileEntityMultiblock
import net.illuc.kontraption.Kontraption
import net.illuc.kontraption.KontraptionBlocks
import net.illuc.kontraption.multiblocks.largeHydrogenThruster.HydrogenThrusterMultiblockData
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState


open class TileEntityHydrogenThrusterCasing(blockProvider: IBlockProvider?, pos: BlockPos?, state: BlockState?) : TileEntityMultiblock<HydrogenThrusterMultiblockData?>(blockProvider, pos, state), IHasGasMode{
    constructor(pos: BlockPos?, state: BlockState?) : this(KontraptionBlocks.HYDROGEN_THRUSTER_CASING, pos, state)

    lateinit var prevMultiblock: HydrogenThrusterMultiblockData


    override fun createMultiblock(): HydrogenThrusterMultiblockData {
        return HydrogenThrusterMultiblockData(this)
    }

    override fun getManager(): MultiblockManager<HydrogenThrusterMultiblockData?> {
        return Kontraption.hydrogenThrusterManager
    }

    override fun blockRemoved() {
        super.blockRemoved()
    }

    override fun getMultiblock(): HydrogenThrusterMultiblockData? {
        return super.getMultiblock()
    }

    override fun onUpdateServer() {

        super.onUpdateServer()
    }

    //actually fuck it let the baller remove the thruster 913482 times
    override fun structureChanged(multiblock: HydrogenThrusterMultiblockData?) {

        if (multiblock!!.isFormed == false) {

            println("baler")
            prevMultiblock.disable()
        }
        prevMultiblock = multiblock
        super.structureChanged(multiblock)
    }

    override fun nextMode(tank: Int) {
        if (tank === 0) {
            val multiblock: HydrogenThrusterMultiblockData? = multiblock
            //multiblock.setDumpMode(multiblock.dumpMode.getNext())
        }

    }
}