package net.illuc.kontraption.blockEntities.largehydrogen

import mekanism.common.tile.base.SubstanceType
import net.illuc.kontraption.KontraptionBlocks
import net.illuc.kontraption.multiblocks.largeHydrogenThruster.LiquidFuelThrusterMultiblockData
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

class TileEntityLiquidFuelThrusterExhaust(
    pos: BlockPos?,
    state: BlockState?,
) : TileEntityLiquidFuelThrusterCasing(
        KontraptionBlocks.LIQUID_FUEL_THRUSTER_EXHAUST,
        pos,
        state,
    ) {
   /* @Nonnull
    protected override fun getInitialFluidTanks(listener: IContentsListener?): IFluidTankHolder {
        return IFluidTankHolder { side: Direction? ->
            val multiblock: HydrogenThrusterMultiblockData? = getMultiblock()
            if (multiblock?.isFormed()) multiblock.ventTanks else emptyList<IExtendedFluidTank>()
        }
    }*/

    protected override fun onUpdateServer(multiblock: LiquidFuelThrusterMultiblockData?): Boolean {
        val needsPacket: Boolean = super.onUpdateServer(multiblock)
        if (multiblock != null) {
            if (multiblock.isFormed()) {
                // FluidUtils.emit(multiblock.getDirectionsToEmit(getBlockPos()), multiblock.ventTank, this)
            }
        }
        return needsPacket
    }

    override fun persists(type: SubstanceType): Boolean {
        // Do not handle fluid when it comes to syncing it/saving this tile to disk
        return if (type == SubstanceType.GAS) {
            false
        } else {
            super.persists(type)
        }
    }
}
