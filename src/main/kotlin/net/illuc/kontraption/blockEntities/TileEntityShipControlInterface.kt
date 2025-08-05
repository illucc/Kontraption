package net.illuc.kontraption.blockEntities

import com.mojang.logging.LogUtils
import dan200.computercraft.shared.Capabilities
import io.netty.buffer.Unpooled
import mekanism.common.integration.computer.ComputerException
import mekanism.common.integration.computer.IComputerTile
import mekanism.common.tile.base.TileEntityMekanism
import net.illuc.kontraption.Kontraption
import net.illuc.kontraption.KontraptionBlocks
import net.illuc.kontraption.controls.KontraptionSeatedControllingPlayer
import net.illuc.kontraption.entity.KontraptionShipMountingEntity
import net.illuc.kontraption.events.KeyBindEvent
import net.illuc.kontraption.gui.ShipTerminalMenu
import net.illuc.kontraption.peripherals.ShipControlInterfacePeripheral
import net.illuc.kontraption.ship.KontraptionGyroControl
import net.illuc.kontraption.ship.KontraptionKeyBlockControl
import net.illuc.kontraption.ship.KontraptionThrusterControl
import net.illuc.kontraption.util.ByteUtils
import net.illuc.kontraption.util.KontraptionVSUtils.getShipObjectManagingPos
import net.illuc.kontraption.util.toBlockPos
import net.illuc.kontraption.util.toDoubles
import net.illuc.kontraption.util.toJOMLD
import net.minecraft.commands.arguments.EntityAnchorArgument
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.fml.ModList
import net.minecraftforge.network.NetworkHooks
import org.joml.Quaterniond
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.saveAttachment
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.abs
import kotlin.math.absoluteValue

