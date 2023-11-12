package net.illuc.kontraption;

import mekanism.common.block.prefab.BlockBasicMultiblock;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import net.illuc.kontraption.blocks.BlockGyro;
import net.illuc.kontraption.blocks.BlockIonThruster;
import net.illuc.kontraption.blocks.BlockWheel;
import net.illuc.kontraption.blocks.PilotSeatBlock;
import net.minecraft.world.item.BlockItem;

public class KontraptionBlocks {
    private KontraptionBlocks() {
    }

    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister(Kontraption.MODID);

    // <-----BLOCKS----->
    //public static final BlockRegistryObject<BlockTile.BlockTileModel<TileEntityIonThruster, Machine<TileEntityIonThruster>>, ItemBlockMachine> ION_THRUSTER = BLOCKS.register("ion_thruster", () -> new BlockTile.BlockTileModel<>(KontraptionBlockTypes.ION_THRUSTER), ItemBlockMachine::new);
    public static final BlockRegistryObject<BlockIonThruster, BlockItem> ION_THRUSTER = BLOCKS.register("ion_thruster", () -> new BlockIonThruster(KontraptionBlockTypes.ION_THRUSTER));
    public static final BlockRegistryObject<PilotSeatBlock, BlockItem> PILOT_SEAT = BLOCKS.register("pilot_seat", () -> new PilotSeatBlock(KontraptionBlockTypes.PILOT_SEAT));
    public static final BlockRegistryObject<BlockGyro, BlockItem> GYRO = BLOCKS.register("gyro", () -> new BlockGyro(KontraptionBlockTypes.GYRO));
    //needs work
    public static final BlockRegistryObject<BlockWheel, BlockItem> WHEEL = BLOCKS.register("wheel", () -> new BlockWheel(KontraptionBlockTypes.WHEEL));
    public static final BlockRegistryObject<BlockBasicMultiblock, BlockItem> LIQUID_FUEL_THRUSTER_CASING = BLOCKS.register("liquid_fuel_thruster_casing", () -> new BlockBasicMultiblock<>(KontraptionBlockTypes.LIQUID_FUEL_THRUSTER_CASING));
    public static final BlockRegistryObject<BlockBasicMultiblock, BlockItem> LIQUID_FUEL_THRUSTER_VALVE = BLOCKS.register("liquid_fuel_thruster_valve", () -> new BlockBasicMultiblock<>(KontraptionBlockTypes.LIQUID_FUEL_THRUSTER_VALVE));
    public static final BlockRegistryObject<BlockBasicMultiblock, BlockItem> LIQUID_FUEL_THRUSTER_EXHAUST = BLOCKS.register("liquid_fuel_thruster_exhaust", () -> new BlockBasicMultiblock<>(KontraptionBlockTypes.LIQUID_FUEL_THRUSTER_EXHAUST));
}
