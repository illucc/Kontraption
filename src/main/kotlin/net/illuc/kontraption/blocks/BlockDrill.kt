package net.illuc.kontraption.blocks

import mekanism.common.block.prefab.BlockTile
import mekanism.common.content.blocktype.BlockTypeTile
import net.illuc.kontraption.blockEntities.TileEntityDrill
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState

class BlockDrill(
    type: BlockTypeTile<TileEntityDrill?>?,
) : BlockTile<TileEntityDrill?, BlockTypeTile<TileEntityDrill?>?>(type, BlockBehaviour.Properties.of()) {
    override fun onPlace(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        oldState: BlockState,
        isMoving: Boolean,
    ) {
        val be = world.getBlockEntity(pos) as TileEntityDrill
        be.enable()
        super.onPlace(state, world, pos, oldState, isMoving)
    }

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>,
    ): BlockEntityTicker<T> =
        BlockEntityTicker { level, pos, state, blockEntity ->
            if (level.isClientSide) return@BlockEntityTicker
            if (blockEntity is TileEntityDrill) {
                blockEntity.tick()
            }
        }
}
