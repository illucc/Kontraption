package net.illuc.kontraption.blocks

import mekanism.common.block.prefab.BlockTile
import mekanism.common.content.blocktype.BlockTypeTile
import net.illuc.kontraption.blockEntities.TileEntityGyro
import net.illuc.kontraption.blockEntities.TileEntityIonThruster
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

class BlockGyro(type: BlockTypeTile<TileEntityGyro?>?) : BlockTile<TileEntityGyro?, BlockTypeTile<TileEntityGyro?>?>(type) {

    override fun onPlace(state: BlockState, world: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        val be = world.getBlockEntity(pos) as TileEntityGyro
        be.enable()
        super.onPlace(state, world, pos, oldState, isMoving)
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        if (level !is ServerLevel) return

        val be = level.getBlockEntity(pos) as TileEntityGyro
        be.disable()

        super.onRemove(state, level, pos, newState, isMoving)
    }

}