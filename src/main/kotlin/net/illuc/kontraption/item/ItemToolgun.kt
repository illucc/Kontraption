package net.illuc.kontraption.item


import mekanism.api.Action
import mekanism.api.AutomationType
import mekanism.api.NBTConstants
import mekanism.api.math.MathUtils
import mekanism.api.text.EnumColor
import mekanism.api.text.IHasTextComponent
import mekanism.api.text.ILangEntry
import mekanism.common.Mekanism
import mekanism.common.MekanismLang
import mekanism.common.item.ItemEnergized
import mekanism.common.item.gear.ItemFlamethrower.FlamethrowerMode
import mekanism.common.item.interfaces.IItemHUDProvider
import mekanism.common.item.interfaces.IModeItem
import mekanism.common.registries.MekanismSounds
import mekanism.common.util.ItemDataUtils
import mekanism.common.util.MekanismUtils
import mekanism.common.util.StorageUtils
import net.illuc.kontraption.KontraptionLang
import net.illuc.kontraption.config.KontraptionConfigs
import net.illuc.kontraption.util.*
import net.illuc.kontraption.util.KontraptionVSUtils.createNewShipWithBlocks
import net.minecraft.Util
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextComponent
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import org.joml.Vector3d
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet
import javax.annotation.Nonnull
import kotlin.math.max
import kotlin.math.min


class ItemToolgun(properties: Properties) : ItemEnergized(KontraptionConfigs.kontraption.toolgunChargeRate, KontraptionConfigs.kontraption.toolgunStorage, properties.rarity(Rarity.UNCOMMON)), IItemHUDProvider, IModeItem {

    //TODO: Figure out how to do rendering stuff

    var firstPosition: BlockPos? = null
    var secondPosition: BlockPos? = null

    override fun use(level: Level, player: Player, interactionHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val clipResult = level.clip(
                ClipContext(
                        (Vector3d(player.eyePosition.toJOML()).toMinecraft()),
                        (player.eyePosition.toJOML()
                                .add(0.5, 0.5, 0.5)
                                .add(Vector3d(player.lookAngle.toJOML()).mul(10.0)) //distance
                                ).toMinecraft(),
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        null
                )
        )
        //player.sendMessage(TextComponent(" ${clipResult.blockPos}"), Util.NIL_UUID)
        player.playSound(MekanismSounds.BEEP.get(), 1F, 1F)

        val pos = clipResult.blockPos
        if (!player.isCreative) {
            val energyPerUse = KontraptionConfigs.kontraption.toolgunActionConsumption.get()
            val energyContainer = StorageUtils.getEnergyContainer(player.getItemInHand(interactionHand), 0)
            if (energyContainer == null || energyContainer.extract(energyPerUse, Action.SIMULATE, AutomationType.MANUAL).smallerThan(energyPerUse)) {
                return super.use(level, player, interactionHand)
            }
            energyContainer.extract(energyPerUse, Action.EXECUTE, AutomationType.MANUAL)
        }
        if (getMode(player.getItemInHand(interactionHand)) == ToolgunMode.ASSEMBLE){
            makeSelection(level, player, interactionHand, pos)
        } else if (getMode(player.getItemInHand(interactionHand)) == ToolgunMode.MOVE){
            player.sendMessage(TextComponent("Work in progress :P"), Util.NIL_UUID)
        } else if (getMode(player.getItemInHand(interactionHand)) == ToolgunMode.LOCK){
            player.sendMessage(TextComponent("Work in progress :P"), Util.NIL_UUID)
        } else if (getMode(player.getItemInHand(interactionHand)) == ToolgunMode.PUSH){
            player.sendMessage(TextComponent("Work in progress :P"), Util.NIL_UUID)
        } else if (getMode(player.getItemInHand(interactionHand)) == ToolgunMode.ROTATE){
            player.sendMessage(TextComponent("Work in progress :P"), Util.NIL_UUID)
        }


        return super.use(level, player, interactionHand)
    }

