package net.illuc.kontraption.client

import mekanism.api.text.EnumColor
import mekanism.client.MekanismClient
import mekanism.common.base.HolidayManager
import mekanism.common.base.HolidayManager.YearlyDate
import net.illuc.kontraption.KontraptionLang
import net.minecraft.Util
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.player.Player
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.TickEvent.ClientTickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import java.time.Month


class KontraptionClientTickHandler {

    var notificationSent: Boolean = false
    val notificationType: String = "testing"
    val minecraft: Minecraft = Minecraft.getInstance()

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (event.phase == TickEvent.Phase.START) {
            tickStart()
        }
    }

    //maybe i should stop
    fun tickStart(){
        if (minecraft.level != null && minecraft.player != null && !minecraft.isPaused()) {
            if (!notificationSent && MekanismClient.ticksPassed % 5_12 == 0L) {
                if (notificationType != null) {
                    val message = if (notificationType == "testing") KontraptionLang.TESTINGNOTICE.translateColored(EnumColor.RED) else KontraptionLang.TESTINGNOTICE.translateColored(EnumColor.ORANGE)
                    minecraft.player!!.sendSystemMessage(KontraptionLang.NAMEBORDER.translateColored(EnumColor.ORANGE))
                    minecraft.player!!.sendSystemMessage(message)
                    minecraft.player!!.sendSystemMessage(KontraptionLang.BORDER.translateColored(EnumColor.ORANGE))
                }
                notificationSent = true
            }
        }
    }
}