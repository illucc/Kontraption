package net.illuc.kontraption.blocks


import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Material
import net.minecraft.world.phys.BlockHitResult
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.impl.util.x
import org.valkyrienskies.core.impl.util.y
import org.valkyrienskies.core.impl.util.z
import org.valkyrienskies.mod.common.ValkyrienSkiesMod

class PilotSeatBlock :
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
            //player.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3(1.0, 0.0, 0.0))
            player.xRot = 0F
            player.yRot = 0F
            Minecraft.getInstance().options.sensitivity = -1/3.0;
            isController = true
        }

        level.addFreshEntity(seatEntity)
        player.startRiding(seatEntity)
        return InteractionResult.CONSUME
    }
}