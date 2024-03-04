package net.illuc.kontraption

import mekanism.common.Mekanism
import mekanism.common.MekanismLang
import mekanism.common.registries.MekanismItems
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import java.util.*
import javax.annotation.Nonnull


class KontraptionCreativeTab : CreativeModeTab(Kontraption.MODID) {
    @Nonnull
    override fun makeIcon(): ItemStack {
        return KontraptionItems.LIGHTWEIGHT_ALLOY.itemStack
    }
    @Nonnull
    override fun getDisplayName(): Component {
        return KontraptionLang.KONTRAPTION.translate()
    }
}