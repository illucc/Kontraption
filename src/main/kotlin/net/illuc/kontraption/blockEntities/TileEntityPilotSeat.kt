package net.illuc.kontraption.blockEntities

import mekanism.common.tile.base.TileEntityMekanism
import net.illuc.kontraption.KontraptionBlocks
import net.illuc.kontraption.controls.KontraptionSeatedControllingPlayer
import net.illuc.kontraption.entity.KontraptionShipMountingEntity
import net.illuc.kontraption.ship.KontraptionThrusterShipControl
import net.illuc.kontraption.util.KontraptionVSUtils.getShipObjectManagingPos
import net.illuc.kontraption.util.toDoubles
import net.minecraft.commands.arguments.EntityAnchorArgument
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
import net.minecraft.world.level.block.state.properties.Half
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.mod.api.SeatedControllingPlayer
import org.valkyrienskies.mod.common.ValkyrienSkiesMod
import org.valkyrienskies.mod.common.entity.ShipMountingEntity
import net.illuc.kontraption.Kontraption
import org.valkyrienskies.core.api.ships.getAttachment

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

        level.addFreshEntityWithPassengers(entity)
        return entity
    }


    fun tick(){
        //is this a good way to do this?
        seatedControllingPlayer = ship?.getAttachment(KontraptionSeatedControllingPlayer::class.java)
        println("balsl")
        println(ship?.getAttachment(KontraptionSeatedControllingPlayer::class.java))
        //println(ship?.getAttachment(KontraptionThrusterShipControl::class.java))
        if (seatedControllingPlayer != null) {
            ship?.getAttachment(KontraptionThrusterShipControl::class.java)?.controlAll(Vector3d(0.0, 1.0, 0.0), seatedControllingPlayer?.upImpulse!!.toDouble())
        }

        //seatedControllingPlayer?.upImpulse?.let { ship?.getAttachment(KontraptionThrusterShipControl::class.java)?.controlAll(Vector3d(0.0, 1.0, 0.0), it.toDouble()) }
        //check if it exists
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