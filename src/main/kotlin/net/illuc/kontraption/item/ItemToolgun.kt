package net.illuc.kontraption.item


import mekanism.api.Action
import mekanism.api.AutomationType
import mekanism.client.render.MekanismRenderer
import mekanism.client.render.RenderResizableCuboid
import mekanism.common.Mekanism
import mekanism.common.config.MekanismConfig
import mekanism.common.item.ItemEnergized
import mekanism.common.registries.MekanismSounds
import mekanism.common.util.StorageUtils
import net.illuc.kontraption.config.KontraptionConfig
import net.illuc.kontraption.config.KontraptionConfigs
import net.illuc.kontraption.util.KontraptionVSUtils
import net.illuc.kontraption.util.KontraptionVSUtils.createNewShipWithBlocks
import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.TextComponent
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet
import javax.annotation.Nonnull
import kotlin.math.min
import kotlin.math.max



class ItemToolgun(properties: Properties) : ItemEnergized(KontraptionConfigs.kontraption.toolgunChargeRate, KontraptionConfigs.kontraption.toolgunStorage, properties.rarity(Rarity.UNCOMMON)) {

    //TODO: Figure out how to do rendering stuff

    var firstPosition: BlockPos? = null
    var secondPosition: BlockPos? = null

    @Nonnull
    override fun useOn(context: UseOnContext): InteractionResult {
        val player = context.player
        val world: Level = context.level
        context.player!!.playSound(MekanismSounds.BEEP.get(), 1F, 1F)
        if (!world.isClientSide && player != null) {
            val pos = context.clickedPos
            if (!player.isCreative) {
                val energyPerUse = KontraptionConfigs.kontraption.toolgunActionConsumption.get()
                val energyContainer = StorageUtils.getEnergyContainer(context.itemInHand, 0)
                if (energyContainer == null || energyContainer.extract(energyPerUse, Action.SIMULATE, AutomationType.MANUAL).smallerThan(energyPerUse)) {
                    return InteractionResult.FAIL
                }
                energyContainer.extract(energyPerUse, Action.EXECUTE, AutomationType.MANUAL)
            }
            makeSelection(pos, context)
            return InteractionResult.CONSUME
        }
        return InteractionResult.PASS
    }

    fun makeSelection(pos: BlockPos, context: UseOnContext){

        if (!context.level.isClientSide) {
        if (firstPosition == null){
            if (KontraptionVSUtils.getShipObjectManagingPos(context.level, pos) == null) {
                firstPosition = pos
                Mekanism.logger.info("first pos: $firstPosition")
                context.player?.sendMessage(TextComponent("First pos selected"), Util.NIL_UUID)
            } else{
                context.player?.sendMessage(TextComponent("Selected position is on a ship!"), Util.NIL_UUID)
            }
        }else if(secondPosition == null){
            if (KontraptionVSUtils.getShipObjectManagingPos(context.level, pos) == null) {
                secondPosition = pos
                Mekanism.logger.info("second pos: $secondPosition")
                context.player?.sendMessage(TextComponent("Second pos selected"), Util.NIL_UUID)
            } else{
                context.player?.sendMessage(TextComponent("Selected position is on a ship!"), Util.NIL_UUID)
            }

        }else{
            Mekanism.logger.info("now we do the assembly stuff")
            val set = DenseBlockPosSet()
            print(firstPosition)
            print(secondPosition)

            for (x in min(firstPosition!!.x, secondPosition!!.x)..max(firstPosition!!.x, secondPosition!!.x)) {
                for (y in min(firstPosition!!.y, secondPosition!!.y)..max(firstPosition!!.y, secondPosition!!.y)) {
                    for (z in min(firstPosition!!.z, secondPosition!!.z)..max(firstPosition!!.z, secondPosition!!.z)) {
                        set.add(x, y, z)
                    }
                }
            }

            val energyPerUse = KontraptionConfigs.kontraption.toolgunAssembleConsumption.get().multiply(set.size.toDouble())
            val energyContainer = StorageUtils.getEnergyContainer(context.itemInHand, 0)
            if (energyContainer == null || energyContainer.extract(energyPerUse, Action.SIMULATE, AutomationType.MANUAL).smallerThan(energyPerUse)) {
                if (energyContainer != null) {
                    Mekanism.logger.info("Assembly failed! Not enough energy, $energyPerUse needed but had ${energyContainer.energy}")
                    context.player?.sendMessage(TextComponent("Assembly failed! Not enough energy, $energyPerUse needed but had ${energyContainer.energy}"), Util.NIL_UUID)
                }
            }else{
                energyContainer.extract(energyPerUse, Action.EXECUTE, AutomationType.MANUAL)
                createNewShipWithBlocks(pos, set, (context.level as ServerLevel))
                context.player!!.playSound(MekanismSounds.BEEP.get(), 1F, 2F)
                context.player?.sendMessage(TextComponent("Assembled!"), Util.NIL_UUID)
            }
            firstPosition = null
            secondPosition = null


        }
        }
    }
}