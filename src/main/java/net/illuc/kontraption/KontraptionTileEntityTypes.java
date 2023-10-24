package net.illuc.kontraption;

import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import net.illuc.kontraption.blockEntities.*;

public class KontraptionTileEntityTypes {
    private KontraptionTileEntityTypes() {
    }

    public static final TileEntityTypeDeferredRegister TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister(Kontraption.MODID);

    public static final TileEntityTypeRegistryObject<TileEntityIonThruster> ION_THRUSTER = TILE_ENTITY_TYPES.register(KontraptionBlocks.ION_THRUSTER, TileEntityIonThruster::new);
    public static final TileEntityTypeRegistryObject<TileEntityPilotSeat> PILOT_SEAT = TILE_ENTITY_TYPES.register(KontraptionBlocks.PILOT_SEAT, TileEntityPilotSeat::new);
    public static final TileEntityTypeRegistryObject<TileEntityGyro> GYRO = TILE_ENTITY_TYPES.register(KontraptionBlocks.GYRO, TileEntityGyro::new);
    public static final TileEntityTypeRegistryObject<TileEntityWheel> WHEEL = TILE_ENTITY_TYPES.register(KontraptionBlocks.WHEEL, TileEntityWheel::new);
    public static final TileEntityTypeRegistryObject<TileEntityHydrogenThrusterCasing> HYDROGEN_THRUSTER_CASING = TILE_ENTITY_TYPES.register(KontraptionBlocks.HYDROGEN_THRUSTER_CASING, TileEntityHydrogenThrusterCasing::new);



}
