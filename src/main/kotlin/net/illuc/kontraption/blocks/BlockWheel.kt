package net.illuc.kontraption.blocks

import mekanism.common.block.prefab.BlockTile
import mekanism.common.content.blocktype.BlockTypeTile
import net.illuc.kontraption.blockEntities.TileEntityWheel
import net.illuc.kontraption.util.KontraptionVSUtils
import net.illuc.kontraption.util.ShotHandler
import net.illuc.kontraption.util.toJOMLD
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.joml.Matrix3d
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.api.ships.properties.ShipInertiaData
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.apigame.physics.PhysicsEntityData
import org.valkyrienskies.core.impl.game.ships.ShipInertiaDataImpl
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING
import org.joml.Vector3dc
import org.valkyrienskies.core.apigame.physics.VSWheelCollisionShapeData
import org.valkyrienskies.core.impl.game.ships.ShipTransformImpl.Companion.create
import org.valkyrienskies.mod.common.ValkyrienSkiesMod

class BlockWheel(type: BlockTypeTile<TileEntityWheel?>?) : BlockTile<TileEntityWheel?, BlockTypeTile<TileEntityWheel?>?>(type) {
    @SuppressWarnings
    override fun onPlace(state: BlockState, world: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {



        /*val level = world as ServerLevel
        if (!world.isClientSide) {
            val entity = ValkyrienSkiesMod.PHYSICS_ENTITY_TYPE.create(level)!!
            val shipOn = KontraptionVSUtils.getShipManagingPos(level, pos)
            val offsetInLocal: Vector3dc = Direction.SOUTH.normal.toJOMLD().mul(0.25)

            val entityPos: Vector3dc = pos.toJOMLD() //if (shipOn != null) {
                //shipOn.transform.shipToWorld.transformPosition(pos as Vector3).add(offsetInGlobal)
            //} else {
                //ctx.clickLocation.toJOML().add(offsetInGlobal)
            //}

            val transform = create(entityPos, Vector3d())
            val shipId = KontraptionVSUtils.getShipObjectWorld(level).allocateShipId(level.dimension().toString())
            val physicsEntityData = createBasicSphereData(shipId, transform, 0.5)
            entity.setPhysicsEntityData(physicsEntityData)
            entity.setPos(entityPos.x(), entityPos.y(), entityPos.z())
            level.addFreshEntity(entity)
        }*/

    }

    companion object {
        fun createBasicSphereData(
                shipId: ShipId, transform: ShipTransform, radius: Double = 0.25, mass: Double = 10000.0
        ): PhysicsEntityData {
            val inertia = 0.4 * mass * radius * radius
            val inertiaData: ShipInertiaData = ShipInertiaDataImpl(Vector3d(), mass, Matrix3d().scale(inertia))
            val collisionShapeData = VSWheelCollisionShapeData(radius, 0.25)
            return PhysicsEntityData(
                    shipId = shipId,
                    transform = transform,
                    inertiaData = inertiaData,
                    linearVelocity = Vector3d(),
                    angularVelocity = Vector3d(),
                    collisionShapeData = collisionShapeData,
                    isStatic = false
            )
        }
    }

}