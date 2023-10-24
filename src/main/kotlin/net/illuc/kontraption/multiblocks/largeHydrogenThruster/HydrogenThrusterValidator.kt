package net.illuc.kontraption.multiblocks.largeHydrogenThruster

import mekanism.common.content.blocktype.BlockType
import mekanism.common.lib.multiblock.CuboidStructureValidator
import mekanism.common.lib.multiblock.FormationProtocol
import mekanism.common.lib.multiblock.FormationProtocol.CasingType
import net.illuc.kontraption.KontraptionBlockTypes
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState


class HydrogenThrusterValidator : CuboidStructureValidator<HydrogenThrusterMultiblockData>() {
    override fun getCasingType(state: BlockState?): FormationProtocol.CasingType {
        val block: Block = state!!.block
        if (BlockType.`is`(block, KontraptionBlockTypes.HYDROGEN_THRUSTER_CASING)) {
            return CasingType.FRAME
        } else if (BlockType.`is`(block, KontraptionBlockTypes.HYDROGEN_THRUSTER_CASING)) {
            return CasingType.VALVE
        } else if (BlockType.`is`(block, KontraptionBlockTypes.HYDROGEN_THRUSTER_CASING)) {
            return CasingType.OTHER
        }
        return CasingType.INVALID

    }
}