@Suppress("UnstableApiUsage")
class TileEntityShipControlInterface(
    pos: BlockPos?,
    state: BlockState?,
) : TileEntityMekanism(
        KontraptionBlocks.SHIP_CONTROL_INTERFACE,
        pos,
        state,
    ),
    IComputerTile {
    private val ship: ServerShip? get() = getShipObjectManagingPos((level as ServerLevel), this.blockPos)
    private var seatedControllingPlayer: KontraptionSeatedControllingPlayer? = null
    private val seats = mutableListOf<KontraptionShipMountingEntity>()
    private val logger = LogUtils.getLogger()

    private var rotTarget = Quaterniond()
    private var velTarget = Vector3d()

    val keyStates = BooleanArray(6) { false }

    lateinit var entity: KontraptionShipMountingEntity

    override fun <T> getCapability(
        capability: Capability<T>,
        side: Direction?,
    ): LazyOptional<T> {
        if (ModList.get().isLoaded("computercraft")) {
            val peripheral = ShipControlInterfacePeripheral(this)
            val peripheralCapability = LazyOptional.of { peripheral }
            return if (capability == Capabilities.CAPABILITY_PERIPHERAL as Capability<T>) {
                peripheralCapability as LazyOptional<T>
            } else {
                super.getCapability(capability, side)
            }
        }
        return super.getCapability(capability, side)
    }

    fun spawnSeat(
        blockPos: BlockPos,
        state: BlockState,
        level: ServerLevel,
    ): KontraptionShipMountingEntity {
        val newPos = blockPos.relative(state.getValue(HorizontalDirectionalBlock.FACING))
        val newState = level.getBlockState(newPos)
        val newShape = newState.getShape(level, newPos)
        val newBlock = newState.block
        var height = 0.5
        if (!newState.isAir) {
            height = newShape.max(Direction.Axis.Y)
        }
        entity =
            Kontraption.KONTRAPTION_SHIP_MOUNTING_ENTITY_TYPE.create(level)!!.apply {
                val seatEntityPos: Vector3dc = Vector3d(newPos.x + .5, (newPos.y - .5) + height, newPos.z + .5)
                moveTo(seatEntityPos.x(), seatEntityPos.y(), seatEntityPos.z())

                lookAt(
                    EntityAnchorArgument.Anchor.EYES,
                    state
                        .getValue(HORIZONTAL_FACING)
                        .normal
                        .toDoubles()
                        .add(position()),
                )

                isController = true
            }

        ship?.saveAttachment<KontraptionSeatedControllingPlayer>(KontraptionSeatedControllingPlayer(Direction.SOUTH))
        level.addFreshEntityWithPassengers(entity)
        return entity
    }

    fun getKeystones(): CopyOnWriteArrayList<KontraptionKeyBlockControl.KeyStone> = KontraptionKeyBlockControl.getOrCreate(this.ship!!).getKeystones()

    fun tick() {
        val ship = this.ship ?: return

        seatedControllingPlayer = ship.getAttachment(KontraptionSeatedControllingPlayer::class.java) ?: return
        if (seats.isNotEmpty()) {
            if (seats[0].passengers.isNotEmpty()) {
                velTarget = Vector3d(seatedControllingPlayer!!.forwardImpulse, seatedControllingPlayer!!.upImpulse, seatedControllingPlayer!!.leftImpulse)
                if (seatedControllingPlayer!!.openConfig) {
                    val player = entity.controllingPassenger as ServerPlayer
                    val posKeystones = getKeystones().map { it.position.toBlockPos() }
                    val bindKeyStones =
                        posKeystones.map { blockPos ->
                            (level!!.getBlockEntity(blockPos) as TileEntityKey).getsetKeybind(false)
                        }
                    val menuProvider =
                        object : MenuProvider {
                            override fun getDisplayName(): Component = Component.translatable("container." + Kontraption.MODID + ".TerminalGUI")

                            override fun createMenu(
                                windowId: Int,
                                playerInventory: Inventory,
                                player: Player,
                            ): AbstractContainerMenu {
                                val buf =
                                    FriendlyByteBuf(Unpooled.buffer()).apply {
                                        writeCollection(posKeystones, FriendlyByteBuf::writeBlockPos)
                                        writeCollection(bindKeyStones, FriendlyByteBuf::writeInt)
                                    }
                                return ShipTerminalMenu(windowId, playerInventory, buf)
                            }
                        }
                    NetworkHooks.openScreen(player, menuProvider) { buf ->
                        buf.writeCollection(posKeystones, FriendlyByteBuf::writeBlockPos)
                        buf.writeCollection(bindKeyStones, FriendlyByteBuf::writeInt)
                    }
                }
                // pART 3
                val keyBindings =
                    arrayOf(
                        ByteUtils.getBool(seatedControllingPlayer!!.bface, 0),
                        ByteUtils.getBool(seatedControllingPlayer!!.bface, 1),
                        ByteUtils.getBool(seatedControllingPlayer!!.bface, 2),
                        ByteUtils.getBool(seatedControllingPlayer!!.bface, 3),
                        ByteUtils.getBool(seatedControllingPlayer!!.bface, 4),
                        ByteUtils.getBool(seatedControllingPlayer!!.bface, 5),
                    )
                for (i in keyBindings.indices) {
                    val isPressed = keyBindings[i] == true
                    if (isPressed != keyStates[i]) {
                        MinecraftForge.EVENT_BUS.post(KeyBindEvent(i + 1, isPressed, entity.controllingPassenger as ServerPlayer, ship))
                        keyStates[i] = isPressed
                    }
                }
            }
        }

        val thrusters = KontraptionThrusterControl.getOrCreate(ship)
        val gyros = KontraptionGyroControl.getOrCreate(ship)

        /* thrusters.thrusterControlAll(
            this.direction.normal.toJOMLD(),
            // -seatedControllingPlayer?.forwardImpulse!!.toDouble()
            -velTarget.x,
        )

        thrusters.thrusterControlAll(
            this.direction.opposite.normal
                .toJOMLD(),
            // seatedControllingPlayer?.forwardImpulse!!.toDouble(),
            velTarget.x,
        )

        thrusters.thrusterControlAll(
            Direction.UP.normal.toJOMLD(),
            // seatedControllingPlayer?.upImpulse!!.toDouble()
            velTarget.y,
        )

        thrusters.thrusterControlAll(
            Direction.DOWN.normal.toJOMLD(),
            // -seatedControllingPlayer?.upImpulse!!.toDouble()
            -velTarget.y,
        )

        thrusters.thrusterControlAll(
            this.direction.counterClockWise.normal
                .toJOMLD(),
            // seatedControllingPlayer?.leftImpulse!!.toDouble()
            velTarget.z,
        )

        thrusters.thrusterControlAll(
            this.direction.clockWise.normal
                .toJOMLD(),
            // -seatedControllingPlayer?.leftImpulse!!.toDouble()
            -velTarget.z,
        )// OLD IMPE
*/
        val requestedThrust = mutableMapOf<Vector3d, Double>()

        if (velTarget.x > 0) {
            requestedThrust[
                this.direction.opposite.normal
                    .toJOMLD(),
            ] = velTarget.x
        } else if (velTarget.x < 0) {
            requestedThrust[this.direction.normal.toJOMLD()] = -velTarget.x
        }
        if (velTarget.y > 0) {
            requestedThrust[Direction.UP.normal.toJOMLD()] = velTarget.y
        } else if (velTarget.y < 0) {
            requestedThrust[Direction.DOWN.normal.toJOMLD()] = -velTarget.y
        }
        if (velTarget.z > 0) {
            requestedThrust[
                this.direction.counterClockWise.normal
                    .toJOMLD(),
            ] = velTarget.z
        } else if (velTarget.z < 0) {
            requestedThrust[
                this.direction.clockWise.normal
                    .toJOMLD(),
            ] = -velTarget.z
        }
        // HATE THIS but still better than old imple
        thrusters.assignThrustPerDirection(requestedThrust, true) // SHOULD WORK??

        if (seatedControllingPlayer!!.pitch.absoluteValue + seatedControllingPlayer!!.yaw.absoluteValue + seatedControllingPlayer!!.roll.absoluteValue != 0.0) {
            val sensitivity = 0.1
            val tmp = Quaterniond()
            tmp.fromAxisAngleRad(
                this.direction.clockWise.normal
                    .toJOMLD(),
                seatedControllingPlayer?.pitch!! * sensitivity,
            )
            rotTarget.mul(tmp)
            tmp.fromAxisAngleRad(this.direction.normal.toJOMLD(), seatedControllingPlayer?.roll!! * sensitivity)
            rotTarget.mul(tmp)
            tmp.fromAxisAngleRad(Vector3d(0.0, 1.0, 0.0), seatedControllingPlayer?.yaw!! * sensitivity)
            rotTarget.mul(tmp)
        }

        gyros.pointTowards(
            rotTarget,
            1.0,
        )
    }

    fun startRiding(
        player: Player,
        force: Boolean,
        blockPos: BlockPos,
        state: BlockState,
        level: ServerLevel,
    ): Boolean {
        for (i in seats.size - 1 downTo 0) {
            if (!seats[i].isVehicle) {
                seats[i].kill()
                seats.removeAt(i)
            } else if (!seats[i].isAlive) {
                seats.removeAt(i)
            }
        }

        val seat = spawnSeat(blockPos, blockState, level)
        // player.xRot = 0 F
        // player.yRot = 0 F FFFUCK "STYLE" ERRORS
        // Minecraft.getInstance().options.sensitivity = -1/3.0
        // GLFW.glfwSetInputMode(Minecraft.getInstance().getWindow().window, CURSOR, CURSOR_NORMAL)
        val ride = player.startRiding(seat, force)
        if (ride) {
            seats.add(seat)
        }

        return ride
    }

    fun enable() {
        // idc if its against your will or not, you WILL exist, and you WILL like it "Long sentences (45 words here) are harder to read, according to the research; consider splitting" added it just for spite
        // iiluc for love of god stop using "enable" as fricking "CHANGE STATE" working with it is an absolute nightmaree ALSO FUCK INTELIJ TRYIN TO CORRECT MY GRAMAR N SHIT
    }

    fun sit(
        player: Player,
        force: Boolean = false,
    ): Boolean {
        // If the player is already controlling the ship, open the helm menu
        if (!force && player.vehicle?.type == Kontraption.KONTRAPTION_SHIP_MOUNTING_ENTITY_TYPE && seats.contains(player.vehicle as KontraptionShipMountingEntity)) {
            return true
        }

        // val seat = spawnSeat(blockPos, blockState, level as ServerLevel)
        // control?.seatedPlayer = player
        // return player.startRiding(seat, force)

        return startRiding(player, force, blockPos, blockState, level as ServerLevel)
    }

    public fun getRotation(): Map<String, Double> =
        (
            mapOf(
                Pair("x", rotTarget.x()),
                Pair("y", rotTarget.y()),
                Pair("z", rotTarget.z()),
                Pair("w", rotTarget.w()),
            )
        )

    public fun getMovement(): Map<String, Double> =
        (
            mapOf(
                Pair("x", velTarget.x()),
                Pair("y", velTarget.y()),
                Pair("z", velTarget.z()),
            )
        )

    public fun getPosition(): Map<String, Double> {
        val a = ship!!.transform.positionInWorld
        return(
            mapOf(
                Pair("x", a.x()),
                Pair("y", a.y()),
                Pair("z", a.z()),
            )
        )
    }

    public fun getWeight(): Double {
        // TODO: i think using the peripheral while not on ship might crash the game because ship is null
        return(ship!!.inertiaData.mass)
    }

    public fun getSlug(): String = (ship!!.slug.toString())

    public fun getVelocity(): Map<String, Double> {
        val a = ship!!.velocity
        return(
            mapOf(
                Pair("x", a.x()),
                Pair("y", a.y()),
                Pair("z", a.z()),
            )
        )
    }

    public fun setMovement(
        x: Double,
        y: Double,
        z: Double,
    ) {
        velTarget = Vector3d(Math.max(-1.0, Math.min(1.0, x)), Math.max(-1.0, Math.min(1.0, y)), Math.max(-1.0, Math.min(1.0, z))) // "should be replaced with kotlin function" BLA BLA BLAH GO FU . . .
    }

    public fun setRotation(
        x: Double,
        y: Double,
        z: Double,
        w: Double,
    ) {
        if (abs(x * x + y * y + z * z + w * w - 1.0) < 0.01) {
            rotTarget = Quaterniond(x, y, z, w)
        } else {
            throw ComputerException(
                ("Invalid quaternion "..abs(x * x + y * y + z * z + w * w).toString()).toString(),
            ) // needs 2 tostrings for funny
        }
    }

    public fun rotateAlongAxis(
        x: Double,
        y: Double,
        z: Double,
    ) {
        val tmp = Quaterniond()
        tmp.fromAxisAngleRad(
            this.direction.clockWise.normal
                .toJOMLD(),
            z * 0.1,
        )
        rotTarget.mul(tmp)
        tmp.fromAxisAngleRad(this.direction.normal.toJOMLD(), x * 0.1)
        rotTarget.mul(tmp)
        tmp.fromAxisAngleRad(Vector3d(0.0, 1.0, 0.0), y * 0.1)
        rotTarget.mul(tmp)
    }
}
