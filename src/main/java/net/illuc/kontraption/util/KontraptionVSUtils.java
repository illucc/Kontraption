package net.illuc.kontraption.util;


import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.mod.common.VSGameUtilsKt;


// BRAIN DAMAGE BRAIN DAMAGE BRAIN DAMAGE BRAIN DAMAGE BRAIN DAMAGE BRAIN DAMAGE BRAIN DAMAGE BRAIN DAMAGE BRAIN DAMAGE BRAIN DAMAGE BRAIN DAMAGE BRAIN DAMAGE BRAIN DAMAGE BRAIN DAMAGE BRAIN DAMAGE BRAIN DAMAGE
public class KontraptionVSUtils {
    public static org.valkyrienskies.core.api.ships.Ship getShipManagingPos(ServerLevel serverLevel, int chunkX, int chunkZ){
        return VSGameUtilsKt.getShipManagingPos(serverLevel, chunkX, chunkZ);
    }

    public static org.valkyrienskies.core.api.ships.ServerShip getShipManagingPos(ServerLevel serverLevel, BlockPos blockPos){
        return VSGameUtilsKt.getShipManagingPos(serverLevel, blockPos);
    }

    public static org.valkyrienskies.core.api.ships.ServerShip getShipObjectManagingPos(ServerLevel serverLevel, BlockPos blockPos){
        return VSGameUtilsKt.getShipObjectManagingPos(serverLevel, blockPos);
    }
}
