package net.illuc.kontraption.mixin;


import mekanism.common.lib.math.Pos3D;
import mekanism.common.tile.laser.TileEntityBasicLaser;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityBasicLaser.class)
public class MixinLaser {

    @Unique
    Direction a = Direction.UP;


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
