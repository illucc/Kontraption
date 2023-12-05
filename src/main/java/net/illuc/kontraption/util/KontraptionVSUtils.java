package net.illuc.kontraption.util;


import com.mojang.math.Vector3d;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;
import org.valkyrienskies.mod.common.VSGameUtilsKt;



// BRAIN DAMAGE BRAIN DAMAGE BRAIN DAMAGE BRAIN DAMAGE BRAIN DAMAGE BRAIN DAMAGE BRAIN DAMAGE BRAIN DAMAGE BRAIN DAMAGE BRAIN DAMAGE BRAIN DAMAGE BRAIN DAMAGE BRAIN DAMAGE BRAIN DAMAGE BRAIN DAMAGE BRAIN DAMAGE
public class KontraptionVSUtils {
    public static org.valkyrienskies.core.api.ships.Ship getShipManagingPos(ServerLevel serverLevel, int chunkX, int chunkZ){
        return VSGameUtilsKt.getShipManagingPos(serverLevel, chunkX, chunkZ);
    }

    public static org.valkyrienskies.core.api.ships.Ship getShipManagingPos(Level level, BlockPos blockPos){
        return VSGameUtilsKt.getShipManagingPos(level, blockPos);
    }

    public static org.valkyrienskies.core.api.ships.ServerShip getShipManagingPos(ServerLevel serverLevel, BlockPos blockPos){
        return VSGameUtilsKt.getShipManagingPos(serverLevel, blockPos);
    }





    public static org.valkyrienskies.core.api.ships.ServerShip getShipObjectManagingPos(ServerLevel serverLevel, BlockPos blockPos){
        return VSGameUtilsKt.getShipObjectManagingPos(serverLevel, blockPos);
    }

    public static org.valkyrienskies.core.api.ships.Ship getShipObjectManagingPos(Level level, BlockPos blockPos){
        return VSGameUtilsKt.getShipObjectManagingPos(level, blockPos);
    }

    public static org.valkyrienskies.core.api.ships.Ship getShipObjectManagingPos(Level level, Vec3i blockPos){
        return VSGameUtilsKt.getShipObjectManagingPos(level, blockPos);
    }

    public static ServerShipWorldCore getShipObjectWorld(ServerLevel level){
        return VSGameUtilsKt.getShipObjectWorld(level);
    }
}
