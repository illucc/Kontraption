package net.illuc.kontraption

import mekanism.common.MekanismLang
import mekanism.common.block.attribute.Attributes.AttributeMobSpawn
import mekanism.common.block.attribute.Attributes.AttributeMultiblock
import mekanism.common.content.blocktype.BlockTypeTile
import mekanism.common.content.blocktype.BlockTypeTile.BlockTileBuilder
import mekanism.common.content.blocktype.Machine.MachineBuilder
import mekanism.common.tile.base.TileEntityMekanism
import net.illuc.kontraption.blockEntities.TileIonThruster
import java.util.*


public object KontraptionBlockTypes {
    private fun KontraptionBlockTypes() {}

    val ION_THRUSTER: BlockTypeTile<TileIonThruster> = MachineBuilder
            .createMachine({ KontraptionTileEntityTypes.ION_THRUSTER }, MekanismLang.HOLD_FOR_DESCRIPTION)
            .build()


}