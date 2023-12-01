package net.illuc.kontraption.blockEntities

import mekanism.common.block.attribute.Attribute.isActive
import mekanism.common.integration.computer.ComputerException
import mekanism.common.integration.computer.annotation.ComputerMethod
import mekanism.common.integration.computer.annotation.SyntheticComputerMethod
import mekanism.common.integration.computer.annotation.WrappingComputerMethod
import mekanism.common.inventory.container.sync.dynamic.ContainerSync
import mekanism.common.tile.base.TileEntityMekanism
import net.illuc.kontraption.Kontraption
import net.illuc.kontraption.KontraptionBlocks
import net.illuc.kontraption.controls.KontraptionSeatedControllingPlayer
import net.illuc.kontraption.entity.KontraptionShipMountingEntity
import net.illuc.kontraption.ship.KontraptionGyroControl
import net.illuc.kontraption.ship.KontraptionThrusterControl
import net.illuc.kontraption.util.KontraptionVSUtils.getShipObjectManagingPos
import net.illuc.kontraption.util.toDoubles
import net.illuc.kontraption.util.toJOMLD
import net.minecraft.commands.arguments.EntityAnchorArgument
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
import org.joml.Quaterniond
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.saveAttachment
import kotlin.math.absoluteValue


class TileEntityShipControlInterface(pos: BlockPos?, state: BlockState?) : TileEntityMekanism(KontraptionBlocks.SHIP_CONTROL_INTERFACE, pos, state){
    private val ship: ServerShip? get() = getShipObjectManagingPos((level as ServerLevel), this.blockPos)
    private var seatedControllingPlayer: KontraptionSeatedControllingPlayer? = null
    private val seats = mutableListOf<KontraptionShipMountingEntity>()

    private var rotTarget = Quaterniond()
    private var velTarget = Vector3d()

    fun spawnSeat(blockPos: BlockPos, state: BlockState, level: ServerLevel): KontraptionShipMountingEntity {
        val newPos = blockPos.relative(state.getValue(HorizontalDirectionalBlock.FACING))
        val newState = level.getBlockState(newPos)
        val newShape = newState.getShape(level, newPos)
        val newBlock = newState.block
        var height = 0.5
        if (!newState.isAir) {
            height = newShape.max(Direction.Axis.Y)
        }
        val entity = Kontraption.KONTRAPTION_SHIP_MOUNTING_ENTITY_TYPE.create(level)!!.apply {
            val seatEntityPos: Vector3dc = Vector3d(newPos.x + .5, (newPos.y - .5) + height, newPos.z + .5)
            moveTo(seatEntityPos.x(), seatEntityPos.y(), seatEntityPos.z())

            lookAt(
                    EntityAnchorArgument.Anchor.EYES,
                    state.getValue(HORIZONTAL_FACING).normal.toDoubles().add(position())
            )

            isController = true
        }

        ship?.saveAttachment<KontraptionSeatedControllingPlayer>(KontraptionSeatedControllingPlayer(Direction.SOUTH))
        level.addFreshEntityWithPassengers(entity)
        return entity
    }


