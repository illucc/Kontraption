package net.illuc.kontraption.blockEntities

import mekanism.common.Mekanism
import mekanism.common.tile.base.TileEntityMekanism
import net.illuc.kontraption.KontraptionBlocks
import net.illuc.kontraption.blocks.BlockKey
import net.illuc.kontraption.events.KeyBindEvent
import net.illuc.kontraption.ship.KontraptionBConfigControl
import net.illuc.kontraption.ship.KontraptionKeyBlockControl
import net.illuc.kontraption.util.KontraptionVSUtils
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.state.BlockState
import java.lang.Math.random

class TileEntityKey(
    pos: BlockPos?,
    state: BlockState?,
) : TileEntityMekanism(KontraptionBlocks.KEY, pos, state) {
    private var isEnabled = false
    var keybindz: Int = 0

    fun tick() {
    }

    override fun load(nbt: CompoundTag) {
        super.load(nbt)
        keybindz = nbt.getInt("keybind")
    }

    override fun saveAdditional(nbtTags: CompoundTag) {
        super.saveAdditional(nbtTags)
        nbtTags.putInt("keybind", keybindz)
    }

    override fun getUpdateTag(): CompoundTag {
        val tag = super.getUpdateTag()
        saveAdditional(tag)
        return tag
    }

    fun enable() { // Totally not stolen from gyro xd
        if (level !is ServerLevel) return
        val slevel = level as ServerLevel
        val settings =
            listOf(
                KontraptionBConfigControl.BlockSetting.BooleanSetting("enabled", true),
                KontraptionBConfigControl.BlockSetting.IntSetting("range", (0 until 10).random()),
                KontraptionBConfigControl.BlockSetting.StringSetting("name", "CoolBlock"),
            )

        val ship =
            KontraptionVSUtils.getShipObjectManagingPos((level as ServerLevel), worldPosition)
                ?: KontraptionVSUtils.getShipManagingPos((level as ServerLevel), worldPosition)
                ?: return

        KontraptionKeyBlockControl.getOrCreate(ship).let {
            it.removeAll(worldPosition)
            it.addKeys(
                worldPosition,
                0,
                this,
                // keybind is unsused lol
            )
        }
        KontraptionBConfigControl.getOrCreate(ship).let {
            it.removeConfigBlock(worldPosition)

            it.addConfigBlock(worldPosition, slevel.getBlockEntity(worldPosition), settings)
        }
    }

    fun isRedstoneActive(): Boolean = isEnabled

    fun setRedstone(
        state: Boolean,
    ) {
        if (level == null) {
            Mekanism.logger.info("somehow level is null xd")
            return
        }
        val blockstate = level?.getBlockState(this.blockPos)
        if (blockstate?.block is BlockKey) {
            (blockstate.block as BlockKey).setRedstone(state, level!!, this.blockPos)
        }
    }

    fun getsetKeybind(
        op: Boolean,
        kb: Int = 0,
    ): Int {
        if (op == true) {
            keybindz = kb
            setChanged()
            return 0
        } else {
            return keybindz
        }
    }

    fun fire(event: KeyBindEvent) {
        val eventkey = event.keybindIndex
        if (eventkey == this.keybindz) {
            setRedstone(event.updown)
        }
    }

    fun disable() {
        if (level !is ServerLevel) return
        KontraptionKeyBlockControl
            .getOrCreate(
                KontraptionVSUtils.getShipObjectManagingPos((level as ServerLevel), worldPosition)
                    ?: KontraptionVSUtils.getShipManagingPos((level as ServerLevel), worldPosition)
                    ?: return,
            ).removeAll(worldPosition)
    }
}
