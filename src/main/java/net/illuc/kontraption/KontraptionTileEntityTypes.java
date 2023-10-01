package net.illuc.kontraption;

import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import net.illuc.kontraption.blockEntities.TileIonThruster;

public class KontraptionTileEntityTypes {
    private KontraptionTileEntityTypes() {
    }

    public static final TileEntityTypeDeferredRegister TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister(Kontraption.MODID);

    public static final TileEntityTypeRegistryObject<TileIonThruster> ION_THRUSTER = TILE_ENTITY_TYPES.register(KontraptionBlocks.ION_THRUSTER, TileIonThruster::new);

}
