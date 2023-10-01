package net.illuc.kontraption;

import mekanism.common.block.prefab.BlockTile;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.content.blocktype.Machine;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.registries.MekanismBlockTypes;
import mekanism.common.tile.TileEntityChargepad;
import net.illuc.kontraption.Kontraption;
import net.illuc.kontraption.KontraptionBlockTypes;
import net.illuc.kontraption.blockEntities.TileIonThruster;
import mekanism.common.item.block.machine.ItemBlockMachine;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import net.illuc.kontraption.blockType.Thruster;
import net.illuc.kontraption.blocks.BlockIonThruster;
import net.minecraft.world.item.BlockItem;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.Objects;

public class KontraptionBlocks {
    private KontraptionBlocks() {
    }

    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister(Kontraption.MODID);

    //public static final BlockRegistryObject<? extends BlockTileModel<?, ? extends Machine<?>>, BlockItem> ION_THRUSTER = (BlockRegistryObject<? extends BlockTileModel<?, ? extends Machine<?>>, BlockItem>) BLOCKS.register("ion_thruster", () -> new BlockTileModel<>(KontraptionBlockTypes.INSTANCE.getION_THRUSTER()));
    //public static final BlockRegistryObject<? extends BlockTileModel<?, ? extends Thruster<?>>, BlockItem> ION_THRUSTER = BLOCKS.register("ion_thruster", () -> new BlockTileModel<>(Objects.requireNonNull(KontraptionBlockTypes.INSTANCE.getION_THRUSTER())));
    //public static final BlockRegistryObject<BlockTileModel<TileIonThruster, Thruster<TileIonThruster>>, ItemBlockMachine> ION_THRUSTER = BLOCKS.register("ion_thruster", () -> new BlockTileModel<>(KontraptionBlockTypes.ION_THRUSTER), ItemBlockMachine::new);

    public static final BlockRegistryObject<BlockTileModel<TileIonThruster, BlockTypeTile<TileIonThruster>>, BlockItem> ION_THRUSTER = BLOCKS.register("ion_thruster", () -> new BlockTileModel<>(KontraptionBlockTypes.ION_THRUSTER));



}
