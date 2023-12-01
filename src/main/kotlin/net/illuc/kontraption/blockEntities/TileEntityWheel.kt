package net.illuc.kontraption.blockEntities

import mekanism.common.tile.base.TileEntityMekanism
import net.illuc.kontraption.KontraptionBlocks
import net.illuc.kontraption.util.ShotHandler
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class TileEntityWheel(pos: BlockPos?, state: BlockState?) : TileEntityMekanism(KontraptionBlocks.WHEEL, pos, state) {
    override fun onUpdateServer() {
        super.onUpdateServer()
        level?.let { ShotHandler.shoot(blockState.getValue(BlockStateProperties.FACING), it, blockPos) }
    }
}