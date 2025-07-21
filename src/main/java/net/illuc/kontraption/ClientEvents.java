package net.illuc.kontraption;

import mekanism.common.Mekanism;
import net.illuc.kontraption.client.MuzzleFlashParticle;
import net.illuc.kontraption.client.ThrusterParticle;
import net.illuc.kontraption.renderers.LargeIonRenderer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;

import java.util.logging.Logger;

import static net.illuc.kontraption.client.render.RendererKt.renderData;

public class ClientEvents {

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