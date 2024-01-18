package net.illuc.kontraption.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import mekanism.client.render.transmitter.RenderMechanicalPipe;
import mekanism.client.render.transmitter.RenderPressurizedTube;
import mekanism.common.content.network.BoxedChemicalNetwork;
import mekanism.common.content.network.FluidNetwork;
import mekanism.common.content.network.transmitter.MechanicalPipe;
import mekanism.common.tile.transmitter.TileEntityMechanicalPipe;
import mekanism.common.tile.transmitter.TileEntityPressurizedTube;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderMechanicalPipe.class)
public class MixinRenderMechanicalPipe {

    @Inject(method = "render(Lmekanism/common/tile/transmitter/TileEntityMechanicalPipe;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("HEAD"), cancellable = true)
    private void c(TileEntityMechanicalPipe tile, float partialTick, PoseStack matrix, MultiBufferSource renderer, int light, int overlayLight, ProfilerFiller profiler, CallbackInfo ci){
        MechanicalPipe pipe = tile.getTransmitter();
        FluidNetwork network = pipe.getTransmitterNetwork();

        if (network == null) {
            ci.cancel();
        }
    }
}