    fun makeSelection(level: Level, player: Player, interactionHand: InteractionHand, pos: BlockPos){

        if (!level.isClientSide) {
            println(level.getBlockState(pos))
            if (player.isShiftKeyDown and (level.getBlockState(pos).isAir)) {
                firstPosition = null
                secondPosition = null
                player.sendMessage(TextComponent("Selection reset"), Util.NIL_UUID)
            } else if (firstPosition == null) {
                if (KontraptionVSUtils.getShipObjectManagingPos(level, pos) == null) {
                    firstPosition = pos
                    Mekanism.logger.info("first pos: $firstPosition")
                    player.sendMessage(TextComponent("First pos selected"), Util.NIL_UUID)
                } else {
                    player.sendMessage(TextComponent("Selected position is on a ship!"), Util.NIL_UUID)
                }
            } else if (secondPosition == null) {
                if (KontraptionVSUtils.getShipObjectManagingPos(level, pos) == null) {
                    secondPosition = pos
                    Mekanism.logger.info("second pos: $secondPosition")
                    player.sendMessage(TextComponent("Second pos selected"), Util.NIL_UUID)
                } else {
                    player.sendMessage(TextComponent("Selected position is on a ship!"), Util.NIL_UUID)
                }
            } else {
                Mekanism.logger.info("now we do the assembly stuff")
                val set = DenseBlockPosSet()
                print(firstPosition)
                print(secondPosition)

                for (x in min(firstPosition!!.x, secondPosition!!.x)..max(firstPosition!!.x, secondPosition!!.x)) {
                    for (y in min(firstPosition!!.y, secondPosition!!.y)..max(firstPosition!!.y, secondPosition!!.y)) {
                        for (z in min(firstPosition!!.z, secondPosition!!.z)..max(firstPosition!!.z, secondPosition!!.z)) {
                            if (!level.getBlockState(BlockPos(x, y, z)).isAir) {
                                set.add(x, y, z)
                            }
                        }
                    }
                }

                val energyPerUse = KontraptionConfigs.kontraption.toolgunAssembleConsumption.get().multiply(set.size.toDouble())
                val energyContainer = StorageUtils.getEnergyContainer(player.getItemInHand(interactionHand), 0)
                if (energyContainer == null || energyContainer.extract(energyPerUse, Action.SIMULATE, AutomationType.MANUAL).smallerThan(energyPerUse)) {
                    if (energyContainer != null) {
                        Mekanism.logger.info("Assembly failed! Not enough energy, $energyPerUse needed but had ${energyContainer.energy}")
                        player.sendMessage(TextComponent("Assembly failed! Not enough energy, $energyPerUse needed but had ${energyContainer.energy}"), Util.NIL_UUID)
                    }
                } else {
                    if (!set.isEmpty()) {
                        energyContainer.extract(energyPerUse, Action.EXECUTE, AutomationType.MANUAL)
                        createNewShipWithBlocks(pos, set, (level as ServerLevel))
                        player.playSound(MekanismSounds.BEEP.get(), 1F, 2F)
                        player.sendMessage(TextComponent("Assembled!"), Util.NIL_UUID)
                    }
                }
                firstPosition = null
                secondPosition = null
            }
        }
    }

    override fun changeMode(player: Player, stack: ItemStack, shift: Int, displayChangeMessage: Boolean) {
        val mode: ToolgunMode = getMode(stack)
        val newMode = mode.byIndex(mode.ordinal + shift)
        if (mode != newMode) {
            setMode(stack, newMode)
            if (displayChangeMessage) {
                player.sendMessage(MekanismUtils.logFormat(KontraptionLang.MODE_CHANGE.translate(newMode)), Util.NIL_UUID)
            }
        }
    }

    fun setMode(stack: ItemStack?, mode: ToolgunMode) {
        ItemDataUtils.setInt(stack, NBTConstants.MODE, mode.ordinal)
    }

    fun getMode(itemStack: ItemStack?): ToolgunMode {
        return ToolgunMode.byIndexStatic(ItemDataUtils.getInt(itemStack, NBTConstants.MODE))
    }


    enum class ToolgunMode(private val langEntry: ILangEntry, private val color: EnumColor) : IHasTextComponent {
        ASSEMBLE(KontraptionLang.ASSEMBLE, EnumColor.BRIGHT_GREEN) {
        },
        MOVE(KontraptionLang.MOVE, EnumColor.DARK_BLUE) {
        },
        LOCK(KontraptionLang.LOCK, EnumColor.RED) {
        },
        PUSH(KontraptionLang.PUSH, EnumColor.ORANGE) {
        },
        ROTATE(KontraptionLang.ROTATE, EnumColor.PURPLE) {
        };

        override fun getTextComponent(): Component {
            return langEntry.translateColored(color)
        }

        fun byIndex(index: Int): ToolgunMode {
            return byIndexStatic(index)
        }

        companion object {
            private val MODES = values()
            fun byIndexStatic(index: Int): ToolgunMode {
                return MathUtils.getByIndexMod(MODES, index)
            }
        }
    }


    override fun addHUDStrings(list: MutableList<Component>?, player: Player?, stack: ItemStack?, slotType: EquipmentSlot?) {
        if (list != null) {
            val mode: ToolgunMode = getMode(stack)
            //list.add(TextComponent("test"))
            list.add(MekanismLang.MODE.translateColored(EnumColor.GRAY, mode));
        };
    }
}