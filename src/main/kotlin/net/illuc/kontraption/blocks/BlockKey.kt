package net.illuc.kontraption.blocks

import mekanism.common.block.prefab.BlockTile
import mekanism.common.content.blocktype.BlockTypeTile
import net.illuc.kontraption.Kontraption
import net.illuc.kontraption.blockEntities.TileEntityKey
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty

class BlockKey(
    type: BlockTypeTile<TileEntityKey?>?,
) : BlockTile<TileEntityKey?, BlockTypeTile<TileEntityKey?>?>(
        type,
        BlockBehaviour.Properties.of(),
    ) {
    companion object {
        val POWERED: BooleanProperty = BlockStateProperties.POWERED
    }

    init {
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(POWERED)
        super.createBlockStateDefinition(builder)
    }

    override fun onPlace(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        oldState: BlockState,
        isMoving: Boolean,
    ) {
        val be = world.getBlockEntity(pos) as TileEntityKey
        be.enable()
        super.onPlace(state, world, pos, oldState, isMoving)
    }

    override fun onRemove(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        newState: BlockState,
        isMoving: Boolean,
    ) {
        if (level !is ServerLevel) return

        val be = level.getBlockEntity(pos) as TileEntityKey
        be.disable()

        super.onRemove(state, level, pos, newState, isMoving)
    }

    override fun getSignal(
        state: BlockState,
        world: BlockGetter,
        pos: BlockPos,
        direction: net.minecraft.core.Direction,
    ): Int = if (state.getValue(POWERED)) 15 else 0

    override fun getDirectSignal(
        state: BlockState,
        world: BlockGetter,
        pos: BlockPos,
        direction: net.minecraft.core.Direction,
    ): Int = if (state.getValue(POWERED)) 15 else 0

    override fun isSignalSource(state: BlockState): Boolean = true

    fun setRedstone(
        state: Boolean,
        world: Level,
        pos: BlockPos,
    ): Boolean {
        val newstate = this.defaultBlockState().setValue(POWERED, state)
        world.setBlock(pos, newstate, 3)
        world.updateNeighborsAt(pos, this)
        val bstate = world.getBlockState(pos)
        Kontraption.LOGGER.debug("Switching red state")
        return bstate.getValue(POWERED)
    }

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>,
    ): BlockEntityTicker<T> =
        BlockEntityTicker { level, pos, state, blockEntity ->
            if (level.isClientSide) return@BlockEntityTicker
            if (blockEntity is TileEntityKey) {
                blockEntity.tick()
            }
        }
}
