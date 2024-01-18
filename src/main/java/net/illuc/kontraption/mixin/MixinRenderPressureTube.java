package net.illuc.kontraption.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import mekanism.client.render.transmitter.RenderPressurizedTube;
import mekanism.client.render.transmitter.RenderUniversalCable;
import mekanism.common.content.network.BoxedChemicalNetwork;
import mekanism.common.content.network.EnergyNetwork;
import mekanism.common.tile.transmitter.TileEntityPressurizedTube;
import mekanism.common.tile.transmitter.TileEntityUniversalCable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderPressurizedTube.class)
public class MixinRenderPressureTube {

    @Inject(method = "render(Lmekanism/common/tile/transmitter/TileEntityPressurizedTube;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("HEAD"), cancellable = true)
    private void c(TileEntityPressurizedTube tile, float partialTick, PoseStack matrix, MultiBufferSource renderer, int light, int overlayLight, ProfilerFiller profiler, CallbackInfo ci){
        BoxedChemicalNetwork network = tile.getTransmitter().getTransmitterNetwork();

        if (network == null) {
            ci.cancel();
        }
    }
}