    fun tick() {
        val ship = this.ship ?: return

        seatedControllingPlayer = ship.getAttachment(KontraptionSeatedControllingPlayer::class.java) ?: return
        if(seatedControllingPlayer!!.forwardImpulse.absoluteValue+seatedControllingPlayer!!.upImpulse.absoluteValue+seatedControllingPlayer!!.leftImpulse.absoluteValue != 0.0){
            velTarget = Vector3d(seatedControllingPlayer!!.forwardImpulse, seatedControllingPlayer!!.upImpulse, seatedControllingPlayer!!.leftImpulse)
        }

        val thrusters = KontraptionThrusterControl.getOrCreate(ship)
        val gyros = KontraptionGyroControl.getOrCreate(ship)

        thrusters.thrusterControlAll(
            this.direction.normal.toJOMLD(),
            //-seatedControllingPlayer?.forwardImpulse!!.toDouble()
            -velTarget.x
        )

        thrusters.thrusterControlAll(
            this.direction.opposite.normal.toJOMLD(),
            //seatedControllingPlayer?.forwardImpulse!!.toDouble(),
            velTarget.x
        )

        thrusters.thrusterControlAll(
            Direction.UP.normal.toJOMLD(),
            //seatedControllingPlayer?.upImpulse!!.toDouble()
            velTarget.y
        )

        thrusters.thrusterControlAll(
            Direction.DOWN.normal.toJOMLD(),
            //-seatedControllingPlayer?.upImpulse!!.toDouble()
            -velTarget.y
        )

        thrusters.thrusterControlAll(
            this.direction.counterClockWise.normal.toJOMLD(),
            //seatedControllingPlayer?.leftImpulse!!.toDouble()
            velTarget.z
        )

        thrusters.thrusterControlAll(
            this.direction.clockWise.normal.toJOMLD(),
           // -seatedControllingPlayer?.leftImpulse!!.toDouble()
           -velTarget.z
        )

        if(seatedControllingPlayer!!.pitch.absoluteValue+seatedControllingPlayer!!.yaw.absoluteValue+seatedControllingPlayer!!.roll.absoluteValue != 0.0){
            val sensitivity = 0.1
            val tmp = Quaterniond()
            tmp.fromAxisAngleRad(this.direction.clockWise.normal.toJOMLD(), seatedControllingPlayer?.pitch!!.toDouble() * sensitivity)
            rotTarget.mul(tmp)
            tmp.fromAxisAngleRad(this.direction.normal.toJOMLD(), seatedControllingPlayer?.roll!!.toDouble() * sensitivity)
            rotTarget.mul(tmp)
            tmp.fromAxisAngleRad(Vector3d(0.0, 1.0, 0.0), seatedControllingPlayer?.yaw!!.toDouble() * sensitivity)
            rotTarget.mul(tmp)
        }



        gyros.pointTowards(
            rotTarget,
            1.0
        )
    }

    fun startRiding(player: Player, force: Boolean, blockPos: BlockPos, state: BlockState, level: ServerLevel): Boolean {

        for (i in seats.size-1 downTo 0) {
            if (!seats[i].isVehicle) {
                seats[i].kill()
                seats.removeAt(i)
            } else if (!seats[i].isAlive) {
                seats.removeAt(i)
            }
        }

        val seat = spawnSeat(blockPos, blockState, level)
        //player.xRot = 0F
        //player.yRot = 0F
        //Minecraft.getInstance().options.sensitivity = -1/3.0
        //GLFW.glfwSetInputMode(Minecraft.getInstance().getWindow().window, CURSOR, CURSOR_NORMAL)
        val ride = player.startRiding(seat, force)
        if (ride) {
            seats.add(seat)
        }



        return ride
    }

    fun enable() {
        //idc if its against your will or not you WILL exist and you WILL like it

    }

    fun sit(player: Player, force: Boolean = false): Boolean {
        // If player is already controlling the ship, open the helm menu
        if (!force && player.vehicle?.type == Kontraption.KONTRAPTION_SHIP_MOUNTING_ENTITY_TYPE && seats.contains(player.vehicle as KontraptionShipMountingEntity))
        {
            return true
        }

        //val seat = spawnSeat(blockPos, blockState, level as ServerLevel)
        //control?.seatedPlayer = player
        //return player.startRiding(seat, force)

        return startRiding(player, force, blockPos, blockState, level as ServerLevel)

    }

    @ComputerMethod
    private fun getRotation(): Map<String, Double> {
        return(mapOf(
                Pair("x", rotTarget.x()),
                Pair("y", rotTarget.y()),
                Pair("z", rotTarget.z()),
                Pair("w", rotTarget.w())
        ))
    }
    @ComputerMethod
    private fun getMovement(): Map<String, Double> {
        return(mapOf(
                Pair("x", velTarget.x()),
                Pair("y", velTarget.y()),
                Pair("z", velTarget.z())
        ))
    }
    @ComputerMethod
    private fun setMovement(x: Double, y: Double, z: Double) {
        velTarget = Vector3d(x, y, z)
    }

    @ComputerMethod
    private fun setRotation(x: Double, y: Double, z: Double, w: Double) {
        rotTarget = Quaterniond(x, y, z, w)
    }

    @ComputerMethod
    private fun rotateAlongAxis(x: Double, y: Double, z: Double) {
        val tmp = Quaterniond()
        tmp.fromAxisAngleRad(this.direction.clockWise.normal.toJOMLD(), z * 0.1)
        rotTarget.mul(tmp)
        tmp.fromAxisAngleRad(this.direction.normal.toJOMLD(), x * 0.1)
        rotTarget.mul(tmp)
        tmp.fromAxisAngleRad(Vector3d(0.0, 1.0, 0.0), y * 0.1)
        rotTarget.mul(tmp)
    }
}