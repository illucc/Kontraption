package net.illuc.kontraption.blockEntities.railgun

import mekanism.api.providers.IBlockProvider
import mekanism.common.lib.multiblock.MultiblockManager
import mekanism.common.tile.interfaces.IHasGasMode
import mekanism.common.tile.prefab.TileEntityMultiblock
import net.illuc.kontraption.Kontraption
import net.illuc.kontraption.KontraptionBlocks
import net.illuc.kontraption.multiblocks.railgun.RailgunMultiblockData
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

open class TileEntityRailgunCasing(
    blockProvider: IBlockProvider?,
    pos: BlockPos?,
    state: BlockState?,
) : TileEntityMultiblock<RailgunMultiblockData?>(
        blockProvider,
        pos,
        state,
    ),
    IHasGasMode {
    constructor(pos: BlockPos?, state: BlockState?) : this(KontraptionBlocks.RAILGUN_CASING, pos, state)

    lateinit var prevMultiblock: RailgunMultiblockData

    override fun createMultiblock(): RailgunMultiblockData = RailgunMultiblockData(this)

    override fun getManager(): MultiblockManager<RailgunMultiblockData?> = Kontraption.railgunManager

    override fun blockRemoved() {
        super.blockRemoved()
    }

    override fun getMultiblock(): RailgunMultiblockData? = super.getMultiblock()

    override fun onUpdateServer() {
        super.onUpdateServer()
    }

    override fun structureChanged(multiblock: RailgunMultiblockData?) {
        super.structureChanged(multiblock)
    }

    override fun nextMode(tank: Int) {
        if (tank === 0) {
            val multiblock: RailgunMultiblockData? = multiblock
            // multiblock.setDumpMode(multiblock.dumpMode.getNext())
        }
    }
}
