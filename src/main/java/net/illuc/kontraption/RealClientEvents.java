package net.illuc.kontraption;

import net.illuc.kontraption.client.MuzzleFlashParticle;
import net.illuc.kontraption.client.ThrusterParticle;
import net.illuc.kontraption.renderers.LargeIonRenderer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



@Mod.EventBusSubscriber(modid = Kontraption.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RealClientEvents {
    static Logger logger = LogManager.getLogger(RealClientEvents.class);
    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        logger.info("[TEST] BAL UWU");
        Minecraft.getInstance().particleEngine.register(KontraptionParticleTypes.INSTANCE.getTHRUSTER().get(), ThrusterParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(KontraptionParticleTypes.INSTANCE.getMUZZLE_FLASH().get(), MuzzleFlashParticle.Factory::new);
    }

}
