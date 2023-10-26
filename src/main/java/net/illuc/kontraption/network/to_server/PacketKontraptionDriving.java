package net.illuc.kontraption.network.to_server;

import mekanism.common.network.IMekanismPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class PacketKontraptionDriving implements IMekanismPacket {
    private final Vector3dc impulse;
    private final Vector3dc rotation;

    public PacketKontraptionDriving(Double impx, Double impy, Double impz, Double rotx, Double roty, Double rotz) {
        this.impulse = new Vector3d(impx, impy, impz);
        this.rotation = new Vector3d(rotx, roty, rotz);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        Player player = context.getSender();
        if (player != null) {
            System.out.println("blehh");

        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeDouble(impulse.x());
        buffer.writeDouble(impulse.y());
        buffer.writeDouble(impulse.z());
        buffer.writeDouble(rotation.x());
        buffer.writeDouble(rotation.y());
        buffer.writeDouble(rotation.z());
    }

    public static PacketKontraptionDriving decode(FriendlyByteBuf buffer) {
        return new PacketKontraptionDriving(buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

}
