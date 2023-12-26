package net.illuc.kontraption.blocks

import mekanism.common.block.prefab.BlockTile
import mekanism.common.content.blocktype.BlockTypeTile
import net.illuc.kontraption.blockEntities.TileEntityCannon
import net.illuc.kontraption.blockEntities.TileEntityGyro
import net.minecraft.world.level.block.state.BlockBehaviour

class BlockCannon(type: BlockTypeTile<TileEntityCannon?>?) : BlockTile<TileEntityCannon?, BlockTypeTile<TileEntityCannon?>?>(type, BlockBehaviour.Properties.of()) {
}