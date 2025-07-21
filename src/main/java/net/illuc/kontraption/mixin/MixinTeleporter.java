package net.illuc.kontraption.mixin;


import mekanism.api.text.EnumColor;
import mekanism.client.render.tileentity.RenderTeleporter;
import mekanism.common.tile.TileEntityTeleporter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(RenderTeleporter.class)
public class MixinTeleporter {
    @Redirect(
            method = "render(Lmekanism/common/tile/TileEntityTeleporter;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/util/profiling/ProfilerFiller;)V",
            at = @At(value = "INVOKE", target = "Lmekanism/common/tile/TileEntityTeleporter;getColor()Lmekanism/api/text/EnumColor;")
    )
    private EnumColor redirectGetColor(TileEntityTeleporter tile) {
        return tile.getColor() == null ? EnumColor.AQUA : tile.getColor();
    }
}

