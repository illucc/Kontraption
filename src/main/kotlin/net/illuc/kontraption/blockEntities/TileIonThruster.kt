package net.illuc.kontraption.blockEntities

import mekanism.api.Action
import mekanism.api.AutomationType
import mekanism.api.IContentsListener
import mekanism.api.RelativeSide
import mekanism.api.energy.IStrictEnergyHandler
import mekanism.common.capabilities.energy.MachineEnergyContainer
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder
import mekanism.common.integration.energy.EnergyCompatUtils
import mekanism.common.tile.base.TileEntityMekanism
import mekanism.common.util.MekanismUtils
import net.illuc.kontraption.KontraptionBlocks
import net.illuc.kontraption.ship.KontraptionThrusterShipControl
import net.illuc.kontraption.util.KontraptionVSUtils
import net.illuc.kontraption.util.toJOMLD
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.items.IItemHandler
import org.jetbrains.annotations.Nullable
import java.util.*
import javax.annotation.Nonnull



class TileIonThruster(pos: BlockPos?, state: BlockState?) : TileEntityMekanism(KontraptionBlocks.ION_THRUSTER, pos, state)  {
    var enabled = false

    private var energyContainer: MachineEnergyContainer<TileIonThruster>? = null

    @Nonnull
    override fun getInitialEnergyContainers(listener: IContentsListener?): IEnergyContainerHolder? {
        val builder = EnergyContainerHelper.forSide { this.direction }
        builder.addContainer(MachineEnergyContainer.input(this, listener).also { energyContainer = it }, RelativeSide.BACK, RelativeSide.BOTTOM)
        return builder.build()

    }






    override fun onUpdateServer() {
        super.onUpdateServer()
        if (redstone == true and !enabled) {
            enable()
        }
        var active = false

    }

    private fun chargeHandler(itemHandlerCap: Optional<out IItemHandler>): Boolean {
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
        println("enabled: $redstone")

        enabled = true

        val ship = KontraptionVSUtils.getShipObjectManagingPos((level as ServerLevel), worldPosition)
                ?: KontraptionVSUtils.getShipManagingPos((level as ServerLevel), worldPosition)
                ?: return




        KontraptionThrusterShipControl.getOrCreate(ship).let {
            it.stopThruster(worldPosition)
            it.addThruster(
                    worldPosition,
                    1.0,
                    this.direction
                            .normal
                            .toJOMLD()


            )
        }
    }



}