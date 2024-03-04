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
import mekanism.common.item.interfaces.IItemHUDProvider
import mekanism.common.item.interfaces.IModeItem
import mekanism.common.registries.MekanismSounds
import mekanism.common.util.ItemDataUtils
import mekanism.common.util.StorageUtils
import net.illuc.kontraption.KontraptionLang
import net.illuc.kontraption.client.render.Renderer
import net.illuc.kontraption.client.render.RenderingData
import net.illuc.kontraption.client.render.SelectionZoneRenderer
import net.illuc.kontraption.config.KontraptionConfigs
import net.illuc.kontraption.util.*
import net.illuc.kontraption.util.KontraptionVSUtils.createNewShipWithBlocks
import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import org.joml.Vector3d
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet
import java.awt.Color
import kotlin.math.max
import kotlin.math.min


class ItemToolgun(properties: Properties) : ItemEnergized(KontraptionConfigs.kontraption.toolgunChargeRate, KontraptionConfigs.kontraption.toolgunStorage, properties.rarity(Rarity.UNCOMMON)), IItemHUDProvider, IModeItem {

    //TODO: Figure out how to do rendering stuff

    var firstPosition: BlockPos? = null
    var secondPosition: BlockPos? = null
    var SelectionZone: RenderingData? = null

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
            player.sendSystemMessage(Component.literal("Work in progress :P"))
        } else if (getMode(player.getItemInHand(interactionHand)) == ToolgunMode.LOCK){
            player.sendSystemMessage(Component.literal("Work in progress :P"))
        } else if (getMode(player.getItemInHand(interactionHand)) == ToolgunMode.PUSH){
            player.sendSystemMessage(Component.literal("Work in progress :P"))
        } else if (getMode(player.getItemInHand(interactionHand)) == ToolgunMode.ROTATE){
            player.sendSystemMessage(Component.literal("Work in progress :P"))
        }


        return super.use(level, player, interactionHand)
    }

    fun makeSelection(level: Level, player: Player, interactionHand: InteractionHand, pos: BlockPos){

        if (!level.isClientSide) {
            println(level.getBlockState(pos))

            // Selection reset if player is holding shift and looking at air
            if (player.isShiftKeyDown and (level.getBlockState(pos).isAir)) {
                firstPosition = null
                secondPosition = null
                if (SelectionZone!=null) Renderer.removeRender(SelectionZone!!)
                SelectionZone = null;
                player.sendSystemMessage(Component.literal("Selection reset"))

            // First pos selection
            } else if (firstPosition == null) {
                val res = raycast(level, player, ClipContext.Fluid.NONE)
                if (res!=null && KontraptionVSUtils.getShipObjectManagingPos(level, res.blockPos) == null ) {
                    firstPosition = res.blockPos
                    Mekanism.logger.info("first pos: $firstPosition")
                    player.sendSystemMessage(Component.literal("First pos selected"))
                } else {
                    player.sendSystemMessage(Component.literal("Selected position is on a ship!"))
                }


            // Second pos selection
            } else if (secondPosition == null) {
                val res = raycast(level, player, ClipContext.Fluid.NONE)
                if (res!=null && KontraptionVSUtils.getShipObjectManagingPos(level, res.blockPos) == null) {

                    if (SelectionZone!=null) Renderer.removeRender(SelectionZone!!)
                    SelectionZone = null;

                    secondPosition = res.blockPos
                    Mekanism.logger.info("second pos: $secondPosition")
                    player.sendSystemMessage(Component.literal("Second pos selected"))

                    val SZ = SelectionZoneRenderer(Vector3d(firstPosition!!.x.toDouble(),
                            firstPosition!!.y.toDouble(), firstPosition!!.z.toDouble()),Vector3d(res.blockPos.x.toDouble(),res.blockPos.y.toDouble(),res.blockPos.z.toDouble()), Color.GREEN);
                    SelectionZone = Renderer.addRender(SZ)

                } else {
                    player.sendSystemMessage(Component.literal("Selected position is on a ship!"))
                }

            // Assembly
            } else {
                player.playSound(MekanismSounds.BEEP.get(), 1F, 1.4F)
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
                        player.sendSystemMessage(Component.literal("Assembly failed! Not enough energy, $energyPerUse needed but had ${energyContainer.energy}"))
                    }
                } else {
                    if (!set.isEmpty()) {
                        energyContainer.extract(energyPerUse, Action.EXECUTE, AutomationType.MANUAL)
                        createNewShipWithBlocks(pos, set, (level as ServerLevel))
                        player.playSound(MekanismSounds.BEEP.get(), 1F, 2F)
                        player.sendSystemMessage(Component.literal("Assembled!"))
                    }
                }
                if (SelectionZone!=null) Renderer.removeRender(SelectionZone!!)
                SelectionZone = null;
                firstPosition = null
                secondPosition = null
            }
        }
    }

    @Override
    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        super.inventoryTick(stack, level, entity, slotId, isSelected)

        if (isSelected && secondPosition == null) {
            if (SelectionZone!=null) Renderer.removeRender(SelectionZone!!)
            SelectionZone = null
            val res = raycast(level, entity as Player, ClipContext.Fluid.NONE)
            if (res != null) {
                var otherPos = firstPosition
                if (otherPos==null)  otherPos=res.blockPos


                val SZ = SelectionZoneRenderer(
                        Vector3d(
                                otherPos!!.x.toDouble(),
                                otherPos.y.toDouble(), otherPos.z.toDouble()
                        ),
                        Vector3d(res.blockPos.x.toDouble(), res.blockPos.y.toDouble(), res.blockPos.z.toDouble()),
                        Color.ORANGE
                );
                SelectionZone = Renderer.addRender(SZ)

            }
        } else if (isSelected==false && secondPosition == null && SelectionZone!=null) {
            Renderer.removeRender(SelectionZone!!)
            SelectionZone = null
        }

    }

    protected fun raycast(level: Level, player: Player, fluidMode: ClipContext.Fluid?): BlockHitResult? {
        val f = player.xRot
        val g = player.yRot
        val vec3 = player.eyePosition
        val h = Mth.cos(-g * 0.017453292f - 3.1415927f)
        val i = Mth.sin(-g * 0.017453292f - 3.1415927f)
        val j = -Mth.cos(-f * 0.017453292f)
        val k = Mth.sin(-f * 0.017453292f)
        val l = i * j
        val n = h * j
        val d = 5.0
        val vec32 = vec3.add(l.toDouble() * 5.0, k.toDouble() * 5.0, n.toDouble() * 5.0)
        return level.clip(ClipContext(vec3, vec32, ClipContext.Block.OUTLINE, fluidMode, player))
    }

    override fun changeMode(player: Player, stack: ItemStack, shift: Int, displayChange: IModeItem.DisplayChange?) {
        val mode: ToolgunMode = getMode(stack)
        val newMode = mode.byIndex(mode.ordinal + shift)
        if (mode != newMode) {
            setMode(stack, newMode)
            displayChange?.sendMessage(player) {KontraptionLang.MODE_CHANGE.translate(newMode)}
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
        },
        WELD(KontraptionLang.WELD, EnumColor.YELLOW) {
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