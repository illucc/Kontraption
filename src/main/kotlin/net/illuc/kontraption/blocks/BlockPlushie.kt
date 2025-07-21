package net.illuc.kontraption.blocks

import it.zerono.mods.zerocore.lib.block.ModBlock
import net.illuc.kontraption.blockEntities.TileEntityPlushie
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.level.block.state.properties.RotationSegment
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class BlockPlushie(
    public val type: Type,
    properties: Properties,
) : ModBlock(properties),
    EntityBlock {
    init {
        this.registerDefaultState(this.stateDefinition.any().setValue(ROTATION, 0))
    }

    val FINAL_SHAPE: VoxelShape =
        listOf(
            Shapes.join(Block.box(5.5, -0.1, 3.90553, 7.5, 1.9, 9.90553), Block.box(5.25, -0.35, 3.65553, 7.75, 2.15, 10.15553), BooleanOp.OR),
            Shapes.join(Block.box(8.5, -0.1, 3.90553, 10.5, 1.9, 9.90553), Block.box(8.25, -0.35, 3.65553, 10.75, 2.15, 10.15553), BooleanOp.OR),
            Shapes.join(Block.box(9.75, 1.15388, 8.14725, 12.25, 7.65388, 10.64725), Block.box(10.0, 1.40388, 8.39725, 12.0, 7.40388, 10.39725), BooleanOp.OR),
            Shapes.join(Block.box(4.0, 1.40388, 8.39725, 6.0, 7.40388, 10.39725), Block.box(3.75, 1.15388, 8.14725, 6.25, 7.65388, 10.64725), BooleanOp.OR),
            Shapes.join(Block.box(5.0, 7.0, 6.6765, 11.0, 13.0, 12.6765), Block.box(4.75, 6.75, 6.4265, 11.25, 13.25, 12.9265), BooleanOp.OR),
            Shapes.join(Block.box(6.0, 0.0, 8.6765, 10.0, 7.0, 10.6765), Block.box(5.8, -0.25, 8.4265, 10.3, 7.25, 10.9265), BooleanOp.OR),
        ).reduce { v1, v2 -> Shapes.join(v1, v2, BooleanOp.OR) }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(ROTATION)
    }

    fun getYRotationDegrees(pState: BlockState): Float = RotationSegment.convertToDegrees(pState.getValue(ROTATION))

    override fun canSurvive(
        pState: BlockState,
        pLevel: LevelReader,
        pPos: BlockPos,
    ): Boolean = pLevel.getBlockState(pPos.below()).isSolid

    override fun updateShape(
        pState: BlockState,
        pFacing: Direction,
        pFacingState: BlockState,
        pLevel: LevelAccessor,
        pCurrentPos: BlockPos,
        pFacingPos: BlockPos,
    ): BlockState = if (pFacing == Direction.DOWN && !this.canSurvive(pState, pLevel, pCurrentPos)) Blocks.AIR.defaultBlockState() else super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos)

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState = this.defaultBlockState().setValue(ROTATION, Integer.valueOf(RotationSegment.convertToSegment(context.rotation)))

    override fun rotate(
        state: BlockState,
        level: LevelAccessor,
        pos: BlockPos,
        direction: Rotation,
    ): BlockState = state.setValue(ROTATION, direction.rotate(state.getValue(ROTATION), 16))

    override fun mirror(
        pState: BlockState,
        pMirror: Mirror,
    ): BlockState = pState.setValue(ROTATION, pMirror.mirror(pState.getValue(ROTATION), 16))

    override fun getShape(
        pState: BlockState,
        pLevel: BlockGetter,
        pPos: BlockPos,
        pContext: CollisionContext,
    ): VoxelShape = FINAL_SHAPE

    companion object {
        val ROTATION: IntegerProperty = BlockStateProperties.ROTATION_16
    }

    override fun newBlockEntity(
        pPos: BlockPos,
        pState: BlockState,
    ): BlockEntity = TileEntityPlushie(pPos, pState)

    override fun getRenderShape(pState: BlockState): RenderShape = RenderShape.ENTITYBLOCK_ANIMATED

    interface Type

    enum class Plushies : Type {
        OTTER,
        ILLUC,
        COSMOS,
    }
}
