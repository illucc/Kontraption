package net.illuc.kontraption.blockType;

import mekanism.api.text.ILangEntry;
import mekanism.common.block.attribute.AttributeStateFacing;
import mekanism.common.block.attribute.Attributes;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;

import java.util.function.Supplier;

public class Thruster<TILE extends TileEntityMekanism> extends BlockTypeTile<TILE> {

    public Thruster(Supplier<TileEntityTypeRegistryObject<TILE>> tileEntityRegistrar, ILangEntry description) {
        super(tileEntityRegistrar, description);
        add(Attributes.ACTIVE_LIGHT, new AttributeStateFacing(), Attributes.SECURITY, Attributes.INVENTORY, Attributes.REDSTONE, Attributes.COMPARATOR);
    }

    public static class ThrusterBuilder<THRUSTER extends Thruster<TILE>, TILE extends TileEntityMekanism, T extends ThrusterBuilder<THRUSTER, TILE, T>> extends BlockTileBuilder<THRUSTER, TILE, T> {

        protected ThrusterBuilder(THRUSTER holder) {
            super(holder);
        }

        public static <TILE extends TileEntityMekanism> ThrusterBuilder<Thruster<TILE>, TILE, ?> createThruster(Supplier<TileEntityTypeRegistryObject<TILE>> tileEntityRegistrar,
                                                                                                                 ILangEntry description) {
            return new ThrusterBuilder<>(new Thruster<>(tileEntityRegistrar, description));
        }
    }
}