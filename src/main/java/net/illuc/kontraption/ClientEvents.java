package net.illuc.kontraption;

import net.illuc.kontraption.client.ThrusterParticle;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;

import static net.illuc.kontraption.client.render.RendererKt.renderData;

public class ClientEvents {


    @Mod.EventBusSubscriber(modid = Kontraption.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {

        @SubscribeEvent
        public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
            System.out.println("bal");
            Minecraft.getInstance().particleEngine.register(KontraptionParticleTypes.INSTANCE.getTHRUSTER().get(), ThrusterParticle.Factory::new);
        }
    }

    @Mod.EventBusSubscriber(Dist.CLIENT)
    public static class ClientRuntimeEvents {

        @SubscribeEvent
        public static void onRenderWorld(RenderLevelStageEvent event) {
            if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
                var matrixStack = event.getPoseStack();
                var mainCamera = event.getCamera();

                Minecraft.getInstance().getProfiler().push("vsshipassembler_rendering_phase");
                renderData(matrixStack, mainCamera);
                Minecraft.getInstance().getProfiler().pop();
            }
        }

    }
}