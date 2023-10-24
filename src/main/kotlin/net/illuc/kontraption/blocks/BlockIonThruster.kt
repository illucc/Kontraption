package net.illuc.kontraption.blocks

import mekanism.common.block.prefab.BlockTile
import mekanism.common.content.blocktype.BlockTypeTile
import net.illuc.kontraption.blockEntities.TileEntityIonThruster
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import thedarkcolour.kotlinforforge.forge.vectorutil.toVec3


class BlockIonThruster(type: BlockTypeTile<TileEntityIonThruster?>?) : BlockTile<TileEntityIonThruster?, BlockTypeTile<TileEntityIonThruster?>?>(type) {

    override fun onPlace(state: BlockState, world: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        val be = world.getBlockEntity(pos) as TileEntityIonThruster
        be.enable()
        super.onPlace(state, world, pos, oldState, isMoving)
        //PositionedScreenshakeInstance(5, pos.toVec3(), 5f, 10f)
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        if (level !is ServerLevel) return

        val be = level.getBlockEntity(pos) as TileEntityIonThruster
        be.disable()

        super.onRemove(state, level, pos, newState, isMoving)
    }





}
