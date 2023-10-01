package net.illuc.kontraption;

import mekanism.api.math.FloatingLong;
import mekanism.common.MekanismLang;
import mekanism.common.block.attribute.AttributeStateFacing;
import mekanism.common.block.attribute.Attributes;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.content.blocktype.Machine;
import net.illuc.kontraption.blockEntities.TileIonThruster;
import net.illuc.kontraption.blockType.Thruster;

public class KontraptionBlockTypes {

    private static final FloatingLong RESISTIVE_HEATER_BASE_USAGE = FloatingLong.createConst(100);

    private KontraptionBlockTypes() {
    }

    public static final BlockTypeTile<TileIonThruster> ION_THRUSTER = BlockTypeTile.BlockTileBuilder
            .createBlock(() -> KontraptionTileEntityTypes.ION_THRUSTER, MekanismLang.HOLD_FOR_DESCRIPTION)
            .withEnergyConfig(() -> RESISTIVE_HEATER_BASE_USAGE, null)
            .with(new AttributeStateFacing())
            .build();


}
