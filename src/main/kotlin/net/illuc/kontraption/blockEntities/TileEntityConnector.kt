package net.illuc.kontraption.blockEntities
import dan200.computercraft.shared.Capabilities
import mekanism.common.tile.base.TileEntityMekanism
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
    private val ship: ServerShip? get() = getShipObjectManagingPos((level as ServerLevel), this.blockPos)
    private val sLevel: ServerLevel? get() = level as? ServerLevel
    var conid: Int = 666 // placeholder as i dont want to play with nulls, rlly hope it wont cause errors in future UPDATE: it wont, may troll CC users a bit xd
    var isConnected = false
    var underCC = false
    val x = this.blockPos.x.toDouble()
    val y = this.blockPos.y.toDouble()
    val z = this.blockPos.z.toDouble()
    val vectorBlocpos = Vector3d(x, y, z) // stupid solution but most of .to conversion are somewhy broken to me
    private val peripheral = ConnectorPeripheral(this)
    private val peripheralCapability = LazyOptional.of { peripheral }

    override fun <T> getCapability(
        capability: Capability<T>,
        side: Direction?,
    ): LazyOptional<T> =
        if (ModList.get().isLoaded("computercraft")) {
            if (capability == Capabilities.CAPABILITY_PERIPHERAL as Capability<T>) {
                peripheralCapability as LazyOptional<T>
            } else {
                super.getCapability(capability, side)
            }
        } else {
            super.getCapability(capability, side)
        }

    fun enable() {
        // as ceo intended
    }

    override fun onLoad() {
        super.onLoad()
        if (!level?.isClientSide!!) {
            if (this.isConnected) {
                this.isConnected = false
                this.connect()
            } // unfortunly connections dont save on load
        }
    }

    private fun rayTrace(distance: Double): BlockEntity? {
        val blockState = sLevel!!.getBlockState(blockPos)
        val fdirection = blockState.getValue(BlockStateProperties.FACING)
        var startVec = Vec3.atCenterOf(blockPos).add(Vec3.atLowerCornerOf(fdirection.normal).scale(0.7))
        val directionVec = Vec3(fdirection.stepX.toDouble(), fdirection.stepY.toDouble(), fdirection.stepZ.toDouble())
        var endVec = startVec.add(directionVec.scale(distance))
        if (ship?.id != null) {
            val tempstartVec = ship!!.transform.shipToWorld.transformPosition(fV3V3d(startVec))
            val tempendVec = ship!!.transform.shipToWorld.transformPosition(fV3V3d(endVec))
            endVec = fV3dV3(tempendVec)
            startVec = fV3dV3(tempstartVec)
            // Mekanism.logger.info("Transformed raycast")
        }

        val context =
            ClipContext(
                startVec,
                endVec,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                null,
            )
        val bhitresul = sLevel!!.clip(context)
        if (bhitresul.type === HitResult.Type.BLOCK) {
            val hitPos: BlockPos = bhitresul.blockPos
            val btype = sLevel!!.getBlockEntity(hitPos) // 50/50 its my serverLevel fault
            if (btype != null) {
                if (btype::class == this::class) {
                    return btype
                }
            }
        } else {
            // Mekanism.logger.info("No block hit.") ALSO UHH too lazy to change its imple XD
        }
        return null
    }

    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
        tag.putInt("ConID", conid)
        tag.putBoolean("isconnected", isConnected)
        tag.putBoolean("undercc", underCC)
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)
        conid = tag.getInt("ConID")
        isConnected = tag.getBoolean("isconnected")
        underCC = tag.getBoolean("undercc")
    }

    fun check(): Pair<BlockEntity?, ServerShip?>? {
        val world = this.level ?: return null // kinda needed lol
        if (world.isClientSide) {
            return null // mf crash
        }
        val bp2 = rayTrace(2.5) ?: return null
        val tileEntity2c: TileEntityConnector? = bp2 as? TileEntityConnector
        val ship2 = tileEntity2c?.ship
        return Pair(tileEntity2c, ship2)
    }

    fun connectpass() {
        this.connect() // for some VERY WEIRD REASON, no matter if i use this weird passthru or normal .connect it CANNOT get tileEntity for some reason like WTF? Raycast gets correct possition(same as redstone eq) but doesnt get tileentity
    }

    fun connect() {
        if (this.level?.isClientSide!!) {
            return
        }
        var shipid2: ShipId?
        val getPaz = check()
        val (tileEntity2, ship2) = getPaz ?: Pair(null, null)
        val tileEntity2c: TileEntityConnector? = tileEntity2 as? TileEntityConnector
        if (ship2 == null && tileEntity2c != null) {
            // Mekanism.logger.info("Didnt find SHIP2 but found TE2, so static connection")
        }
        val shipid1 = ship?.id
        shipid2 =
            if (ship2?.id != null) {
                ship2.id
            } else {
                OttUtils.getDimID(tileEntity2c?.level as ServerLevel)
            }
        if (shipid2 == null || shipid1 == null) {
            // Mekanism.logger.info("SHIPID NULLED AND CONNECTOR NULLED  ID1:$shipid1 ID2:$shipid2")
            return
        }
        // Mekanism.logger.info("Attempting connection")
        if (!this.isConnected && tileEntity2c?.isConnected == false) {
            // Mekanism.logger.info("both are not connected")
            val sypos1 = vectorBlocpos
            val sypos2 = tileEntity2.vectorBlocpos
            // println("SX1: ${sypos1.x()} SY1: ${sypos1.y()} SZ1: ${sypos1.z()}")
            // println("SX2: ${sypos2.x()} SY2: ${sypos2.y()} SZ2: ${sypos2.z()}")
            val constraint =
                VSAttachmentConstraint(
                    shipid1,
                    shipid2,
                    compliance = 1e-10,
                    localPos0 = sypos1,
                    localPos1 = sypos2,
                    maxForce = 1e10,
                    fixedDistance = 0.5,
                )
            conid = KontraptionVSUtils.getShipObjectWorld(sLevel).createNewConstraint(constraint)!!
            isConnected = true
        }
    }

    fun tick() {
        if (!underCC) {
            if (canConnect()) {
                connect()
            }
        }
    }

    fun canConnect(): Boolean {
        if (this.level?.hasNeighborSignal(blockPos) == true) {
            if (!isConnected) {
                return true
            }
            return false
        } else {
            if (isConnected && !underCC) {
                KontraptionVSUtils.getShipObjectWorld(sLevel).removeConstraint(conid)
                isConnected = false
                return false
            }
            return false
        }
    }

    fun disconnect() {
        KontraptionVSUtils.getShipObjectWorld(sLevel).removeConstraint(conid)
    }
}
