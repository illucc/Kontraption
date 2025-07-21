package net.illuc.kontraption.gui

import net.illuc.kontraption.Kontraption
import net.illuc.kontraption.ship.KontraptionBConfigControl
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory

class ShipTerminalScreen(
    private val menz: ShipTerminalMenu,
    playerInventory: Inventory,
    title: Component,
) : AbstractContainerScreen<ShipTerminalMenu>(menz, playerInventory, title) {
    private val BACKGROUND_TEXTURE = ResourceLocation(Kontraption.MODID, "textures/gui/gui_tile.png")

    private val buttonMap = mutableListOf<Button>()
    private val textMap = mutableMapOf<Int, String>()
    private val scale = 0.33f
    private val gap = 4
    private val blockHeight = 24
    private val panelWidth = (imageWidth).toInt()
    private val panelHeight = (imageHeight * 1.25).toInt()
    val left = leftPos

    val lp = leftPos
    val tp = topPos

    private var selectedBlockInd: Int = 0 // should thiss be counted as 0 or 1  indexed?? do we want an ampty screen?

    override fun init() {
        super.init()
        buttonMap.clear() // jic
        this.inventoryLabelY = 10000
        /* this.imageWidth = 900
        this.imageHeight = 1026
        this.leftPos = (width - (imageWidth * scale).toInt()) / 2
        this.topPos = (height - (imageHeight * scale).toInt()) / 2 */
        // old scalable stuff
        val blocks = menz.configBlocks
        for ((i, block) in blocks.withIndex()) {
            val y = tp + i * (blockHeight + gap) + gap
            val posSting = "${block.pos.x},${block.pos.y},${block.pos.z}" // debb
            val btn =
                Button
                    .builder(Component.literal(posSting)) {
                        selectedBlockInd = i
                    }.bounds(lp + gap, y, panelWidth - 2 * gap, blockHeight)
                    .build()
            buttonMap.add(btn)
            addRenderableWidget(btn)
        }
    }

    override fun render(
        GG: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        partialTicks: Float,
    ) {
        renderBackground(GG)
        super.render(GG, mouseX, mouseY, partialTicks)
        renderBlockListPanel(GG, mouseX, mouseY)
        renderBlockSettingsPanel(GG, mouseX, mouseY)
        renderTooltip(GG, mouseX, mouseY)
    }

    private fun renderBlockListPanel(
        GG: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
    ) {
        val top = topPos

        GG.fill(left, top, left + panelWidth, top + panelHeight, 0xFF202028.toInt())
    }

    private fun renderBlockSettingsPanel(
        GG: GuiGraphics,
        mouseX: Int,
        mouseY: Int, // was used in debuggin of scl on other things
    ) {
        val leftcurr = left + 100
        val top = topPos
        val width = (imageWidth * 1.1).toInt()

        GG.fill(leftcurr, top, leftcurr + width, top + panelHeight, 0xFF181820.toInt())

        val block = menz.configBlocks.getOrNull(selectedBlockInd) ?: return

        val boxHeight = 28
        val gap = 8
        for ((i, setting) in block.settings.withIndex()) {
            val y = top + i * (boxHeight + gap) + 18
            val boxColor =
                when (setting) {
                    is KontraptionBConfigControl.BlockSetting.BooleanSetting -> 0xFF00BB00.toInt()
                    is KontraptionBConfigControl.BlockSetting.IntSetting -> 0xFF5694E3.toInt()
                    is KontraptionBConfigControl.BlockSetting.StringSetting -> 0xFFAA22DD.toInt()
                    is KontraptionBConfigControl.BlockSetting.TittleSetting -> 0xFFFFC401.toInt()
                }

            GG.fill(left + gap, y, left + width - gap, y + boxHeight, boxColor)
            GG.drawString(font, "${setting.name}: ${setting.displayValue}", left + gap + 6, y + 8, 0x222222)
        }
    }

    override fun renderBg(
        GG: GuiGraphics,
        partialTicks: Float,
        mouseX: Int,
        mouseY: Int,
    ) {
        /* RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE)
        renderBackground(GG)
        GG.pose().pushPose()
        GG.pose().scale(scale, scale, 1f)
        GG.blit(BACKGROUND_TEXTURE, (leftPos / scale).toInt(), (topPos / scale).toInt(), 0f, 0f, 900, 1026, 1374, 1026)
        GG.pose().popPose() */
    }
}
