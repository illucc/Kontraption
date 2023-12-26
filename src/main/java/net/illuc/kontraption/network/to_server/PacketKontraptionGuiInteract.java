package net.illuc.kontraption.network.to_server;

import mekanism.api.functions.TriConsumer;
import mekanism.common.network.IMekanismPacket;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.WorldUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

/**
 * Used for informing the server that an action happened in a GUI
 */
public class PacketKontraptionGuiInteract implements IMekanismPacket {

    private final KontraptionGuiInteraction interaction;
    private final BlockPos tilePosition;
    private final double extra;

    public PacketKontraptionGuiInteract(KontraptionGuiInteraction interaction, BlockEntity tile) {
        this(interaction, tile.getBlockPos());
    }

    public PacketKontraptionGuiInteract(KontraptionGuiInteraction interaction, BlockEntity tile, double extra) {
        this(interaction, tile.getBlockPos(), extra);
    }

    public PacketKontraptionGuiInteract(KontraptionGuiInteraction interaction, BlockPos tilePosition) {
        this(interaction, tilePosition, 0);
    }

    public PacketKontraptionGuiInteract(KontraptionGuiInteraction interaction, BlockPos tilePosition, double extra) {
        this.interaction = interaction;
        this.tilePosition = tilePosition;
        this.extra = extra;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        Player player = context.getSender();
        if (player != null) {
            TileEntityMekanism tile = WorldUtils.getTileEntity(TileEntityMekanism.class, player.level(), tilePosition);
            if (tile != null) {
                interaction.consume(tile, player, extra);
            }
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(interaction);
        buffer.writeBlockPos(tilePosition);
        buffer.writeDouble(extra);
    }

    public static PacketKontraptionGuiInteract decode(FriendlyByteBuf buffer) {
        return new PacketKontraptionGuiInteract(buffer.readEnum(KontraptionGuiInteraction.class), buffer.readBlockPos(), buffer.readDouble());
    }

    public enum KontraptionGuiInteraction {
        ;

        private final TriConsumer<TileEntityMekanism, Player, Double> consumerForTile;

        KontraptionGuiInteraction(TriConsumer<TileEntityMekanism, Player, Double> consumerForTile) {
            this.consumerForTile = consumerForTile;
        }

        public void consume(TileEntityMekanism tile, Player player, double extra) {
            consumerForTile.accept(tile, player, extra);
        }
    }
}