package net.illuc.kontraption.blockEntities

import javax.annotation.Nonnull;
import mekanism.api.*
import mekanism.api.math.FloatingLong
import mekanism.common.capabilities.Capabilities
import mekanism.common.capabilities.energy.MachineEnergyContainer
import mekanism.common.capabilities.energy.ResistiveHeaterEnergyContainer
import mekanism.common.capabilities.heat.BasicHeatCapacitor
import mekanism.common.capabilities.heat.CachedAmbientTemperature
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder
import mekanism.common.capabilities.holder.heat.HeatCapacitorHelper
import mekanism.common.capabilities.holder.heat.IHeatCapacitorHolder
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder
import mekanism.common.capabilities.holder.slot.InventorySlotHelper
import mekanism.common.capabilities.resolver.BasicCapabilityResolver
import mekanism.common.config.MekanismConfig
import mekanism.common.integration.computer.ComputerException
import mekanism.common.integration.computer.SpecialComputerMethodWrapper
import mekanism.common.integration.computer.annotation.ComputerMethod
import mekanism.common.integration.computer.annotation.WrappingComputerMethod
import mekanism.common.inventory.container.MekanismContainer
import mekanism.common.inventory.container.sync.SyncableDouble
import mekanism.common.inventory.slot.EnergyInventorySlot
import mekanism.common.registries.MekanismBlocks
import mekanism.common.tile.base.TileEntityMekanism
import mekanism.common.util.MekanismUtils
import mekanism.common.util.NBTUtils
import net.illuc.kontraption.container.IonThrusterEnergyContainer
import net.illuc.kontraption.network.to_server.KontraptionBlocks
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.state.BlockState

class TileIonThruster(pos: BlockPos?, state: BlockState?) : TileEntityMekanism(KontraptionBlocks.ION_THRUSTER, pos, state) {
    private var soundScale = 1f

    @get:ComputerMethod(nameOverride = "getEnvironmentalLoss")
    var lastEnvironmentLoss = 0.0
        private set
    private var energyContainer: IonThrusterEnergyContainer? = null

    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerHeatCapacitorWrapper::class, methodNames = ["getTemperature"])
    private var heatCapacitor: BasicHeatCapacitor? = null

    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper::class, methodNames = ["getEnergyItem"])
    private var energySlot: EnergyInventorySlot? = null

    init {
        addCapabilityResolver(BasicCapabilityResolver.constant(Capabilities.CONFIG_CARD_CAPABILITY, this))
    }

    @Nonnull
    override fun getInitialEnergyContainers(listener: IContentsListener): IEnergyContainerHolder? {
        val builder = EnergyContainerHelper.forSide { this.direction }
        builder.addContainer(IonThrusterEnergyContainer.input(this, listener).also { energyContainer = it }, RelativeSide.LEFT, RelativeSide.RIGHT)
        return builder.build()
    }

    @Nonnull
    override fun getInitialHeatCapacitors(listener: IContentsListener, ambientTemperature: CachedAmbientTemperature): IHeatCapacitorHolder? {
        val builder = HeatCapacitorHelper.forSide { this.direction }
        builder.addCapacitor(BasicHeatCapacitor.create(100.0, 5.0, 100.0, ambientTemperature, listener).also { heatCapacitor = it })
        return builder.build()
    }

    @Nonnull
    override fun getInitialInventory(listener: IContentsListener): IInventorySlotHolder? {
        val builder = InventorySlotHelper.forSide { this.direction }
        energyContainer?.let { EnergyInventorySlot.fillOrConvert(it, { getLevel() }, listener, 15, 35).also { energySlot = it } }?.let { builder.addSlot(it) }
        return builder.build()
    }

    override fun onUpdateServer() {
        super.onUpdateServer()
        energySlot!!.fillContainerOrConvert()
        var toUse = FloatingLong.ZERO
        if (MekanismUtils.canFunction(this)) {
            toUse = energyContainer!!.extract(energyContainer!!.energyPerTick, Action.SIMULATE, AutomationType.INTERNAL)
            if (!toUse.isZero) {
                heatCapacitor!!.handleHeat(toUse.multiply(MekanismConfig.general.resistiveHeaterEfficiency.get()).toDouble())
                energyContainer!!.extract(toUse, Action.EXECUTE, AutomationType.INTERNAL)
            }
        }
        active = !toUse.isZero
        val transfer = simulate()
        lastEnvironmentLoss = transfer.environmentTransfer()
        val newSoundScale = toUse.divide(100000).toFloat()
        if (Math.abs(newSoundScale - soundScale) > 0.01) {
            soundScale = newSoundScale
            sendUpdatePacket()
        }
    }

    fun setEnergyUsageFromPacket(floatingLong: FloatingLong?) {
        if (floatingLong != null) {
            energyContainer!!.updateEnergyUsage(floatingLong)
        }
        markForSave()
    }

    override fun getVolume(): Float {
        return Math.sqrt(soundScale.toDouble()).toFloat()
    }

    fun getEnergyContainer(): IonThrusterEnergyContainer? {
        return energyContainer
    }

    override fun getConfigurationData(player: Player): CompoundTag {
        val data = super.getConfigurationData(player)
        data.putString(NBTConstants.ENERGY_USAGE, energyContainer!!.energyPerTick.toString())
        return data
    }

    override fun setConfigurationData(player: Player, data: CompoundTag) {
        super.setConfigurationData(player, data)
        NBTUtils.setFloatingLongIfPresent(data, NBTConstants.ENERGY_USAGE) { energyUsage: FloatingLong? ->
            if (energyUsage != null) {
                energyContainer!!.updateEnergyUsage(energyUsage)
            }
        }
    }

    override fun addContainerTrackers(container: MekanismContainer) {
        super.addContainerTrackers(container)
        container.track(SyncableDouble.create({ lastEnvironmentLoss }) { value: Double -> lastEnvironmentLoss = value })
    }

    override fun getName(): Component {
        TODO("Not yet implemented")
    }

    @Nonnull
    override fun getReducedUpdateTag(): CompoundTag {
        val updateTag = super.getReducedUpdateTag()
        updateTag.putFloat(NBTConstants.SOUND_SCALE, soundScale)
        return updateTag
    }

    override fun handleUpdateTag(@Nonnull tag: CompoundTag) {
        super.handleUpdateTag(tag)
        NBTUtils.setFloatIfPresent(tag, NBTConstants.SOUND_SCALE) { value: Float -> soundScale = value }
    }

    @get:ComputerMethod
    @set:Throws(ComputerException::class)
    @set:ComputerMethod
    private var energyUsage: FloatingLong
        //Methods relating to IComputerTile
        private get() = energyContainer!!.energyPerTick
        private set(usage) {
            validateSecurityIsPublic()
            setEnergyUsageFromPacket(usage)
        } //End methods IComputerTile
}