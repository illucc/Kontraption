package net.illuc.kontraption.container

import mekanism.api.AutomationType
import mekanism.api.IContentsListener
import mekanism.api.NBTConstants
import mekanism.api.math.FloatingLong
import mekanism.common.capabilities.energy.MachineEnergyContainer
import mekanism.common.util.NBTUtils
import net.illuc.kontraption.blockEntities.TileEntityIonThruster
import net.minecraft.nbt.CompoundTag
import java.util.function.Predicate


class IonThrusterEnergyContainer private constructor(maxEnergy: FloatingLong, energyPerTick: FloatingLong, canExtract: Predicate<AutomationType>,
                                                     canInsert: Predicate<AutomationType>, tile: TileEntityIonThruster, listener: IContentsListener?) : MachineEnergyContainer<TileEntityIonThruster?>(maxEnergy, energyPerTick, canExtract, canInsert, tile, listener) {
    override fun adjustableRates(): Boolean {
        return true
    }

    fun updateEnergyUsage(energyUsage: FloatingLong) {
        currentEnergyPerTick = energyUsage
        maxEnergy = energyUsage.multiply(400)
    }

    override fun serializeNBT(): CompoundTag {
        val nbt = super.serializeNBT()
        nbt.putString(NBTConstants.ENERGY_USAGE, energyPerTick.toString())
        return nbt
    }

    override fun deserializeNBT(nbt: CompoundTag) {
        super.deserializeNBT(nbt)
        NBTUtils.setFloatingLongIfPresent(nbt, NBTConstants.ENERGY_USAGE) { energyUsage: FloatingLong -> updateEnergyUsage(energyUsage) }
    }

    companion object {
        fun input(tile: TileEntityIonThruster, listener: IContentsListener?): IonThrusterEnergyContainer {
            val electricBlock = validateBlock(tile)
            return IonThrusterEnergyContainer(electricBlock.storage, electricBlock.usage, notExternal, alwaysTrue, tile, listener)
        }
    }
}