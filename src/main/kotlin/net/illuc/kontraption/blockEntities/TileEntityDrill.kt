package net.illuc.kontraption.blockEntities

import mekanism.common.tile.base.TileEntityMekanism
import net.illuc.kontraption.KontraptionBlocks
import net.illuc.kontraption.util.KontraptionVSUtils.getShipObjectManagingPos
import net.illuc.kontraption.util.OttUtils
import net.illuc.kontraption.util.OttUtils.rayTrace
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.BlockTags
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.ItemStackHandler
import org.valkyrienskies.core.api.ships.ServerShip

class TileEntityDrill(
    pos: BlockPos?,
    state: BlockState?,
) : TileEntityMekanism(KontraptionBlocks.DRILL, pos, state) {
    private val ship: ServerShip? get() = getShipObjectManagingPos((level as ServerLevel), this.blockPos)
    private val sLevel: ServerLevel? get() = level as? ServerLevel

    companion object {
        const val INVENTORY_SIZE: Int = 27 // perchance more
        private const val DRILL_RADIUS: Double = 10.0
        private const val DRILL_DELAY: Int = 40 // do i hear UPGRADES?
        private const val DRILL_SWEEP_ANGLE: Int = 20 // Dis goes in both ways soo its 2x for total rad
        var tool: ItemStack = ItemStack(Items.NETHERITE_PICKAXE) // ABOVE SHOULD NEVER BE IN CONFIG(imagine user setting range to 400 and sweep to 300 by accident
    }

    private var tickCT = 0
    private val inventory = ItemStackHandler(INVENTORY_SIZE)
    private val inventoryHandlerLazyOptional = LazyOptional.of { inventory }

    override fun <T> getCapability(
        cap: Capability<T>,
        side: Direction?,
    ): LazyOptional<T> =
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            inventoryHandlerLazyOptional.cast()
        } else {
            super.getCapability(cap, side)
        }

    override fun invalidateCaps() {
        super.invalidateCaps()
        inventoryHandlerLazyOptional.invalidate()
    }

    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
        tag.put("Inventory", inventory.serializeNBT())
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)
        inventory.deserializeNBT(tag.getCompound("Inventory"))
    }

    fun tick() {
        if (sLevel == null) return
        this.tickCT++
        if (this.tickCT < DRILL_DELAY) return
        this.tickCT = 0
        this.performDrillOperation(sLevel!!, this.blockPos)
    }

    private fun performDrillOperation(
        serverLevel: ServerLevel,
        start: BlockPos,
    ) {
        val step = 5

        val drops = NonNullList.create<ItemStack?>()

        for (yaw in -DRILL_SWEEP_ANGLE..DRILL_SWEEP_ANGLE step step) {
            for (pitch in -DRILL_SWEEP_ANGLE..DRILL_SWEEP_ANGLE step step) {
                val targetPos = rayTrace(DRILL_RADIUS, this, start, blockState.getValue(BlockStateProperties.FACING), ship, yaw.toFloat(), pitch.toFloat()) ?: continue

                val blockState = serverLevel.getBlockState(targetPos)
                val block = blockState.block
                if (block.defaultDestroyTime() <= 0 || !isMinable(block)) continue

                var lootContext =
                    LootParams
                        .Builder(serverLevel)
                        .withParameter(LootContextParams.ORIGIN, targetPos.center)
                        .withParameter(LootContextParams.BLOCK_STATE, blockState)
                        .withParameter(LootContextParams.TOOL, tool) // TOTALY DIDNT HAVE TO CHECK OTHER QUARRY MODS FOR DIS

                val be = serverLevel.getBlockEntity(targetPos)
                if (be != null) lootContext = lootContext.withParameter(LootContextParams.BLOCK_ENTITY, be)
                drops.addAll(blockState.getDrops(lootContext))

                val canFit = drops.all { it != null && OttUtils.canInesrtItemStack(it, inventory) }
                if (!canFit) continue

                for (drop in drops) {
                    if (drop != null) {
                        addToInventory(drop)
                    }
                }
                serverLevel.removeBlock(targetPos, false)
            }
        }
    }

    // APARENTLY SOME MORONS DONT USE TAGS!!! LIKE WHY
    private fun isMinable(block: Block): Boolean {
        val state = block.defaultBlockState()
        return state.`is`(BlockTags.MINEABLE_WITH_PICKAXE) ||
            state.`is`(BlockTags.MINEABLE_WITH_SHOVEL) ||
            state.`is`(BlockTags.MINEABLE_WITH_AXE) ||
            state.`is`(BlockTags.MINEABLE_WITH_HOE) // Today i learned how tags work :3 also dunno if i need all of em? or pix is good
    }

    private fun addToInventory(stack: ItemStack) {
        var remainingStack = stack.copy()
        for (i in 0 until inventory.slots) {
            remainingStack = inventory.insertItem(i, remainingStack, false)
            if (remainingStack.isEmpty) {
                break
            }
        } // DROPSSSS xd Ok, ok uhh i dont think leaving dis here is a good idea, should change to stop on full inv
        if (!remainingStack.isEmpty) {
            level?.addFreshEntity(
                net.minecraft.world.entity.item.ItemEntity(
                    level,
                    worldPosition.x.toDouble(),
                    worldPosition.y.toDouble(),
                    worldPosition.z.toDouble(),
                    remainingStack,
                ),
            )
        }
    }

    fun enable() {}
}
