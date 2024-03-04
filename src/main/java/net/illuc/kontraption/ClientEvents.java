package net.illuc.kontraption;

import net.illuc.kontraption.client.KontraptionClientTickHandler;
import net.illuc.kontraption.client.ThrusterParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEvents {


    @Mod.EventBusSubscriber(modid = Kontraption.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {

        @SubscribeEvent
        public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
            System.out.println("bal");
            Minecraft.getInstance().particleEngine.register(KontraptionParticleTypes.INSTANCE.getTHRUSTER().get(), ThrusterParticle.Factory::new);
        }



    }
}