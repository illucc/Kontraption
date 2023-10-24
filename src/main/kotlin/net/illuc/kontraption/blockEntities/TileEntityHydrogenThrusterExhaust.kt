package net.illuc.kontraption.blockEntities

import mekanism.api.IContentsListener
import mekanism.api.fluid.IExtendedFluidTank
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder
import mekanism.common.tile.base.SubstanceType
import mekanism.common.util.FluidUtils
import net.illuc.kontraption.KontraptionBlocks
import net.illuc.kontraption.multiblocks.largeHydrogenThruster.HydrogenThrusterMultiblockData
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
import javax.annotation.Nonnull


class TileEntityHydrogenThrusterExhaust(pos: BlockPos?, state: BlockState?) : TileEntityHydrogenThrusterCasing(KontraptionBlocks.HYDROGEN_THRUSTER_EXHAUST, pos, state) {
   /* @Nonnull
    protected override fun getInitialFluidTanks(listener: IContentsListener?): IFluidTankHolder {
        return IFluidTankHolder { side: Direction? ->
            val multiblock: HydrogenThrusterMultiblockData? = getMultiblock()
            if (multiblock?.isFormed()) multiblock.ventTanks else emptyList<IExtendedFluidTank>()
        }
    }*/

    protected override fun onUpdateServer(multiblock: HydrogenThrusterMultiblockData?): Boolean {
        val needsPacket: Boolean = super.onUpdateServer(multiblock)
        if (multiblock != null) {
            if (multiblock.isFormed()) {
                //FluidUtils.emit(multiblock.getDirectionsToEmit(getBlockPos()), multiblock.ventTank, this)
            }
        }
        return needsPacket
    }

    override fun persists(type: SubstanceType): Boolean {
        //Do not handle fluid when it comes to syncing it/saving this tile to disk
        return if (type == SubstanceType.FLUID) {
            false
        } else super.persists(type)
    }
}