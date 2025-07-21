package net.illuc.kontraption.multiblocks.largeionring.parts

import it.zerono.mods.zerocore.lib.energy.NullEnergyHandlers
import it.zerono.mods.zerocore.lib.energy.adapter.ForgeEnergyAdapter
import it.zerono.mods.zerocore.lib.energy.handler.WideEnergyStorageForwarder
import net.illuc.kontraption.GlobalRegistry
import net.illuc.kontraption.multiblocks.largeionring.LargeIonRingMultiBlock
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.client.model.data.ModelData
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.common.capabilities.CapabilityToken
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.energy.IEnergyStorage
import org.jetbrains.annotations.NotNull

class LargeIonRingPowerPortEntity(
    position: BlockPos,
    blockState: BlockState,
) : AbstractRingEntity(GlobalRegistry.TileEntities.LARGE_ION_THRUSTER_VALVE.get(), position, blockState) {
    private val forwarder: WideEnergyStorageForwarder = WideEnergyStorageForwarder(NullEnergyHandlers.STORAGE)
    private val capability: LazyOptional<IEnergyStorage> = LazyOptional.of { ForgeEnergyAdapter.wrap(forwarder) }

    override fun onPostMachineAssembled(controller: LargeIonRingMultiBlock) {
        super.onPostMachineAssembled(controller)
        this.forwarder.handler = getEnergyStorage()
    }

    override fun onPostMachineBroken() {
        super.onPostMachineBroken()
        this.forwarder.handler = NullEnergyHandlers.STORAGE
    }

    @NotNull
    override fun <T : Any?> getCapability(
        @NotNull cap: Capability<T>,
        side: Direction?,
    ): LazyOptional<T> = if (CAPAP_FORGE_ENERGYSTORAGE === cap) this.capability.cast() else super.getCapability(cap, side)

    override fun getUpdatedModelData(): ModelData = ModelData.EMPTY

    companion object {
        @JvmStatic
        @Suppress("FieldMayBeFinal")
        private var CAPAP_FORGE_ENERGYSTORAGE: Capability<IEnergyStorage> = CapabilityManager.get(object : CapabilityToken<IEnergyStorage>() {})
    }
}
