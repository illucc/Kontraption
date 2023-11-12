package net.illuc.kontraption.blockEntities

import mekanism.api.providers.IBlockProvider
import mekanism.common.lib.multiblock.MultiblockManager
import mekanism.common.tile.interfaces.IHasGasMode
import mekanism.common.tile.prefab.TileEntityMultiblock
import net.illuc.kontraption.Kontraption
import net.illuc.kontraption.KontraptionBlocks
import net.illuc.kontraption.multiblocks.largeHydrogenThruster.LiquidFuelThrusterMultiblockData
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState


open class TileEntityLiquidFuelThrusterCasing(blockProvider: IBlockProvider?, pos: BlockPos?, state: BlockState?) : TileEntityMultiblock<LiquidFuelThrusterMultiblockData?>(blockProvider, pos, state), IHasGasMode{
    constructor(pos: BlockPos?, state: BlockState?) : this(KontraptionBlocks.LIQUID_FUEL_THRUSTER_CASING, pos, state)

    lateinit var prevMultiblock: LiquidFuelThrusterMultiblockData


    override fun createMultiblock(): LiquidFuelThrusterMultiblockData {
        return LiquidFuelThrusterMultiblockData(this)
    }

    override fun getManager(): MultiblockManager<LiquidFuelThrusterMultiblockData?> {
        return Kontraption.hydrogenThrusterManager
    }

    override fun blockRemoved() {
        super.blockRemoved()
    }

    override fun getMultiblock(): LiquidFuelThrusterMultiblockData? {
        return super.getMultiblock()
    }

    override fun onUpdateServer() {

        super.onUpdateServer()
    }

    //actually fuck it let the baller remove the thruster 913482 times
    override fun structureChanged(multiblock: LiquidFuelThrusterMultiblockData?) {

        if (multiblock!!.isFormed == false) {

            println("baler")
            prevMultiblock.disable()
        }
        prevMultiblock = multiblock
        super.structureChanged(multiblock)
    }

    override fun nextMode(tank: Int) {
        if (tank === 0) {
            val multiblock: LiquidFuelThrusterMultiblockData? = multiblock
            //multiblock.setDumpMode(multiblock.dumpMode.getNext())
        }

    }
}