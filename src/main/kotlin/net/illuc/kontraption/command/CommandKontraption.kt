package net.illuc.kontraption.command

import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import mekanism.api.MekanismAPI
import mekanism.api.text.EnumColor
import mekanism.common.MekanismLang
import mekanism.common.util.text.BooleanStateDisplay.OnOff
import net.illuc.kontraption.util.ShotHandler
import net.illuc.kontraption.util.toJOML
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.item.PrimedTnt
import net.minecraft.world.level.Explosion
import net.minecraft.world.phys.BlockHitResult
import org.joml.*
import java.util.*


object CommandKontraption {
    private val tpStack: MutableMap<UUID, Deque<BlockPos>> = Object2ObjectOpenHashMap()

    fun register(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal("kontraption")
                .then(SpawnRay.register())
    }

    private object SpawnRay {
        fun register(): ArgumentBuilder<CommandSourceStack, *> {
            return Commands.literal("spawnray")
                    .requires { cs: CommandSourceStack -> cs.hasPermission(2) }
                    .executes { ctx: CommandContext<CommandSourceStack> ->
                        //ctx.source.sendSuccess(MekanismLang.COMMAND_DEBUG.translateColored(EnumColor.GRAY, OnOff.of(MekanismAPI.debug, true)), true)

                        ShotHandler.shoot(ctx.source.entity!!.lookAngle.toJOML(), ctx.source.level, Vector3d(ctx.source.position.x,ctx.source.position.y,ctx.source.position.z), 200.0) { m -> hit(m, ctx) }
                        0
                    }
        }

        fun hit(clipResult: BlockHitResult, ctx: CommandContext<CommandSourceStack>){
            //todo yucky make it better
            ctx.source.level.explode(PrimedTnt(EntityType.TNT, ctx.source.level), clipResult.blockPos.x.toDouble(), clipResult.blockPos.y.toDouble(), clipResult.blockPos.z.toDouble(), 40F, Explosion.BlockInteraction.DESTROY)

            ctx.source.level.explode(PrimedTnt(EntityType.TNT, ctx.source.level), clipResult.blockPos.x.toDouble()+10, clipResult.blockPos.y.toDouble(), clipResult.blockPos.z.toDouble(), 10F, Explosion.BlockInteraction.DESTROY)
            ctx.source.level.explode(PrimedTnt(EntityType.TNT, ctx.source.level), clipResult.blockPos.x.toDouble()-10, clipResult.blockPos.y.toDouble(), clipResult.blockPos.z.toDouble(), 10F, Explosion.BlockInteraction.DESTROY)
            ctx.source.level.explode(PrimedTnt(EntityType.TNT, ctx.source.level), clipResult.blockPos.x.toDouble(), clipResult.blockPos.y.toDouble(), clipResult.blockPos.z.toDouble()+10, 10F, Explosion.BlockInteraction.DESTROY)
            ctx.source.level.explode(PrimedTnt(EntityType.TNT, ctx.source.level), clipResult.blockPos.x.toDouble(), clipResult.blockPos.y.toDouble(), clipResult.blockPos.z.toDouble()-10, 10F, Explosion.BlockInteraction.DESTROY)

            //ShotHandler.shoot(Vec3(ctx.source.entity!!.lookAngle.x+1,ctx.source.entity!!.lookAngle.y,ctx.source.entity!!.lookAngle.z), ctx.source.level, clipResult.blockPos.toDoubles(), 5.0) { m -> secondhit(m, ctx) }
            //ShotHandler.shoot(Vec3(ctx.source.entity!!.lookAngle.x-1,ctx.source.entity!!.lookAngle.y,ctx.source.entity!!.lookAngle.z), ctx.source.level, clipResult.blockPos.toDoubles(), 5.0) { m -> secondhit(m, ctx) }
            //ShotHandler.shoot(Vec3(ctx.source.entity!!.lookAngle.x,ctx.source.entity!!.lookAngle.y,ctx.source.entity!!.lookAngle.z+1), ctx.source.level, clipResult.blockPos.toDoubles(), 5.0) { m -> secondhit(m, ctx) }
            //ShotHandler.shoot(Vec3(ctx.source.entity!!.lookAngle.x,ctx.source.entity!!.lookAngle.y,ctx.source.entity!!.lookAngle.z-1), ctx.source.level, clipResult.blockPos.toDoubles(), 5.0) { m -> secondhit(m, ctx) }
            //ShotHandler.shoot(Vec3(ctx.source.entity!!.lookAngle.x,ctx.source.entity!!.lookAngle.y+1,ctx.source.entity!!.lookAngle.z), ctx.source.level, clipResult.blockPos.toDoubles(), 5.0) { m -> secondhit(m, ctx) }
            //ShotHandler.shoot(Vec3(ctx.source.entity!!.lookAngle.x,ctx.source.entity!!.lookAngle.y-1,ctx.source.entity!!.lookAngle.z), ctx.source.level, clipResult.blockPos.toDoubles(), 5.0) { m -> secondhit(m, ctx) }
            /*for (i in 0..359) {
                if (i % 20 == 0) {
                }
            }*/

        }

        fun secondhit(clipResult: BlockHitResult, ctx: CommandContext<CommandSourceStack>){
            ctx.source.level.explode(PrimedTnt(EntityType.TNT, ctx.source.level), clipResult.blockPos.x.toDouble(), clipResult.blockPos.y.toDouble(), clipResult.blockPos.z.toDouble(), 20F, Explosion.BlockInteraction.DESTROY)
        }


    }
}