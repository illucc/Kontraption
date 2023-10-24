package net.illuc.kontraption.blockEntities

import mekanism.api.Action
import mekanism.api.AutomationType
import mekanism.api.IContentsListener
import mekanism.api.RelativeSide
import mekanism.api.energy.IStrictEnergyHandler
import mekanism.api.math.FloatingLong
import mekanism.common.capabilities.energy.MachineEnergyContainer
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder
import mekanism.common.integration.energy.EnergyCompatUtils
import mekanism.common.tile.base.TileEntityMekanism
import mekanism.common.util.MekanismUtils
import net.illuc.kontraption.KontraptionBlocks
import net.illuc.kontraption.ship.KontraptionShipControl
import net.illuc.kontraption.util.KontraptionVSUtils
import net.illuc.kontraption.util.toJOMLD
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.items.IItemHandler
import org.jetbrains.annotations.Nullable
import java.util.*
import javax.annotation.Nonnull


//class TileEntityIonThruster(pos: BlockPos?, state: BlockState?) : TileEntityMekanism(KontraptionBlocks.ION_THRUSTER, pos, state) {
class TileEntityIonThruster(pos: BlockPos?, state: BlockState?) : TileEntityThruster(KontraptionBlocks.ION_THRUSTER, pos, state) {
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