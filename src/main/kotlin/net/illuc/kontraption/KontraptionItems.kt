package net.illuc.kontraption

import mekanism.common.registration.impl.ItemDeferredRegister
import mekanism.common.registration.impl.ItemRegistryObject
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity


object KontraptionItems { //idk check how tournament fixed it
    private fun KontraptionItems() {}

    val ITEMS = ItemDeferredRegister(Kontraption.MODID)

    val LIGHTWEIGHT_ALLOY: ItemRegistryObject<Item> = ITEMS.register("alloy_lightweight", Rarity.RARE)
    val ION_ENGINE: ItemRegistryObject<Item> = ITEMS.register("ion_engine", Rarity.RARE)

}