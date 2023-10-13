package net.illuc.kontraption;

import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import net.illuc.kontraption.blockEntities.TileEntityIonThruster;
import net.illuc.kontraption.blockEntities.TileEntityPilotSeat;

public class KontraptionTileEntityTypes {
    private KontraptionTileEntityTypes() {
    }

    public static final TileEntityTypeDeferredRegister TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister(Kontraption.MODID);

    public static final TileEntityTypeRegistryObject<TileEntityIonThruster> ION_THRUSTER = TILE_ENTITY_TYPES.register(KontraptionBlocks.ION_THRUSTER, TileEntityIonThruster::new);
    public static final TileEntityTypeRegistryObject<TileEntityPilotSeat> PILOT_SEAT = TILE_ENTITY_TYPES.register(KontraptionBlocks.PILOT_SEAT, TileEntityPilotSeat::new);



}
