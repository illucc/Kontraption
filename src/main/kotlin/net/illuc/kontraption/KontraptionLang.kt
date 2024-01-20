package net.illuc.kontraption

import mekanism.api.text.ILangEntry

import net.minecraft.Util;


enum class KontraptionLang(private val key: String) : ILangEntry {

    KONTRAPTION("constants", "mod_name"),
    PLACEHOLDER("description", "placeholder"),
    MODE_CHANGE("toolgun", "mode_change"),
    ASSEMBLE("toolgun", "assemble"),
    MOVE("toolgun", "move"),
    PUSH("toolgun", "push"),
    LOCK("toolgun", "lock"),
    ROTATE("toolgun", "rotate"),
    WELD("toolgun", "weld"),
    DESCRIPTION_ION_THRUSTER("description", "ion_thruster"),
    TESTINGNOTICE("chat", "testing_build"),
    PRERELEASENOTICE("chat", "prerelease_build"),
    NAMEBORDER  ("chat", "titleborder"),
    BORDER      ("chat", "border");

    constructor(type: String, path: String) : this(Util.makeDescriptionId(type, Kontraption.rl(path)))

    override fun getTranslationKey(): String {
        return key
    }
}