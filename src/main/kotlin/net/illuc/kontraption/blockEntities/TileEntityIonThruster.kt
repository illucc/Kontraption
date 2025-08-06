package net.illuc.kontraption.blockEntities

import mekanism.api.Action
import mekanism.api.AutomationType
import mekanism.api.IContentsListener
import mekanism.api.RelativeSide
import mekanism.api.math.FloatingLong
import mekanism.common.capabilities.energy.MachineEnergyContainer
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder
import mekanism.common.tile.base.TileEntityMekanism
import mekanism.common.util.MekanismUtils
import net.illuc.kontraption.Kontraption
import net.illuc.kontraption.KontraptionBlocks
import net.illuc.kontraption.ThrusterInterface
import net.illuc.kontraption.config.KontraptionConfigs
import net.illuc.kontraption.ship.KontraptionBConfigControl
import net.illuc.kontraption.util.KontraptionVSUtils
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import javax.annotation.Nonnull

class TileEntityIonThruster(
    pos: BlockPos?,
    state: BlockState?,
) : TileEntityMekanism(KontraptionBlocks.ION_THRUSTER, pos, state),
    ThrusterInterface {
    // TODO: TOUCH THIS MF
    override var enabled = false
    override var thrusterLevel: Level? = null
    override val worldPosition: BlockPos? = pos
    override val forceDirection: Direction = getDirection().opposite
    override var powered: Boolean = true
    override val thrusterPower: Double = KontraptionConfigs.kontraption.ionThrust.get()
    override val basePower: Double = KontraptionConfigs.kontraption.ionThrust.get()
    override var currentThrust: Double = 0.0

    private var clientEnergyUsed = FloatingLong.ZERO

    private var energyContainer: MachineEnergyContainer<TileEntityIonThruster>? = null

    @Nonnull
    override fun getInitialEnergyContainers(listener: IContentsListener?): IEnergyContainerHolder? {
        val builder = EnergyContainerHelper.forSide { this.direction }
        builder.addContainer(MachineEnergyContainer.input(this, listener).also { energyContainer = it }, RelativeSide.BACK)
        return builder.build()
    }

    override fun onUpdateServer() {
        super.onUpdateServer()
        thrusterLevel = level as ServerLevel
        var toUse = FloatingLong.ZERO
        if (MekanismUtils.canFunction(this)) {
            if (powered) { // WHY ILLUC YA USE IF BOOLEAN IS TRUE WTFF
                if (currentThrust != 0.0) {
                    val thrustPercent = currentThrust / thrusterPower
                    val pwrUsage = energyContainer!!.energyPerTick.multiply(thrustPercent)
                    toUse = energyContainer!!.extract(pwrUsage, Action.SIMULATE, AutomationType.INTERNAL) // Uh so we always are sure that we have poweer
                    if (!toUse.isZero) {
                        energyContainer!!.extract(toUse, Action.EXECUTE, AutomationType.INTERNAL)
                        enabled = true
                    } else {
                        enabled = false
                    }
                } else {
                    val maybeUsage = energyContainer!!.energyPerTick
                    toUse = energyContainer!!.extract(maybeUsage, Action.SIMULATE, AutomationType.INTERNAL)
                    enabled = !toUse.isZero
                }
            }
        }
        active = !toUse.isZero
        clientEnergyUsed = toUse
    }

    fun shipAdd() {
        if (level !is ServerLevel) return
        if (worldPosition == null) return
        val slevel = level as ServerLevel
        val settings =
            listOf(
                KontraptionBConfigControl.BlockSetting.BooleanSetting("enabled", true),
                KontraptionBConfigControl.BlockSetting.IntSetting("range", (0 until 10).random()),
                KontraptionBConfigControl.BlockSetting.StringSetting("name", "IonThruster"),
            )

        val ship =
            KontraptionVSUtils.getShipObjectManagingPos((level as ServerLevel), worldPosition)
                ?: KontraptionVSUtils.getShipManagingPos((level as ServerLevel), worldPosition)
                ?: return

        KontraptionBConfigControl.getOrCreate(ship).let {
            it.removeConfigBlock(worldPosition)

            it.addConfigBlock(worldPosition, slevel.getBlockEntity(worldPosition), settings)
            Kontraption.LOGGER.debug("Added to config list, may Asmodeus have mercy over us")
        }
        // This has to somehow be called on ship creation . . . ON LOAD XD
    }

    override fun onLoad() {
        if (level is ServerLevel) {
            enable(level as ServerLevel, blockPos)
            shipAdd()
        }
        super.onLoad()
    }
}
