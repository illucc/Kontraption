package net.illuc.kontraption.network.to_server;

import mekanism.common.block.prefab.BlockTile;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.content.blocktype.Machine;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import net.illuc.kontraption.Kontraption;
import net.illuc.kontraption.KontraptionBlockTypes;
import net.illuc.kontraption.blockEntities.TileIonThruster;
import mekanism.common.item.block.machine.ItemBlockMachine;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;

public class KontraptionBlocks {
    private KontraptionBlocks() {
    }

    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister(Kontraption.MODID);

    public static final BlockRegistryObject<BlockTile.BlockTileModel<TileIonThruster, BlockTypeTile<TileIonThruster>>, ItemBlockMachine> ION_THRUSTER = BLOCKS.register("ion_thruster", () -> new BlockTileModel<>(KontraptionBlockTypes.INSTANCE.getION_THRUSTER()), ItemBlockMachine::new);
}
