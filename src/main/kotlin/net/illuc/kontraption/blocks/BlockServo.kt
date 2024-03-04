package net.illuc.kontraption.blocks

import mekanism.common.block.prefab.BlockTile
import mekanism.common.content.blocktype.BlockTypeTile
import net.illuc.kontraption.blockEntities.TileEntityServo
import net.minecraft.world.level.block.state.BlockBehaviour

class BlockServo(type: BlockTypeTile<TileEntityServo?>?) : BlockTile<TileEntityServo?, BlockTypeTile<TileEntityServo?>?>(type, BlockBehaviour.Properties.of()) {
}