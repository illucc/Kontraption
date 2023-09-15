package net.illuc.kontraption

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import java.util.*

class KontraptionCreativeTab {
    companion object {
        fun create(id: ResourceLocation, stack: () -> ItemStack): CreativeModeTab {
            return ServiceLoader.load(KontraptionPlatformHelper::class.java)
                    .findFirst()
                    .get()
                    .createCreativeTab(id, stack)
        }
    }
}