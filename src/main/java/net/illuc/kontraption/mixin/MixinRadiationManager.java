package net.illuc.kontraption.mixin;

import mekanism.api.Coord4D;
import mekanism.api.providers.IBlockProvider;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.lib.radiation.RadiationManager;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.interfaces.ITileWrapper;
import mekanism.common.tile.laser.TileEntityBasicLaser;
import net.illuc.kontraption.util.KontraptionVSUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;

import static org.valkyrienskies.mod.common.util.VectorConversionsMCKt.toJOML;
import static org.valkyrienskies.mod.common.util.VectorConversionsMCKt.toMinecraft;

@Mixin(RadiationManager.class)
public class MixinRadiationManager  {


    @ModifyVariable(remap = false, ordinal = 0, method = "radiate(Lmekanism/api/Coord4D;D)V", at = @At("HEAD"), argsOnly = true)
    private Coord4D MixinDumpRadiation(Coord4D a0) {
        ResourceKey<Level> resourceKey = a0.dimension;
        Mekanism.logger.info("testing the server doodad");
        Level level = ValkyrienSkiesMod.getCurrentServer().getLevel(resourceKey);
        Mekanism.logger.info("we mixining!");
        Ship ship = KontraptionVSUtils.getShipObjectManagingPos(level, a0.getPos());
        if (ship == null) {
            ship = KontraptionVSUtils.getShipManagingPos(level, a0.getPos());
        }
        if (ship == null){
            return a0;
        }else{
            return new Coord4D(new BlockPos(toMinecraft(VSGameUtilsKt.toWorldCoordinates(ship, a0.getPos()))), a0.dimension);
        }
    }
}
