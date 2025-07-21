package net.illuc.kontraption.multiblocks.largeionring.parts

import net.illuc.kontraption.GlobalRegistry
import net.illuc.kontraption.multiblocks.largeionring.LargeIonRingMultiBlock
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.client.model.data.ModelData

class LargeIonRingCasingEntity(
    position: BlockPos,
    blockState: BlockState,
) : AbstractRingEntity(GlobalRegistry.TileEntities.LARGE_ION_THRUSTER_CASING.get(), position, blockState) {
    override fun getUpdatedModelData(): ModelData = ModelData.EMPTY

    override fun createController(): LargeIonRingMultiBlock = LargeIonRingMultiBlock(currentWorld)

    override fun getControllerType(): Class<LargeIonRingMultiBlock> = LargeIonRingMultiBlock::class.java
}
