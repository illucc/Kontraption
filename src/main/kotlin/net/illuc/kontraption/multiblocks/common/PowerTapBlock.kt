package net.illuc.kontraption.multiblocks.common

import it.zerono.mods.zerocore.base.multiblock.part.io.IOPortBlock
import it.zerono.mods.zerocore.lib.block.INeighborChangeListener.Notifier
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockPartType
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController

class PowerTapBlock<Controller : IMultiblockController<Controller>?, PartType : IMultiblockPartType?>(
    properties: MultiblockPartProperties<PartType>?,
) : IOPortBlock<Controller, PartType>(properties),
    Notifier
