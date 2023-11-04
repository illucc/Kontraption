package net.illuc.kontraption.mixin;


import mekanism.common.lib.math.Pos3D;
import mekanism.common.lib.math.Quaternion;
import mekanism.common.tile.laser.TileEntityBasicLaser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.Ship;

import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.Objects;

import static net.minecraft.world.level.block.DirectionalBlock.FACING;
import static org.valkyrienskies.mod.common.util.VectorConversionsMCKt.*;

@Mixin(TileEntityBasicLaser.class)
public class MixinLaser {


    @ModifyVariable(method = "onUpdateServer()V", at = @At("STORE"), ordinal = 0, remap = false)
    private Pos3D MixinPos3DFrom(Pos3D pos3D) {
        TileEntityBasicLaser te = ((TileEntityBasicLaser)(Object)this);
        Ship ship = VSGameUtilsKt.getShipManagingPos(te.getLevel(), te.getBlockPos());
        if (ship != null){
            return pos3D;
            //return Pos3D.create((toMinecraft(VSGameUtilsKt.toWorldCoordinates(ship, te.getBlockPos())))).centre();
            //ship.getShipToWorld().getNormalizedRotation(new Quaterniond())
        }
        return pos3D;
    }


    @ModifyVariable(method = "onUpdateServer()V", at = @At("STORE"), ordinal = 1, remap = false)
    private Pos3D MixinPos3DTo(Pos3D pos3D) {
        TileEntityBasicLaser te = ((TileEntityBasicLaser)(Object)this);
        Ship ship = VSGameUtilsKt.getShipManagingPos(te.getLevel(), te.getBlockPos());
        Vector3d worldDirection = toJOML(Vec3.atLowerCornerOf(te.getDirection().getNormal()));
        Vector3d targetDirection = toJOML(Vec3.atLowerCornerOf(te.getBlockState().getValue(FACING).getNormal()));
        if (ship != null) {
            ship.getShipToWorld().transformDirection(worldDirection, worldDirection);
            return pos3D.rotate((float) worldDirection.y, (float) worldDirection.x, (float) worldDirection.z);
        }


        /*if (ship != null){
            Vector3d quatd = ship.getShipToWorld().getNormalizedRotation(new Quaterniond()).getEulerAnglesXYZ(new Vector3d());
            return pos3D.rotate((float) quatd.y, (float) quatd.x, (float) quatd.z); //ship.getShipToWorld().getNormalizedRotation(new Quaterniond())
        }*/

        return pos3D;
    }



    /*@ModifyVariable(method = "onUpdateServer", at = @At(value = "STORE"), name = "direction")
    private static Direction injectDirection(Direction value) {
        direction = value;
        return value;
    }*/



    /*@ModifyVariable(method = "onUpdateServer", at = @At("STORE"), remap = false)
    private Direction myMethod2(Direction value) {
        Direction direction = Direction.UP;
        return direction;
    }*/
    /*@Inject(method = "onUpdateServer", at = @At("HEAD"), remap = false)
    private void modifyDirection(CallbackInfo ci) {
        Direction direction = Direction.UP;
    }*/
    /*
    @ModifyVariable(method = "onUpdateServer", at = @At("HEAD"), ordinal = 0, remap = false)
    private Direction direction() {
        return Direction.UP;
    }
*/


}
