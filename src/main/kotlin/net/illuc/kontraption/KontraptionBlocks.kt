package net.illuc.kontraption

import mekanism.common.block.interfaces.IHasDescription
import mekanism.common.block.prefab.BlockTile.BlockTileModel
import mekanism.common.content.blocktype.BlockTypeTile
import mekanism.common.content.blocktype.Machine
import mekanism.common.item.block.ItemBlockTooltip
import mekanism.common.item.block.machine.ItemBlockMachine
import mekanism.common.registration.impl.BlockDeferredRegister
import mekanism.common.registration.impl.BlockRegistryObject
import mekanism.common.tile.base.TileEntityMekanism
import net.illuc.kontraption.blockEntities.TileIonThruster
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import java.util.function.Supplier

/*
public object KontraptionBlocks {
    private fun KontraptionBlocks() {}

    val BLOCKS = BlockDeferredRegister(Kontraption.MODID)


    //val TURBINE_CASING: BlockRegistryObject<BlockBasicMultiblock<TileIonThruster>, ItemBlockTooltip<BlockBasicMultiblock<TileIonThruster>>> = registerTooltipBlock("turbine_casing") { BlockBasicMultiblock<TILE>(KontraptionBlocks.ION_THRUSTER) }

    //val ION_THRUSTER = BLOCKS.register("ion_thruster", { BlockTileModel(KontraptionBlockTypes.ION_THRUSTER) }) { block: BlockTileModel<TileIonThruster?, Machine<TileIonThruster?>?>? -> ItemBlockMachine(block) }

    //val ION_THRUSTER: BlockRegistryObject<BlockTile<TileIonThruster?, Machine<TileIonThruster?>?>, ItemBlockMachine> = BLOCKS.register<BlockTile<TileIonThruster?, Machine<TileIonThruster?>?>, ItemBlockMachine>("ion_thruster", Supplier<BlockTile<TileIonThruster?, Machine<TileIonThruster?>?>> { BlockTile<TileEntityMekanism, BlockTypeTile<TileEntityMekanism>>(KontraptionBlockTypes., UnaryOperator<BlockBehaviour.Properties> { properties: BlockBehaviour.Properties -> properties.mapColor(BlockResourceInfo.STEEL.getMapColor()) }) }, Function<BlockTile<TileEntityDimensionalStabilizer?, Machine<TileEntityDimensionalStabilizer?>?>, ItemBlockMachine> { block: BlockTile<TileEntityDimensionalStabilizer?, Machine<TileEntityDimensionalStabilizer?>?>? -> ItemBlockMachine(block) })
    //val ION_THRUSTER: BlockRegistryObject<BlockTileModel<TileIonThruster?, Machine<TileIonThruster?>>, ItemBlockMachine>? = BLOCKS.register<BlockTileModel<TileIonThruster?, Machine<TileIonThruster?>>, ItemBlockMachine>("ion_thruster", { BlockTileModel<TileEntityMekanism?, BlockTypeTile<TileEntityMekanism>>(KontraptionBlockTypes.ION_THRUSTER) }) { block: BlockTileModel<TileIonThruster?, Machine<TileIonThruster?>>? -> ItemBlockMachine(block) }

    private fun <BLOCK> registerTooltipBlock(name: String, blockCreator: Supplier<BLOCK>): BlockRegistryObject<BLOCK, ItemBlockTooltip<BLOCK>>? where BLOCK : Block?, BLOCK : IHasDescription? {
        return BLOCKS.registerDefaultProperties(name, blockCreator) { block: BLOCK, properties: Item.Properties? -> ItemBlockTooltip(block, properties) }
    }

}*/