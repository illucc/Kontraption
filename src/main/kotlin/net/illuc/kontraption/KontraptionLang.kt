package net.illuc.kontraption

import mekanism.api.text.ILangEntry

import net.minecraft.Util;


enum class KontraptionLang(private val key: String) : ILangEntry {
    DESCRIPTION_ION_THRUSTER("description", "ion_thruster");

    constructor(type: String, path: String) : this(Util.makeDescriptionId(type, Kontraption.rl(path)))

    override fun getTranslationKey(): String {
        return key
    }
}