package net.illuc.kontraption

import net.illuc.kontraption.blocks.pilotSeatBlock
import net.minecraft.core.Registry
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.Material
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.forge.registerObject


object KontraptionBlocks {
    val BLOCKS = DeferredRegister.create(Registry.BLOCK_REGISTRY, KontraptionMod.ID)


    // the returned ObjectHolderDelegate can be used as a property delegate
    // this is automatically registered by the deferred registry at the correct times
    val PILOT_SEAT by BLOCKS.registerObject("pilot_seat") {
        pilotSeatBlock()
        //Block(BlockBehaviour.Properties.of(Material.METAL).strength(1f, 6f))
    }

    fun register() {
        BLOCKS.register(FMLJavaModLoadingContext.get().modEventBus)
    }

    //val PILOT_SEAT = BLOCKS.register<Block>("pilot_seat") { Block(BlockBehaviour.Properties.of(Material.METAL).strength(1f, 6f)) }

    fun registerItems(items: DeferredRegister<Item>) {
        for (it in BLOCKS.entries) {
            //items.register(it.get().name.toString()) { BlockItem(it.get(), Item.Properties().tab(KontraptionItems.TAB)) }
        }
    }


}