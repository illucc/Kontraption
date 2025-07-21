package net.illuc.kontraption.blockEntities.largehydrogen

import mekanism.api.IContentsListener
import mekanism.api.chemical.gas.Gas
import mekanism.api.chemical.gas.GasStack
import mekanism.api.chemical.gas.IGasTank
import mekanism.common.capabilities.holder.chemical.IChemicalTankHolder
import mekanism.common.tile.base.SubstanceType
import net.illuc.kontraption.KontraptionBlocks
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
import javax.annotation.Nonnull

class TileEntityLiquidFuelThrusterValve(
    pos: BlockPos?,
    state: BlockState?,
) : TileEntityLiquidFuelThrusterCasing(
        KontraptionBlocks.LIQUID_FUEL_THRUSTER_VALVE,
        pos,
        state,
    ) {
    @Nonnull
    override fun getInitialGasTanks(listener: IContentsListener?): IChemicalTankHolder<Gas, GasStack, IGasTank> = IChemicalTankHolder { side: Direction? -> multiblock!!.getGasTanks(side) }

    override fun persists(type: SubstanceType): Boolean {
        // Do not handle fluid when it comes to syncing it/saving this tile to disk
        return if (type == SubstanceType.FLUID || type == SubstanceType.GAS || type == SubstanceType.INFUSION || type == SubstanceType.PIGMENT || type == SubstanceType.SLURRY) {
            false
        } else {
            super.persists(type)
        }
    }

    override fun getRedstoneLevel(): Int = multiblock!!.currentRedstoneLevel
}
