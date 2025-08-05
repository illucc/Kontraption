package net.illuc.kontraption.gui

import com.mojang.blaze3d.systems.RenderSystem
import mekanism.common.Mekanism
import net.illuc.kontraption.Kontraption
import net.illuc.kontraption.network.to_server.PacketKontraptionScreen
import net.illuc.kontraption.util.guiutils.ScaledImageButton
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory

class ShipTerminalScreen(
    private val menz: ShipTerminalMenu,
    playerInventory: Inventory,
    title: Component,
) : AbstractContainerScreen<ShipTerminalMenu>(menz, playerInventory, title) {
    private val BACKGROUND_TEXTURE = ResourceLocation(Kontraption.MODID, "textures/gui/gui_tile.png")

    private val buttonMap = mutableMapOf<Int, ScaledImageButton>()
    private val textMap = mutableMapOf<Int, String>()
    private val scale = 0.33f

    override fun render(
        GG: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        partialTicks: Float,
    ) {
        renderTooltip(GG, mouseX, mouseY)
        super.render(GG, mouseX, mouseY, partialTicks)
    }

    override fun renderBg(
        GG: GuiGraphics,
        partialTicks: Float,
        mouseX: Int,
        mouseY: Int,
    ) {
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE)
        renderBackground(GG)
        GG.pose().pushPose()
        GG.pose().scale(scale, scale, 1f)
        GG.blit(BACKGROUND_TEXTURE, (leftPos / scale).toInt(), (topPos / scale).toInt(), 0f, 0f, 900, 1026, 1374, 1026)
        GG.pose().popPose()
    }

    private fun renderButtons() {
        buttonMap.clear() // yeet

        if (menz.keyStones.isEmpty()) {
            Mekanism.logger.error("List empty!")
        }

        // BUTTON SHEETS
        // Texture sheet size: 1374x1026
        // Big button unclicked:
        // Start: (1018, 83)
        // End: (1372, 186)
        // Big button clicked:
        // Start: (1018, 300)
        // End: (1372, 403)
        // Small button unclicked:
        // Start: (1018, 192)
        // End: (1264, 295)
        // Small button clicked:
        // Start: (1018, 409) (217)
        // End: (1264, 512) (217)
        menz.combinedList.forEachIndexed { index, pair ->

            val buttonX = leftPos + 41
            val buttonY = topPos + 27 + (index * 35)

            val IB =
                ScaledImageButton(
                    buttonX,
                    buttonY,
                    354,
                    103,
                    1018,
                    192,
                    217,
                    // its addition not absolute
                    BACKGROUND_TEXTURE,
                    1374,
                    1026,
                    {
                        Mekanism.logger.info("IB: ${pair.keyBind}, ID: $index")
                        pair.keyBind = if (pair.keyBind >= 6) 1 else pair.keyBind + 1
                        btCLICK(pair.keyBind, pair.blockPos)
                        updateButtonText(index, "KS: $index in: ${pair.keyBind}")
                    },
                    scale,
                    Component.literal("KS: $index in: ${pair.keyBind}"),
                )
            addRenderableWidget(IB)
            buttonMap[index] = IB
        }
    }

    private fun updateButtonText(
        buttonIndex: Int,
        newText: String,
    ) {
        val button = buttonMap[buttonIndex] ?: return
        button.message = Component.literal(newText) // i think da best way? wonder if i could have called just renderbutton from within
    }

    override fun init() {
        super.init()
        this.imageWidth = 900
        this.imageHeight = 1026
        this.leftPos = (width - (imageWidth * scale).toInt()) / 2
        this.topPos = (height - (imageHeight * scale).toInt()) / 2
        this.inventoryLabelY = 10000
        renderButtons()
    }

    private fun btCLICK(
        index: Int,
        blockPos: BlockPos,
    ) {
        Kontraption.packetHandler().sendToServer(
            PacketKontraptionScreen(index, blockPos),
        )
    }
}
