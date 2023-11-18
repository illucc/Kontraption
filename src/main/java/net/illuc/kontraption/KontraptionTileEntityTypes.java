package net.illuc.kontraption;

import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import net.illuc.kontraption.blockEntities.*;

public class KontraptionTileEntityTypes {
    private KontraptionTileEntityTypes() {
    }

    public static final TileEntityTypeDeferredRegister TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister(Kontraption.MODID);

    public static final TileEntityTypeRegistryObject<TileEntityIonThruster> ION_THRUSTER = TILE_ENTITY_TYPES.register(KontraptionBlocks.ION_THRUSTER, TileEntityIonThruster::new);
    public static final TileEntityTypeRegistryObject<TileEntityShipControlInterface> SHIP_CONTROL_INTERFACE = TILE_ENTITY_TYPES.register(KontraptionBlocks.SHIP_CONTROL_INTERFACE, TileEntityShipControlInterface::new);
    public static final TileEntityTypeRegistryObject<TileEntityGyro> GYRO = TILE_ENTITY_TYPES.register(KontraptionBlocks.GYRO, TileEntityGyro::new);
    public static final TileEntityTypeRegistryObject<TileEntityWheel> WHEEL = TILE_ENTITY_TYPES.register(KontraptionBlocks.WHEEL, TileEntityWheel::new);
    public static final TileEntityTypeRegistryObject<TileEntityLiquidFuelThrusterCasing> LIQUID_FUEL_THRUSTER_CASING = TILE_ENTITY_TYPES.register(KontraptionBlocks.LIQUID_FUEL_THRUSTER_CASING, TileEntityLiquidFuelThrusterCasing::new);
    public static final TileEntityTypeRegistryObject<TileEntityLiquidFuelThrusterValve> LIQUID_FUEL_THRUSTER_VALVE = TILE_ENTITY_TYPES.register(KontraptionBlocks.LIQUID_FUEL_THRUSTER_VALVE, TileEntityLiquidFuelThrusterValve::new);
    public static final TileEntityTypeRegistryObject<TileEntityLiquidFuelThrusterExhaust> LIQUID_FUEL_THRUSTER_EXHAUST = TILE_ENTITY_TYPES.register(KontraptionBlocks.LIQUID_FUEL_THRUSTER_EXHAUST, TileEntityLiquidFuelThrusterExhaust::new);



}
