package net.illuc.kontraption.blockEntities

import mekanism.common.tile.base.TileEntityMekanism
import net.illuc.kontraption.KontraptionBlocks
import net.illuc.kontraption.controls.KontraptionSeatedControllingPlayer
import net.illuc.kontraption.entity.KontraptionShipMountingEntity
import net.illuc.kontraption.util.KontraptionVSUtils.getShipObjectManagingPos
import net.illuc.kontraption.util.toDoubles
import net.minecraft.commands.arguments.EntityAnchorArgument
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.ServerShip
import net.illuc.kontraption.Kontraption
import net.illuc.kontraption.ship.KontraptionShipControl
import net.illuc.kontraption.util.toJOML
import net.illuc.kontraption.util.toJOMLD
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.saveAttachment

class TileEntityPilotSeat(pos: BlockPos?, state: BlockState?) : TileEntityMekanism(KontraptionBlocks.PILOT_SEAT, pos, state){
    private val ship: ServerShip? get() = getShipObjectManagingPos((level as ServerLevel), this.blockPos)
    private var seatedControllingPlayer: KontraptionSeatedControllingPlayer? = null
    private val seats = mutableListOf<KontraptionShipMountingEntity>()

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


    fun tick(){
        //todo: fix everything
        //is this a good way to do this?
        seatedControllingPlayer = ship?.getAttachment(KontraptionSeatedControllingPlayer::class.java)
        //ship?.getAttachment(KontraptionSeatedControllingPlayer::class.java)?.let { println(it.forwardImpulse) }
        //println(ship?.getAttachment(KontraptionThrusterShipControl::class.java))
        if (seatedControllingPlayer != null) {
            this.direction
            //it looks so fucking bad
            //i am so sorry whoever is reading this
            val controller = ship?.getAttachment(KontraptionShipControl::class.java)
            //var bals = Direction.SOUTH.normal.(this.direction.toYRot().toInt()).toJOMLD()
            //println(bals)
            //Direction.SOUTH.normal.north()
                    //.rotateY(this.direction.toYRot().toDouble())
            //TODO: fix rotation being funky
            controller?.thrusterControlAll(Direction.SOUTH.normal.toJOMLD(),  seatedControllingPlayer?.forwardImpulse!!.toDouble())
            controller?.thrusterControlAll(Direction.NORTH.normal.toJOMLD(), -seatedControllingPlayer?.forwardImpulse!!.toDouble())
            controller?.thrusterControlAll(Direction.UP.normal.toJOMLD(),     seatedControllingPlayer?.upImpulse!!.toDouble())
            controller?.thrusterControlAll(Direction.DOWN.normal.toJOMLD(),  -seatedControllingPlayer?.upImpulse!!.toDouble())
            controller?.thrusterControlAll(Direction.WEST.normal.toJOMLD(),   seatedControllingPlayer?.leftImpulse!!.toDouble())
            controller?.thrusterControlAll(Direction.EAST.normal.toJOMLD(),  -seatedControllingPlayer?.leftImpulse!!.toDouble())

            controller?.gyroControlAll(Vector3d(seatedControllingPlayer?.roll!!.toDouble(), seatedControllingPlayer?.yaw!!.toDouble(), seatedControllingPlayer?.pitch!!.toDouble()), 1.0)

        }

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




}