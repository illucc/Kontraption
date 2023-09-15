package net.illuc.kontraption.blocks


import net.minecraft.commands.arguments.EntityAnchorArgument
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.material.Material
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.impl.util.x
import org.valkyrienskies.core.impl.util.y
import org.valkyrienskies.core.impl.util.z
import org.valkyrienskies.mod.common.ValkyrienSkiesMod
import org.valkyrienskies.*

class pilotSeatBlock :
        HorizontalDirectionalBlock(Properties.of(Material.WOOL)
                .strength(1.0f, 2.0f)
                .sound(SoundType.WOOL)){

    @Deprecated("Deprecated in Java")
    override fun use(
            state: BlockState,
            level: Level,
            pos: BlockPos,
            player: Player,
            hand: InteractionHand,
            blockHitResult: BlockHitResult
    ): InteractionResult {
        if (level.isClientSide) return InteractionResult.SUCCESS
        val seatEntity = ValkyrienSkiesMod.SHIP_MOUNTING_ENTITY_TYPE.create(level)!!.apply {
            val seatEntityPos: Vector3dc = Vector3d(pos.x + .5, pos.y.toDouble(), pos.z + .5)
            moveTo(seatEntityPos.x, seatEntityPos.y, seatEntityPos.z)
            //lookAt(EntityAnchorArgument.Anchor.EYES, state.getValue(FACING).normal.subtract.subtract(position()) )   //.toDoubles().add(position()))
            isController = true
        }

        level.addFreshEntity(seatEntity)
        player.startRiding(seatEntity)
        return InteractionResult.CONSUME
    }
}