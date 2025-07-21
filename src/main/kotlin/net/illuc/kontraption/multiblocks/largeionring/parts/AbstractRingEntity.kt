package net.illuc.kontraption.multiblocks.largeionring.parts

import it.zerono.mods.zerocore.base.multiblock.part.AbstractMultiblockEntity
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockPartTypeProvider
import it.zerono.mods.zerocore.lib.data.geometry.CuboidBoundingBox
import it.zerono.mods.zerocore.lib.energy.IWideEnergyStorage
import it.zerono.mods.zerocore.lib.energy.NullEnergyHandlers
import it.zerono.mods.zerocore.lib.multiblock.cuboid.PartPosition
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator
import net.illuc.kontraption.multiblocks.largeionring.IIonRingPartType
import net.illuc.kontraption.multiblocks.largeionring.LargeIonRingMultiBlock
import net.illuc.kontraption.util.OttUtils
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

abstract class AbstractRingEntity(
    type: BlockEntityType<*>,
    var position: BlockPos,
    blockState: BlockState,
) : AbstractMultiblockEntity<LargeIonRingMultiBlock>(type, position, blockState),
    IMultiblockPartTypeProvider<LargeIonRingMultiBlock, IIonRingPartType> {
    var isTop: Boolean = false
    var isCorner: Boolean = false
    var rotation: Direction = Direction.WEST
    var changedRotation: Boolean = false
    var centerPos: BlockPos = position
    var isController: Boolean = false
    var mbSize: Int = -1
    var enabled: Boolean = false

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket = ClientboundBlockEntityDataPacket.create(this)

    private fun setBlockState(
        level: Level,
        pos: BlockPos,
        newState: BlockState,
    ) {
        if (!level.isClientSide) {
            level.setBlockAndUpdate(pos, newState)
            level.sendBlockUpdated(pos, newState, newState, 3)
        }
    }

    private fun setRotation(
        dir: Direction,
        pos: BlockPos,
    ) {
        val bst = level!!.getBlockState(pos).setValue(LargeIonMultiblockPartBlockTemplate.ROT, dir)
        setBlockState(level!!, pos, bst)
        changedRotation = true
    }

    private fun changeState(SETZERO: Boolean = false) {
        val level = level ?: return
        val pos = blockPos
        val underPos = BlockPos(pos.x, pos.y - 1, pos.z)
        val bbe = level.getBlockEntity(underPos)
        val blockState = level.getBlockState(pos)
        val currentStateType = blockState.getValue(LargeIonMultiblockPartBlockTemplate.STATETYPE)
        var newStateType = 0
        if (!SETZERO) {
            newStateType =
                when (bbe) {
                    is LargeIonRingPowerPortEntity -> 2
                    is LargeIonRingController -> 3
                    is LargeIonRingCasingEntity -> 1
                    else -> 10 // 10 is uh render disabler
                }
        }
        if (!isTop && isMachineAssembled) {
            newStateType = 10
        }
        if (isMachineAssembled && isController) {
            newStateType = 9
        }
        if (currentStateType != newStateType) {
            val updatedState = blockState.setValue(LargeIonMultiblockPartBlockTemplate.STATETYPE, newStateType)
            setBlockState(level, pos, updatedState)
        }
        val boundbox = multiblockCenter()
        val relativeCenter =
            BlockPos(
                (boundbox.minX + boundbox.maxX) / 2,
                (boundbox.maxY),
                (boundbox.minZ + boundbox.maxZ) / 2,
            )
        if (this.isMachineAssembled) {
            if (isCorner) {
                val bStateCorner = this.blockState.setValue(LargeIonMultiblockPartBlockTemplate.STATETYPE, 4)
                setBlockState(level, pos, bStateCorner)
                val cornerRot = checkcornerrotation(pos)
                setRotation(cornerRot, pos)
            } else {
                checkRotation(pos, relativeCenter)
                changedRotation = false
            }
            changedRotation = false
        }
    }

    private fun checkcornerrotation(pos: BlockPos): Direction {
        val isNorth = level!!.getBlockEntity(pos.north()) is AbstractRingEntity
        val isSouth = level!!.getBlockEntity(pos.south()) is AbstractRingEntity
        val isEast = level!!.getBlockEntity(pos.east()) is AbstractRingEntity
        val isWest = level!!.getBlockEntity(pos.west()) is AbstractRingEntity
        return when {
            isNorth && isEast -> Direction.WEST
            isNorth && isWest -> Direction.SOUTH
            isSouth && isEast -> Direction.NORTH
            isSouth && isWest -> Direction.EAST
            else -> Direction.NORTH
        }
    }

    private fun checkRotation(
        Pos: BlockPos,
        Center: BlockPos,
    ) {
        if (this.isMachineAssembled) {
            if (!isCorner) {
                rotation = OttUtils.getDirectionFromPositions(Center, Pos)
                setRotation(rotation, blockPos)
            }
        }
    }

    protected fun multiblockCenter(): CuboidBoundingBox =
        this.multiblockController
            .filter(LargeIonRingMultiBlock::isAssembled)
            .map(LargeIonRingMultiBlock::getBoundingBox)
            .orElse(CuboidBoundingBox.EMPTY)

    protected fun IsRingActive(): Boolean =
        this
            .multiblockController
            .filter(LargeIonRingMultiBlock::isAssembled)
            .map(LargeIonRingMultiBlock::enabled)
            .orElse(false)

    fun getPartDisplayName(): Component =
        Component.translatable(
            this.partType
                .map<Any>(IIonRingPartType::getTranslationKey)
                .orElse("unknown")
                .toString(),
        )

    protected fun setRingActive(active: Boolean) {
        this.multiblockController
            .filter(LargeIonRingMultiBlock::isAssembled)
            .ifPresent { c: LargeIonRingMultiBlock -> c.setMachineActive(active) }
    }

    fun getEnergyStorage(): IWideEnergyStorage = this.evalOnController(LargeIonRingMultiBlock::getEnergyStorage, NullEnergyHandlers.STORAGE)

    override fun isGoodForPosition(
        p0: PartPosition,
        p1: IMultiblockValidator,
    ): Boolean {
        p1.setLastError(this.getWorldPosition(), "multiblock.validation.ring.ILLEGAL_CHECK")
        return true
    }

    override fun createController(): LargeIonRingMultiBlock {
        val myWorld = this.getLevel() ?: throw RuntimeException("Trying to create a Controller from a Part without a Level")

        return LargeIonRingMultiBlock(myWorld)
    }

    override fun onPostMachineAssembled(controller: LargeIonRingMultiBlock) {
        this.changeState()
        super.onPostMachineAssembled(controller)
    }

    override fun onPostMachineBroken() {
        this.changeState(true)
        super.onPostMachineBroken()
    }

    override fun getControllerType(): Class<LargeIonRingMultiBlock> = LargeIonRingMultiBlock::class.java

    override fun onMachineActivated() {
    }

    override fun onMachineDeactivated() {
    }

    fun setRNTags(
        center: BlockPos?,
        size: Int,
    ) {
        if (center != null) {
            centerPos = center
        }
        mbSize = size
        changeState()
    }

    fun setEnabledd(enabled: Boolean) {
        this.enabled = enabled
        setChanged()
        if (level != null && !level!!.isClientSide) {
            setChanged()
            level!!.sendBlockUpdated(
                this.blockPos,
                this.blockState,
                this.blockState,
                3,
            )
        }
    }

    override fun getUpdateTag(): CompoundTag {
        val tag = super.getUpdateTag()
        centerPos.let {
            tag.putInt("centerX", it.x)
            tag.putInt("centerY", it.y)
            tag.putInt("centerZ", it.z)
        }
        tag.putInt("mbSize", mbSize)
        tag.putBoolean("enabled", enabled)
        return tag
    }

    override fun handleUpdateTag(tag: CompoundTag) {
        super.handleUpdateTag(tag)
        if (tag.contains("centerX")) {
            centerPos = BlockPos(tag.getInt("centerX"), tag.getInt("centerY"), tag.getInt("centerZ"))
        }
        if (tag.contains("mbSize")) {
            mbSize = tag.getInt("mbSize")
        }
        if (tag.contains("enabled")) {
            enabled = tag.getBoolean("enabled")
        }
    }

    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
        tag.putInt("centerX", centerPos.x)
        tag.putInt("centerY", centerPos.y)
        tag.putInt("centerZ", centerPos.z)
        tag.putInt("mbSize", mbSize)
        tag.putBoolean("enabled", enabled)
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)
        centerPos = BlockPos(tag.getInt("centerX"), tag.getInt("centerY"), tag.getInt("centerZ"))
        mbSize = tag.getInt("mbSize")
        enabled = tag.getBoolean("enabled")
    }
}
