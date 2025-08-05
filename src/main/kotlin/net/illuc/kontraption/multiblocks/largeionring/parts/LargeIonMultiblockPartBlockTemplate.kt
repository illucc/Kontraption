package net.illuc.kontraption.multiblocks.largeionring.parts

import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartBlock
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController
import net.illuc.kontraption.multiblocks.largeionring.IIonRingPartType
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.level.block.state.properties.IntegerProperty

open class LargeIonMultiblockPartBlockTemplate<Controller : IMultiblockController<Controller>, PartType : IIonRingPartType>(
    properties: MultiblockPartProperties<PartType>,
) : MultiblockPartBlock<Controller, PartType>(properties),
    EntityBlock {
    init {
        registerDefaultState(
            stateDefinition
                .any()
                .setValue(ROT, Direction.UP)
                .setValue(STATETYPE, 0)
                .setValue(FACING, Direction.NORTH),
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(ROT, STATETYPE, FACING)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
        if (!partType.equals(IonRingPartTypes.Coil)) {
            defaultBlockState().setValue(FACING, context.horizontalDirection.opposite)
        } else {
            defaultBlockState().setValue(FACING, context.nearestLookingDirection.opposite)
        }

    @Deprecated("Annoying ass IDE", ReplaceWith("Nothing, bc overriding is fine"))
    override fun getRenderShape(pState: BlockState): RenderShape =
        if (pState.getValue(STATETYPE) != 0) {
            RenderShape.ENTITYBLOCK_ANIMATED
        } else {
            RenderShape.MODEL
        }

    companion object {
        val FACING: DirectionProperty = DirectionProperty.create("facing", *Direction.entries.toTypedArray())
        val ROT: DirectionProperty = DirectionProperty.create("rotation")
        val STATETYPE: IntegerProperty = IntegerProperty.create("statetype", 0, 10)
    }
}
