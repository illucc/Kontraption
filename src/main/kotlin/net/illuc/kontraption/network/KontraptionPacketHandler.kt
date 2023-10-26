package net.illuc.kontraption.network

import mekanism.common.lib.Version
import mekanism.common.network.BasePacketHandler
import net.illuc.kontraption.Kontraption
import net.illuc.kontraption.network.to_server.PacketKontraptionDriving
import net.illuc.kontraption.network.to_server.PacketKontraptionGuiButtonPress
import net.illuc.kontraption.network.to_server.PacketKontraptionGuiInteract
import net.minecraftforge.network.simple.SimpleChannel


class KontraptionPacketHandler : BasePacketHandler() {
    private val netHandler = createChannel(Kontraption.rl(Kontraption.MODID), Version(0, 0, 1))
    override fun getChannel(): SimpleChannel {
        return netHandler
    }

    override fun initialize() {
        //Client to server messages
        registerClientToServer(PacketKontraptionGuiButtonPress::class.java, PacketKontraptionGuiButtonPress::decode)
        registerClientToServer(PacketKontraptionGuiInteract::class.java, PacketKontraptionGuiInteract::decode)
        registerClientToServer(PacketKontraptionDriving::class.java, PacketKontraptionDriving::decode)
        //Server to client messages
    }
}