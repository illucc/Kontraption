package net.illuc.kontraption

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Blocks
import net.minecraftforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT


object KontraptionItems { //idk check how tournament fixed it
    val ITEMS = DeferredRegister.create(Registry.ITEM_REGISTRY, KontraptionMod.ID)

    lateinit var TAB: CreativeModeTab
    /*val TAB: CreativeModeTab = KontraptionCreativeTab.create(
            ResourceLocation(
                    KontraptionMod.ID,
                    "kontraption_tab"
            )
    ) { ItemStack(Blocks.ACACIA_DOOR) }*/

    fun register() {
        //KontraptionBlocks.registerItems(ITEMS)
        ITEMS.register(MOD_CONTEXT.getKEventBus())
    }
    // I wont be adding a lot of blocks sooooooo this will do for now

    val PILOT_SEAT = ITEMS.register("pilot_seat") { BlockItem(KontraptionBlocks.PILOT_SEAT, Item.Properties().stacksTo(16).tab(TAB)) }

    private infix fun Item.byName(name: String) = ITEMS.register(name) { this }
}