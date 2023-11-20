package net.illuc.kontraption.config

import mekanism.common.config.MekanismConfigHelper
import net.minecraftforge.fml.ModLoadingContext


object KontraptionConfigs {
    val kontraption: KontraptionConfig = KontraptionConfig()
    fun registerConfigs(modLoadingContext: ModLoadingContext) {
        val modContainer = modLoadingContext.activeContainer
        MekanismConfigHelper.registerConfig(modContainer, kontraption)
    }
}