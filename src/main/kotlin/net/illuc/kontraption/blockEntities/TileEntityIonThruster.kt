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


class TileEntityIonThruster(pos: BlockPos?, state: BlockState?) : TileEntityMekanism(KontraptionBlocks.ION_THRUSTER, pos, state)  {
    var enabled   = false
    var activated = false

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
        setActive(!toUse.isZero());
        clientEnergyUsed = toUse
    }

    private fun chargeHandler(itemHandlerCap: Optional<out IItemHandler>): Boolean {
        println("yuhuh we using it")
        //Ensure that we have an item handler capability, because if for example the player is dead we will not
        if (itemHandlerCap.isPresent()) {
            val itemHandler: IItemHandler = itemHandlerCap.get()
            val slots = itemHandler.slots
            for (slot in 0 until slots) {
                val stack = itemHandler.getStackInSlot(slot)
                if (!stack.isEmpty && provideEnergy(EnergyCompatUtils.getStrictEnergyHandler(stack))) {
                    //Only allow charging one item per player each check
                    return true
                }
            }
        }
        return false
    }


    private fun provideEnergy(@Nullable energyHandler: IStrictEnergyHandler?): Boolean {
        if (energyHandler == null) {
            return false
        }
        val energyToGive = energyContainer!!.energyPerTick
        val simulatedRemainder = energyHandler.insertEnergy(energyToGive, Action.SIMULATE)
        if (simulatedRemainder.smallerThan(energyToGive)) {
            //We are able to fit at least some energy from our container into the item
            val extractedEnergy = energyContainer!!.extract(energyToGive.subtract(simulatedRemainder), Action.EXECUTE, AutomationType.INTERNAL)
            if (!extractedEnergy.isZero) {
                //If we were able to actually extract it from our energy container, then insert it into the item
                MekanismUtils.logExpectedZero(energyHandler.insertEnergy(extractedEnergy, Action.EXECUTE))
                return true
            }
        }
        return false
    }


    fun enable() {
        if (level !is ServerLevel) return
        println("ENABLED")
        enabled = true
        val ship = KontraptionVSUtils.getShipObjectManagingPos((level as ServerLevel), worldPosition)
                ?: KontraptionVSUtils.getShipManagingPos((level as ServerLevel), worldPosition)
                ?: return




        KontraptionShipControl.getOrCreate(ship).let {
            it.stopThruster(worldPosition)
            it.addThruster(
                    worldPosition,
                    this.direction.opposite
                            .normal
                            .toJOMLD(),
                    1.0,
                    this


            )
        }
    }


    fun disable() {
        println("DISABLED")
        if (level !is ServerLevel) return

        enabled = false

        KontraptionShipControl.getOrCreate(
                KontraptionVSUtils.getShipObjectManagingPos((level as ServerLevel), worldPosition)
                        ?: KontraptionVSUtils.getShipManagingPos((level as ServerLevel), worldPosition)
                        ?: return
        ).stopThruster(worldPosition)
    }




}