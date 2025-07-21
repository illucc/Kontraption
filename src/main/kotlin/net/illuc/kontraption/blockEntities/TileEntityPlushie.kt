package net.illuc.kontraption.blockEntities

import net.illuc.kontraption.GlobalRegistry
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class TileEntityPlushie(
    pos: BlockPos,
    blockState: BlockState,
) : BlockEntity(GlobalRegistry.TileEntities.PLUSHIE_ENTITY.get(), pos, blockState) {
    override fun saveAdditional(compound: CompoundTag) {
        super.saveAdditional(compound)
    }
}
