package net.illuc.kontraption.blockEntities

import mekanism.common.tile.base.TileEntityMekanism
import net.illuc.kontraption.KontraptionBlocks
import net.illuc.kontraption.util.ShotHandler
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.item.PrimedTnt
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult

class TileEntityCannon(pos: BlockPos?, state: BlockState?) : TileEntityMekanism(KontraptionBlocks.CANNON, pos, state) {
    /*override fun onUpdateServer() {
        super.onUpdateServer()
        level?.let { ShotHandler.shoot(blockState.getValue(BlockStateProperties.FACING), it, blockPos,  {m -> hit(m) }) }
    }

    fun hit(clipResult: BlockHitResult){
        (level as ServerLevel).explode(PrimedTnt(EntityType.TNT, level), clipResult.blockPos.x.toDouble(), clipResult.blockPos.y.toDouble(), clipResult.blockPos.z.toDouble(), 5F, Explosion.BlockInteraction.DESTROY)
        if (level is ServerLevel) {
            for (player in (level as ServerLevel).players()) {
                (level as ServerLevel).sendParticles(player, ParticleTypes.EXPLOSION, true, clipResult.blockPos.x.toDouble()+0.5,clipResult.blockPos.y.toDouble()+0.5,clipResult.blockPos.z.toDouble()+0.5, 1, 0.001, 0.001, 0.001, 0.0)
                    //level.sendParticles(player, ParticleTypes.TOTEM_OF_UNDYING, true, blockPos.x.toDouble(), blockPos.x.toDouble(), blockPos.x.toDouble())
            }
        }
    }*/
}