package net.illuc.kontraption.gui

import net.illuc.kontraption.Kontraption
import net.illuc.kontraption.ship.KontraptionBConfigControl
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack
import net.minecraftforge.registries.ForgeRegistries

class ShipTerminalScreen(
    private val menz: ShipTerminalMenu,
    playerInventory: Inventory,
    title: Component,
) : AbstractContainerScreen<ShipTerminalMenu>(menz, playerInventory, title) {
    // COLOURS
    private val DARKBG = 0xFF0a0a12.toInt()
    private val PANELBG = 0xFF151522.toInt()
    private val PANELBORDER = 0xFF2a2a3a.toInt()
    private val ACCENTCOLOR = 0xFF7a3da5.toInt()
    private val TEXTCOLOR = 0xFFe0e0e8.toInt()
    private val TEXTDIM = 0xFFa0a0a8.toInt()

    // Sizings and offsets
    private val BLOCKHEIGHT = 24
    private val VERTICALGAP = 4
    private val SIDEGAP = 6
    private val ICONSIZE = 16
    private var RIGHTOFFET = 5
    private var TEXTPOSOFF = 12
    private val BGPADDING = 15
    private val BTNOFFSET = 20

    private val buttonMap = mutableListOf<Button>()
    private var selectedBlockInd = 0
    private val buttonBlockMap = mutableMapOf<Button, KontraptionBConfigControl.ConfigBlock>()

    override fun init() {
        super.init()
        this.imageWidth = 360
        this.imageHeight = 220
        this.leftPos = (width - imageWidth) / 2
        this.topPos = (height - imageHeight) / 2

        this.inventoryLabelY = 10000

        buttonMap.clear()
        children().removeIf { it is Button }

        val listPanelWidth = (imageWidth * 0.45).toInt()
        val blocks = menz.configBlocks

        for ((i, block) in blocks.withIndex()) {
            val y = topPos + i * (BLOCKHEIGHT + VERTICALGAP) + VERTICALGAP + BTNOFFSET
            val blockName = Component.translatable(block.blockId).string

            val buttonX = leftPos + SIDEGAP
            val buttonWidth = listPanelWidth - 2 * SIDEGAP

            val btn =
                Button
                    .builder(Component.literal(blockName)) {
                        selectedBlockInd = i
                    }.bounds(buttonX, y, buttonWidth, BLOCKHEIGHT)
                    .build()

            buttonMap.add(btn)
            buttonBlockMap[btn] = block
            addRenderableWidget(btn)
        }
    }

    override fun render(
        GG: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        partialTicks: Float,
    ) {
        val bgX1 = leftPos - BGPADDING
        val bgY1 = topPos - BGPADDING
        val bgX2 = leftPos + imageWidth + BGPADDING
        val bgY2 = topPos + imageHeight + BGPADDING

// Draw translucent black background just behind GUI area
        GG.fill(bgX1, bgY1, bgX2, bgY2, DARKBG)

        renderBlockListPanel(GG)
        renderBlockSettingsPanel(GG)

        super.render(GG, mouseX, mouseY, partialTicks) // Should be here i think?

        for (button in buttonMap) {
            val block = buttonBlockMap[button] ?: continue
            Kontraption.LOGGER.debug(block.blockId)
            val blockItem = ForgeRegistries.BLOCKS.getValue(ResourceLocation(block.blockId))?.asItem() ?: continue // NEVER WORKS, TIME FOR LE DEBBUGER
            Kontraption.LOGGER.debug(blockItem)
            val iconStack = ItemStack(blockItem)
            val iconX = button.x + 4
            val iconY = button.y + (button.height - ICONSIZE) / 2
            GG.renderItem(iconStack, iconX, iconY)

            if (button == buttonMap.getOrNull(selectedBlockInd)) {
                GG.fill(button.x, button.y, button.x + button.width, button.y + button.height, 0x667a3da5)
                GG.hLine(button.x, button.x + button.width - 1, button.y, ACCENTCOLOR)
                GG.hLine(button.x, button.x + button.width - 1, button.y + button.height - 1, ACCENTCOLOR)
                GG.vLine(button.x, button.y, button.y + button.height, ACCENTCOLOR)
                GG.vLine(button.x + button.width - 1, button.y, button.y + button.height, ACCENTCOLOR)
            }
        }

        renderTooltip(GG, mouseX, mouseY)
    }

    private fun renderBlockListPanel(GG: GuiGraphics) {
        // BLEH had to use debbugeer for it, i RLLY need to finish OFUPT
        val x = leftPos
        val y = topPos
        val w = (imageWidth * 0.45).toInt()
        val h = imageHeight

        GG.fill(x, y, x + w, y + h, PANELBG)

        val title = Component.literal("BLOCK LIST").withStyle(ChatFormatting.BOLD)
        GG.drawCenteredString(font, title, x + w / 2, y + 8, ACCENTCOLOR)
        GG.hLine(x, x + w - 1, y + 20, PANELBORDER)
    }

    private fun renderBlockSettingsPanel(GG: GuiGraphics) {
        val panelX = leftPos + (imageWidth * 0.45).toInt() + SIDEGAP
        val panelY = topPos
        val panelW = (imageWidth * 0.5).toInt()
        val panelH = imageHeight

        GG.fill(panelX, panelY, panelX + panelW, panelY + panelH, PANELBG)

        val title = Component.literal("BLOCK SETTINGS").withStyle(ChatFormatting.BOLD)
        GG.drawCenteredString(font, title, panelX + panelW / 2, panelY + 8, ACCENTCOLOR)
        GG.hLine(panelX, panelX + panelW - 1, panelY + 20, PANELBORDER)

        val block = menz.configBlocks.getOrNull(selectedBlockInd) ?: return

        // WORKS!!
        val blockName = Component.translatable(block.blockId).withStyle(ChatFormatting.BOLD)
        GG.drawString(font, blockName, panelX + SIDEGAP, panelY + 30, ACCENTCOLOR, false)

        // Egh
        val boxHeight = 28
        val gap = 8
        var settingY = panelY + 50

        for (setting in block.settings) {
            // BS Background, does this need to be in bg render?
            GG.fill(panelX + SIDEGAP, settingY, panelX + panelW - SIDEGAP, settingY + boxHeight, 0xFF2a2a3a.toInt())

            // BS name
            GG.drawString(font, setting.name, panelX + SIDEGAP + 4, settingY + TEXTPOSOFF, TEXTCOLOR, false)

            // Bs Value
            when (setting) {
                is KontraptionBConfigControl.BlockSetting.BooleanSetting -> {
                    val rightEdgeX = panelX + panelW - SIDEGAP - RIGHTOFFET
                    val toggleState = if (setting.value) "ON" else "OFF"
                    val toggleColor = if (setting.value) 0xFF00BB00.toInt() else 0xFFBB0000.toInt()
                    val textWidth = font.width(toggleState)
                    val startX = rightEdgeX - textWidth
                    GG.drawString(font, toggleState, startX, settingY + TEXTPOSOFF, toggleColor, false)
                }

                is KontraptionBConfigControl.BlockSetting.IntSetting -> {
                    val rightEdgeX = panelX + panelW - SIDEGAP - RIGHTOFFET
                    val valueText = setting.value.toString()
                    val textWidth = font.width(valueText)
                    val startX = rightEdgeX - textWidth
                    GG.drawString(font, valueText, startX, settingY + TEXTPOSOFF, 0xFF5694E3.toInt(), false)
                }
                // Right positioned

                is KontraptionBConfigControl.BlockSetting.StringSetting -> {
                    val rightEdgeX = panelX + panelW - SIDEGAP - RIGHTOFFET
                    val textWidth = font.width(setting.value)
                    val startX = rightEdgeX - textWidth
                    GG.drawString(font, setting.value, startX, settingY + TEXTPOSOFF, 0xFFAA22DD.toInt(), false)
                }
            }

            settingY += boxHeight + gap
        }

        // Save button
        val saveButton =
            Button
                .builder(Component.literal("SAVE SETTINGS")) {
                    // PACKET BS
                }.bounds(panelX + SIDEGAP, panelY + panelH - 30, 100, 20)
                .build()

        addRenderableWidget(saveButton)
    }

    override fun renderLabels(
        pGuiGraphics: GuiGraphics,
        pMouseX: Int,
        pMouseY: Int,
    ) {
        // no
    }

    override fun renderBg(
        GG: GuiGraphics,
        partialTicks: Float,
        mouseX: Int,
        mouseY: Int,
    ) {
        // We ball
    }
}
