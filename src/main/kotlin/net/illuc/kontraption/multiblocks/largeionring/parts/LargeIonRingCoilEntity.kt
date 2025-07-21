package net.illuc.kontraption.multiblocks.largeionring.parts

import net.illuc.kontraption.GlobalRegistry
import net.illuc.kontraption.multiblocks.largeionring.LargeIonRingMultiBlock
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.client.model.data.ModelData

class LargeIonRingCoilEntity(
    position: BlockPos,
    blockState: BlockState,
) : AbstractRingEntity(GlobalRegistry.TileEntities.LARGE_ION_THRUSTER_COIL.get(), position, blockState) {
    override fun getUpdatedModelData(): ModelData = ModelData.EMPTY

    override fun createController(): LargeIonRingMultiBlock = LargeIonRingMultiBlock(currentWorld)

    override fun getControllerType(): Class<LargeIonRingMultiBlock> = LargeIonRingMultiBlock::class.java
}
