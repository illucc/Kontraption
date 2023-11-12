package net.illuc.kontraption.blockEntities

import mekanism.api.IContentsListener
import mekanism.api.chemical.gas.Gas
import mekanism.api.chemical.gas.GasStack
import mekanism.api.chemical.gas.IGasTank
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder
import mekanism.common.tile.base.SubstanceType
import net.illuc.kontraption.KontraptionBlocks
import net.illuc.kontraption.multiblocks.largeHydrogenThruster.LiquidFuelThrusterMultiblockData
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
import javax.annotation.Nonnull


class TileEntityLiquidFuelThrusterValve(pos: BlockPos?, state: BlockState?) : TileEntityLiquidFuelThrusterCasing(KontraptionBlocks.LIQUID_FUEL_THRUSTER_VALVE, pos, state) {
    @Nonnull
    override fun getInitialGasTanks(listener: IContentsListener?): IChemicalTankHolder<Gas, GasStack, IGasTank> {
        return IChemicalTankHolder<Gas, GasStack, IGasTank> { side: Direction? ->
            getMultiblock()!!.getGasTanks(side)
        }
    }

    /*@Nonnull
    protected override fun getInitialEnergyContainers(listener: IContentsListener?): IEnergyContainerHolder {
        return IEnergyContainerHolder { side: Direction? -> getMultiblock()!!.getEnergyContainers(side) }
    }
*/
    override fun onUpdateServer(multiblock: LiquidFuelThrusterMultiblockData?): Boolean {
        val needsPacket: Boolean = super.onUpdateServer(multiblock)
        if (multiblock != null) {
            if (multiblock.isFormed()) {
                //CableUtils.emit(multiblock.getDirectionsToEmit(getBlockPos()), multiblock.energyContainer, this)
            }
        }
        return needsPacket
    }

    override fun persists(type: SubstanceType): Boolean {
        //Do not handle gas when it comes to syncing it/saving this tile to disk
        return if (type == SubstanceType.GAS) {
            false
        } else super.persists(type)
    }

    /*override val redstoneLevel: Int
        get() = getMultiblock()!!.getCurrentRedstoneLevel()*/
}