package net.illuc.kontraption.blockEntities

import mekanism.api.IContentsListener
import mekanism.api.providers.IBlockProvider
import mekanism.common.block.attribute.AttributeSound
import mekanism.common.block.interfaces.IHasTileEntity
import mekanism.common.capabilities.heat.CachedAmbientTemperature
import mekanism.common.capabilities.resolver.BasicCapabilityResolver
import mekanism.common.capabilities.resolver.ICapabilityResolver
import mekanism.common.capabilities.resolver.manager.EnergyHandlerManager
import mekanism.common.capabilities.resolver.manager.FluidHandlerManager
import mekanism.common.capabilities.resolver.manager.HeatHandlerManager
import mekanism.common.capabilities.resolver.manager.ItemHandlerManager
import mekanism.common.integration.computer.ComputerCapabilityHelper
import mekanism.common.lib.frequency.TileComponentFrequency
import mekanism.common.tile.base.SubstanceType
import mekanism.common.tile.base.TileEntityMekanism
import mekanism.common.tile.component.TileComponentSecurity
import mekanism.common.tile.component.TileComponentUpgrade
import net.illuc.kontraption.ship.KontraptionShipControl
import net.illuc.kontraption.util.KontraptionVSUtils
import net.illuc.kontraption.util.toJOMLD
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import thedarkcolour.kotlinforforge.forge.vectorutil.toVec3


public open class TileEntityThruster(blockProvider: IBlockProvider, pos: BlockPos?, state: BlockState?): TileEntityMekanism(blockProvider, pos, state) {
    var enabled   = false
    var powered = false
    val thrusterPower: Double = 1.0

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
                        thrusterPower,
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