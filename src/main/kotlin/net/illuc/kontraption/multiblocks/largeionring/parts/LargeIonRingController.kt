package net.illuc.kontraption.multiblocks.largeionring.parts

import it.zerono.mods.zerocore.base.CommonConstants
import it.zerono.mods.zerocore.lib.block.TileCommandDispatcher
import it.zerono.mods.zerocore.lib.multiblock.cuboid.PartPosition
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator
import net.illuc.kontraption.GlobalRegistry
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.client.model.data.ModelData

class LargeIonRingController(
    position: BlockPos,
    blockState: BlockState,
) : AbstractRingEntity(GlobalRegistry.TileEntities.LARGE_ION_THRUSTER_CONTROLLER.get(), position, blockState) {
    init {
        setCommandDispatcher(
            TileCommandDispatcher
                .builder<LargeIonRingController>()
                .addServerHandler(CommonConstants.COMMAND_ACTIVATE) { e -> e.setRingActive(true) }
                .addServerHandler(CommonConstants.COMMAND_DEACTIVATE) { tce -> tce.setRingActive(false) }
                .build(this),
        )
    }

    override fun getUpdatedModelData(): ModelData = ModelData.EMPTY

    override fun isGoodForPosition(
        p0: PartPosition,
        p1: IMultiblockValidator,
    ): Boolean = false

    override fun canOpenGui(
        world: Level,
        position: BlockPos,
        state: BlockState,
    ): Boolean = super.isMachineAssembled()

    override fun load(tag: CompoundTag) {
        super.load(tag)
        multiblockController.get().addToShip()
    }
}
