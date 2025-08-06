package net.illuc.kontraption.blockEntities
import dan200.computercraft.shared.Capabilities
import mekanism.common.tile.base.TileEntityMekanism
import net.illuc.kontraption.Kontraption
import net.illuc.kontraption.KontraptionBlocks
import net.illuc.kontraption.peripherals.ConnectorPeripheral
import net.illuc.kontraption.util.KontraptionVSUtils
import net.illuc.kontraption.util.KontraptionVSUtils.getShipObjectManagingPos
import net.illuc.kontraption.util.OttUtils
import net.illuc.kontraption.util.OttUtils.fV3V3d
import net.illuc.kontraption.util.OttUtils.fV3dV3
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.fml.ModList
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.apigame.constraints.VSAttachmentConstraint

// Comment for CEO, im not even throwing that BAD model i did
class TileEntityConnector(
    pos: BlockPos?,
    state: BlockState?,
) : TileEntityMekanism(KontraptionBlocks.CONNECTOR, pos, state) {
    private val peripheral = ConnectorPeripheral(this)
    private val peripheralCapability = LazyOptional.of { peripheral }

    // TODO: ADD THIS TO GODFORSAKEN CONFIG LIST(SHIP CONFIG)
    var conid: Int = 666
    var isConnected = false
    var underCC = false
    var connectedTo: BlockPos? = null

    val vectorBlocpos: Vector3d
        get() = Vector3d(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble())

    private val sLevel: ServerLevel?
        get() = level as? ServerLevel

    private val ship: ServerShip?
        get() = sLevel?.let { getShipObjectManagingPos(it, blockPos) }

    override fun <T> getCapability(
        capability: Capability<T>,
        side: Direction?,
    ): LazyOptional<T> =
        if (ModList.get().isLoaded("computercraft") && capability == Capabilities.CAPABILITY_PERIPHERAL as Capability<T>) {
            peripheralCapability as LazyOptional<T>
        } else {
            super.getCapability(capability, side)
        }

    fun enable() {
        // as ceo intended
    }

    override fun onLoad() {
        super.onLoad()
        sLevel?.let {
            if (isConnected && connectedTo != null) {
                val other = it.getBlockEntity(connectedTo!!) as? TileEntityConnector
                if (other != null && !other.isConnected) {
                    connectTo(other)
                } else {
                    isConnected = false
                    connectedTo = null
                }
            }
        }
    }

    private fun rayTrace(distance: Double): BlockEntity? {
        // Maybe change to static distance here anyway
        val serverLevel = sLevel ?: return null
        val state = serverLevel.getBlockState(blockPos)
        val facing = state.getValue(BlockStateProperties.FACING)

        var startVec = Vec3.atCenterOf(blockPos).add(Vec3.atLowerCornerOf(facing.normal).scale(0.7))
        var endVec = startVec.add(Vec3(facing.stepX.toDouble(), facing.stepY.toDouble(), facing.stepZ.toDouble()).scale(distance))

        ship?.let {
            startVec = fV3dV3(it.transform.shipToWorld.transformPosition(fV3V3d(startVec)))
            endVec = fV3dV3(it.transform.shipToWorld.transformPosition(fV3V3d(endVec)))
        }

        val result = serverLevel.clip(ClipContext(startVec, endVec, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, null))
        if (result.type == HitResult.Type.BLOCK) {
            Kontraption.LOGGER.debug("Hit Block")
            return serverLevel.getBlockEntity(result.blockPos)?.takeIf { it is TileEntityConnector } // HUH it is neat to use kotlin functions . . . okay
        }

        return null
    }

    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
        tag.putInt("ConID", conid)
        tag.putBoolean("isconnected", isConnected)
        tag.putBoolean("undercc", underCC)
        connectedTo?.let {
            tag.putLong("connectedTo", it.asLong())
        }
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)
        conid = tag.getInt("ConID")
        isConnected = tag.getBoolean("isconnected")
        underCC = tag.getBoolean("undercc")
        if (tag.contains("connectedTo")) {
            connectedTo = BlockPos.of(tag.getLong("connectedTo"))
        }
    }

    private fun check(): Pair<BlockEntity?, ServerShip?>? {
        if (level == null || level!!.isClientSide) return null
        val target = rayTrace(2.5) ?: return null // Make range a config var? i mean blasting a ray cast each s could be bad? on ohter hand DRILL
        val other = target as? TileEntityConnector ?: return null
        return Pair(other, other.ship)
    }

    fun connect() {
        if (isConnected || level?.isClientSide != false) return

        val pair = check() ?: return
        val (other, ship2) = pair
        val otherConnector = other as? TileEntityConnector ?: return

        connectTo(otherConnector, ship2)
    }

    private fun connectTo(
        other: TileEntityConnector,
        ship2: ServerShip? = null,
    ) {
        if (this.isConnected || other.isConnected) return

        val ship1 = this.ship ?: return
        val sid2: ShipId = ship2?.id ?: OttUtils.getDimID(other.sLevel ?: return) ?: return

        val constraint =
            VSAttachmentConstraint(
                ship1.id,
                sid2,
                compliance = 1e-10,
                localPos0 = this.vectorBlocpos,
                localPos1 = other.vectorBlocpos,
                maxForce = 1e10,
                fixedDistance = 0.5,
            )

        val cid = KontraptionVSUtils.getShipObjectWorld(sLevel ?: return).createNewConstraint(constraint) ?: return
        this.conid = cid
        other.conid = cid
        this.connectedTo = other.blockPos
        other.connectedTo = this.blockPos
        this.isConnected = true
        other.isConnected = true
    }

    fun disconnect() {
        sLevel?.let {
            KontraptionVSUtils.getShipObjectWorld(it).removeConstraint(conid)
        }
        isConnected = false
        connectedTo = null
    }

    fun tick() {
        if (!underCC && !isConnected && canConnect()) {
            connect()
        }
    }

    fun canConnect(): Boolean {
        val hasSignal = level?.hasNeighborSignal(blockPos) == true
        if (hasSignal && !isConnected) {
            return true
        }
        if (!hasSignal && isConnected && !underCC) {
            disconnect()
        }
        return false
    }
}
