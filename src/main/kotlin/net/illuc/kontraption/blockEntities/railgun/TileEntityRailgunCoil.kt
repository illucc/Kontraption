package net.illuc.kontraption.blockEntities.railgun

import net.illuc.kontraption.KontraptionBlocks
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

class TileEntityRailgunCoil(
    pos: BlockPos?,
    state: BlockState?,
) : TileEntityRailgunCasing(KontraptionBlocks.RAILGUN_COIL, pos, state)
