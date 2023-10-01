package net.illuc.kontraption

import mekanism.api.math.FloatingLong
import mekanism.common.MekanismLang
import mekanism.common.content.blocktype.BlockTypeTile
import mekanism.common.content.blocktype.Machine
import mekanism.common.content.blocktype.Machine.MachineBuilder
import net.illuc.kontraption.blockType.Thruster
import net.illuc.kontraption.blockType.Thruster.ThrusterBuilder
import java.util.*


public object KontraptionBlockTypess {
    private fun KontraptionBlockTypes() {}

    private val RESISTIVE_HEATER_BASE_USAGE = FloatingLong.createConst(100)


    public val ION_THRUSTER: BlockTypeTile<*> = ThrusterBuilder
            .createBlock({ KontraptionTileEntityTypes.ION_THRUSTER }, MekanismLang.HOLD_FOR_DESCRIPTION)
            .withEnergyConfig { RESISTIVE_HEATER_BASE_USAGE }
            .build()
}