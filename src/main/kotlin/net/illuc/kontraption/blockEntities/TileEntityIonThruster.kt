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
import net.illuc.kontraption.KontraptionBlocks
import net.illuc.kontraption.ThrusterInterface
import net.illuc.kontraption.config.KontraptionConfigs
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import javax.annotation.Nonnull


//class TileEntityIonThruster(pos: BlockPos?, state: BlockState?) : TileEntityMekanism(KontraptionBlocks.ION_THRUSTER, pos, state) {
class TileEntityIonThruster(pos: BlockPos?, state: BlockState?) : TileEntityMekanism(KontraptionBlocks.ION_THRUSTER, pos, state), ThrusterInterface {
    override var enabled = true
    override var thrusterLevel: Level? = null
    override val worldPosition: BlockPos? = pos
    override val forceDirection: Direction = getDirection().opposite
    override var powered: Boolean = true
    override val thrusterPower: Double = KontraptionConfigs.kontraption.ionThrust.get()
    override val basePower: Double = KontraptionConfigs.kontraption.ionThrust.get()



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
            if(powered == true) {
                toUse = energyContainer!!.extract(energyContainer!!.energyPerTick, Action.SIMULATE, AutomationType.INTERNAL)
                if (!toUse.isZero) {
                    energyContainer!!.extract(toUse, Action.EXECUTE, AutomationType.INTERNAL)
                    if (enabled == false) {
                        enable()
                    }
                } else {
                    if (enabled == true) {
                        disable()
                    }

                }
            }
        }
        setActive(!toUse.isZero());
        clientEnergyUsed = toUse
    }
}