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
    public static final BlockRegistryObject<BlockIonThruster, BlockItem> ION_THRUSTER = BLOCKS.register("ion_thruster", () -> new BlockIonThruster(KontraptionBlockTypes.ION_THRUSTER));
    public static final BlockRegistryObject<PilotSeatBlock, BlockItem> PILOT_SEAT = BLOCKS.register("pilot_seat", () -> new PilotSeatBlock(KontraptionBlockTypes.PILOT_SEAT));
    public static final BlockRegistryObject<BlockGyro, BlockItem> GYRO = BLOCKS.register("gyro", () -> new BlockGyro(KontraptionBlockTypes.GYRO));
    //needs work
    public static final BlockRegistryObject<BlockWheel, BlockItem> WHEEL = BLOCKS.register("wheel", () -> new BlockWheel(KontraptionBlockTypes.WHEEL));
    public static final BlockRegistryObject<BlockBasicMultiblock, BlockItem> HYDROGEN_THRUSTER_CASING = BLOCKS.register("hydrogen_thruster_casing", () -> new BlockBasicMultiblock<>(KontraptionBlockTypes.HYDROGEN_THRUSTER_CASING));
}
