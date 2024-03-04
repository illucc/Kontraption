package net.illuc.kontraption;

import mekanism.common.block.prefab.BlockBasicMultiblock;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import net.illuc.kontraption.blocks.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.List;

public class KontraptionBlocks {
    private KontraptionBlocks() {
    }

    public record PlushieData(
            String name,
            String role,
            String tooltip
    ) {
    }

    ;


    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister(Kontraption.MODID);

    // <-----BLOCKS----->
    //public static final BlockRegistryObject<BlockTile.BlockTileModel<TileEntityIonThruster, Machine<TileEntityIonThruster>>, ItemBlockMachine> ION_THRUSTER = BLOCKS.register("ion_thruster", () -> new BlockTile.BlockTileModel<>(KontraptionBlockTypes.ION_THRUSTER), ItemBlockMachine::new);
    public static final BlockRegistryObject<BlockIonThruster, BlockItem> ION_THRUSTER = BLOCKS.register("ion_thruster", () -> new BlockIonThruster(KontraptionBlockTypes.ION_THRUSTER));
    public static final BlockRegistryObject<ShipControlInterfaceBlock, BlockItem> SHIP_CONTROL_INTERFACE = BLOCKS.register("ship_control_interface", () -> new ShipControlInterfaceBlock(KontraptionBlockTypes.SHIP_CONTROL_INTERFACE));
    public static final BlockRegistryObject<BlockGyro, BlockItem> GYRO = BLOCKS.register("gyro", () -> new BlockGyro(KontraptionBlockTypes.GYRO));
    //needs work
    public static final BlockRegistryObject<BlockWheel, BlockItem> WHEEL = BLOCKS.register("wheel", () -> new BlockWheel(KontraptionBlockTypes.WHEEL));
    public static final BlockRegistryObject<BlockBasicMultiblock, BlockItem> LIQUID_FUEL_THRUSTER_CASING = BLOCKS.register("liquid_fuel_thruster_casing", () -> new BlockBasicMultiblock<>(KontraptionBlockTypes.LIQUID_FUEL_THRUSTER_CASING, properties -> properties.mapColor(MapColor.COLOR_GRAY)));
    public static final BlockRegistryObject<BlockBasicMultiblock, BlockItem> LIQUID_FUEL_THRUSTER_VALVE = BLOCKS.register("liquid_fuel_thruster_valve", () -> new BlockBasicMultiblock<>(KontraptionBlockTypes.LIQUID_FUEL_THRUSTER_VALVE, properties -> properties.mapColor(MapColor.COLOR_GRAY)));
    public static final BlockRegistryObject<BlockBasicMultiblock, BlockItem> LIQUID_FUEL_THRUSTER_EXHAUST = BLOCKS.register("liquid_fuel_thruster_exhaust", () -> new BlockBasicMultiblock<>(KontraptionBlockTypes.LIQUID_FUEL_THRUSTER_EXHAUST, properties -> properties.mapColor(MapColor.COLOR_GRAY)));

    public static final BlockRegistryObject<Block, BlockItem> RUBBER_BLOCK = BLOCKS.register("rubber_block", () -> new Block(BlockBehaviour.Properties.of()));

    public static final BlockRegistryObject<BlockCannon, BlockItem> CANNON = BLOCKS.register("cannon", () -> new BlockCannon(KontraptionBlockTypes.CANNON));
    public static final BlockRegistryObject<BlockServo, BlockItem> SERVO = BLOCKS.register("servo", () -> new BlockServo(KontraptionBlockTypes.SERVO));
}
