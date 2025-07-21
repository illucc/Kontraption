package net.illuc.kontraption.gui

import net.illuc.kontraption.Kontraption
import net.illuc.kontraption.ship.KontraptionBConfigControl
import net.illuc.kontraption.util.readConfigBlocks
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack

class ShipTerminalMenu(
    id: Int,
    playerInventory: Inventory,
    buf: FriendlyByteBuf?,
) : AbstractContainerMenu(Kontraption.TERMINALMENU.get(), id) {
    override fun quickMoveStack(
        p0: Player,
        p1: Int,
    ): ItemStack = ItemStack.EMPTY

    override fun stillValid(p0: Player): Boolean = true

    val configBlocks: List<KontraptionBConfigControl.ConfigBlock> = buf?.readConfigBlocks() ?: listOf()

    companion object {
        fun create(
            id: Int,
            inv: Inventory,
            buf: FriendlyByteBuf,
        ): ShipTerminalMenu = ShipTerminalMenu(id, inv, buf)
    }
}
