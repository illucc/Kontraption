package net.illuc.kontraption

import mekanism.common.registration.impl.ItemDeferredRegister
import mekanism.common.registration.impl.ItemRegistryObject
import net.illuc.kontraption.item.ItemToolgun
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity


object KontraptionItems { //idk check how tournament fixed it
    private fun KontraptionItems() {}

    val ITEMS = ItemDeferredRegister(Kontraption.MODID)

    val LIGHTWEIGHT_ALLOY: ItemRegistryObject<Item> = ITEMS.register("alloy_lightweight", Rarity.RARE)
    val TOOLGUN: ItemRegistryObject<ItemToolgun> = ITEMS.register("toolgun")  { properties: Item.Properties -> ItemToolgun(properties) }

}