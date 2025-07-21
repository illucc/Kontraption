package net.illuc.kontraption.multiblocks.railgun

import mekanism.common.lib.multiblock.IValveHandler
import mekanism.common.lib.multiblock.MultiblockData
import net.illuc.kontraption.blockEntities.railgun.TileEntityRailgunCasing

class RailgunMultiblockData(
    tile: TileEntityRailgunCasing,
) : MultiblockData(tile),
    IValveHandler
