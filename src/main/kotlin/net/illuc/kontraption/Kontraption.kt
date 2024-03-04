package net.illuc.kontraption

import mekanism.common.Mekanism
import mekanism.common.base.IModModule
import mekanism.common.config.MekanismModConfig
import mekanism.common.lib.Version
import mekanism.common.lib.multiblock.MultiblockCache
import mekanism.common.lib.multiblock.MultiblockManager
import net.illuc.kontraption.KontraptionParticleTypes.BULLET
import net.illuc.kontraption.KontraptionParticleTypes.THRUSTER
import net.illuc.kontraption.client.BulletParticle
import net.illuc.kontraption.client.KontraptionClientTickHandler
import net.illuc.kontraption.client.ThrusterParticle
import net.illuc.kontraption.command.CommandKontraption
import net.illuc.kontraption.config.KontraptionConfigs
import net.illuc.kontraption.config.KontraptionKeyBindings
import net.illuc.kontraption.entity.KontraptionShipMountingEntity
import net.illuc.kontraption.multiblocks.largeHydrogenThruster.LiquidFuelThrusterMultiblockData
import net.illuc.kontraption.multiblocks.largeHydrogenThruster.LiquidFuelThrusterValidator
import net.illuc.kontraption.network.KontraptionPacketHandler
import net.minecraft.client.Minecraft
import net.minecraft.client.particle.SpriteSet
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.EntityRenderersEvent
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent
import net.minecraftforge.client.event.RegisterParticleProvidersEvent
import net.minecraftforge.client.event.RegisterKeyMappingsEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.event.config.ModConfigEvent
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent
import net.minecraftforge.fml.loading.FMLEnvironment
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import org.valkyrienskies.mod.client.EmptyRenderer
import thedarkcolour.kotlinforforge.forge.MOD_BUS


/**
 * Main mod class. Should be an `object` declaration annotated with `@Mod`.
 * The modid should be declared in this object and should match the modId entry
 * in mods.toml.
 *
 * An example for blocks is in the `blocks` package of this mod.
 */

@Mod(Kontraption.MODID)
class Kontraption : IModModule {
    /**
     * Kontraption version number
     */
    val versionNumber: Version

    /**
     * Kontraption Packet Pipeline
     */
    private val packetHandler: KontraptionPacketHandler

    private val KONTRAPTION_SHIP_MOUNTING_ENTITY_REGISTRY: RegistryObject<EntityType<KontraptionShipMountingEntity>>
    private val ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Kontraption.MODID)



    // = TODO()


    init {
        instance = this
        //MekanismGeneratorsConfig.registerConfigs(ModLoadingContext.get())
        val modEventBus = MOD_BUS
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands)
        KontraptionConfigs.registerConfigs(ModLoadingContext.get());
        /*if(FMLEnvironment.dist.isClient){
            modEventBus.addListener(::registerKeyBindings)
        }*/
        modEventBus.addListener(this::commonSetup)
        modEventBus.addListener(this::onConfigLoad)
        modEventBus.addListener(this::imcQueue)
        KontraptionItems.ITEMS.register(modEventBus)
        KontraptionBlocks.BLOCKS.register(modEventBus)
        ENTITIES.register(modEventBus)
        KontraptionParticleTypes.PARTICLE_TYPES.register(modEventBus)
        //GeneratorsFluids.FLUIDS.register(modEventBus)
        //GeneratorsSounds.SOUND_EVENTS.register(modEventBus)
        KontraptionContainerTypes.CONTAINER_TYPES.register(modEventBus)
        KontraptionTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus)
        //GeneratorsGases.GASES.register(modEventBus)
        //GeneratorsModules.MODULES.register(modEventBus)
        //Set our version number to match the mods.toml file, which matches the one in our build.gradle
        versionNumber = Version(ModLoadingContext.get().activeContainer)
        packetHandler = KontraptionPacketHandler()




        KONTRAPTION_SHIP_MOUNTING_ENTITY_REGISTRY = ENTITIES.register("kontraption_ship_mounting_entity") {
            EntityType.Builder.of(
                    ::KontraptionShipMountingEntity,
                    MobCategory.MISC
            ).sized(.3f, .3f)
                    .build(ResourceLocation(Kontraption.MODID, "kontraption_ship_mounting_entity").toString())
        }


        modEventBus.addListener(::clientSetup)
        modEventBus.addListener(::entityRenderers)
        modEventBus.addListener(::loadComplete)


    }



    private fun commonSetup(event: FMLCommonSetupEvent) {
        //1mB hydrogen + 2*bioFuel/tick*200ticks/100mB * 20x efficiency bonus
        /*MekanismGases.ETHENE.get().addAttribute(Fuel(MekanismConfig.general.ETHENE_BURN_TIME,
                FloatingLongSupplier {
                    MekanismConfig.general.FROM_H2.get().add(MekanismGeneratorsConfig.generators.bioGeneration.get()
                            .multiply(2L * MekanismConfig.general.ETHENE_BURN_TIME.get()))
                }))*/
        event.enqueueWork {
            KontraptionTags.init();

            //Ensure our tags are all initialized
            //GeneratorTags.init()
            //Register dispenser behaviors
            //GeneratorsFluids.FLUIDS.registerBucketDispenserBehavior()
            //Register extended build commands (in enqueue as it is not thread safe)
            //BuildCommand.register("turbine", GeneratorsLang.TURBINE, TurbineBuilder())
            //BuildCommand.register("fission", GeneratorsLang.FISSION_REACTOR, FissionReactorBuilder())
            //BuildCommand.register("fusion", GeneratorsLang.FUSION_REACTOR, FusionReactorBuilder())
        }
        packetHandler.initialize()

        //Finalization
        Mekanism.logger.info("Loaded 'Kontraption' module.")
    }

    private fun imcQueue(event: InterModEnqueueEvent) {
        //MekanismIMC.addMekaSuitHelmetModules(GeneratorsModules.SOLAR_RECHARGING_UNIT)
        //MekanismIMC.addMekaSuitPantsModules(GeneratorsModules.GEOTHERMAL_GENERATOR_UNIT)
    }

    override fun getVersion(): Version {
        return versionNumber
    }

    override fun getName(): String {
        return "Kontraption"
    }

    override fun resetClient() {
        //TurbineMultiblockData.clientRotationMap.clear()
    }

    private fun loadComplete(event: FMLLoadCompleteEvent) {
        KONTRAPTION_SHIP_MOUNTING_ENTITY_TYPE = KONTRAPTION_SHIP_MOUNTING_ENTITY_REGISTRY.get()
    }

    private fun entityRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        event.registerEntityRenderer(KONTRAPTION_SHIP_MOUNTING_ENTITY_REGISTRY.get(), ::EmptyRenderer)
        //event.registerEntityRenderer(PHYSICS_ENTITY_TYPE_REGISTRY.get(), ::VSPhysicsEntityRenderer)
    }

    private fun clientSetup(event: FMLClientSetupEvent) {
        MinecraftForge.EVENT_BUS.register(this)
    }

    private fun registerKeyBindings(event: RegisterKeyMappingsEvent) {
        KontraptionKeyBindings.clientSetup {
            event.register(it)
        }
    }

    private fun registerCommands(event: RegisterCommandsEvent) {
        event.dispatcher.register(CommandKontraption.register())
    }

    private fun onConfigLoad(configEvent: ModConfigEvent) {
        //Note: We listen to both the initial load and the reload, to make sure that we fix any accidentally
        // cached values from calls before the initial loading
        val config = configEvent.config
        //Make sure it is for the same modid as us
        if (config.modId == MODID && config is MekanismModConfig) {
            config.clearCache(configEvent)
        }
    }

    companion object {
        lateinit var KONTRAPTION_SHIP_MOUNTING_ENTITY_TYPE: EntityType<KontraptionShipMountingEntity>
        const val MODID = "kontraption"
        var instance: Kontraption? = null
        //val turbineManager: MultiblockManager<TurbineMultiblockData> = MultiblockManager<TurbineMultiblockData>("industrialTurbine", Supplier<MultiblockCache<TurbineMultiblockData>> { TurbineCache() }, Supplier<IStructureValidator<TurbineMultiblockData>> { TurbineValidator() })
        //val fissionReactorManager: MultiblockManager<FissionReactorMultiblockData> = MultiblockManager<FissionReactorMultiblockData>("fissionReactor", Supplier<MultiblockCache<FissionReactorMultiblockData>> { FissionReactorCache() }, Supplier<IStructureValidator<FissionReactorMultiblockData>> { FissionReactorValidator() })
        //val fusionReactorManager: MultiblockManager<FusionReactorMultiblockData> = MultiblockManager<FusionReactorMultiblockData>("fusionReactor", Supplier<MultiblockCache<FusionReactorMultiblockData>> { FusionReactorCache() }, Supplier<IStructureValidator<FusionReactorMultiblockData>> { FusionReactorValidator() })
        val hydrogenThrusterManager: MultiblockManager<LiquidFuelThrusterMultiblockData?> = MultiblockManager("hydrogenThruster", { MultiblockCache<LiquidFuelThrusterMultiblockData?>() }, { LiquidFuelThrusterValidator() })

        fun packetHandler(): KontraptionPacketHandler {
            return instance!!.packetHandler
        }

        fun rl(path: String?): ResourceLocation {
            return ResourceLocation(MODID, path)
        }
    }


    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
    object ClientRegistryHandler {
        @SubscribeEvent
        fun onParticlesRegistry(e: RegisterParticleProvidersEvent?) {
            Minecraft.getInstance().particleEngine.register(THRUSTER.get()) { spriteSet: SpriteSet? -> ThrusterParticle.Factory(spriteSet) }
            //Minecraft.getInstance().particleEngine.register(BULLET.get()) { spriteSet: SpriteSet? -> BulletParticle.Factory(spriteSet) }
        }

        @SubscribeEvent
        fun init(event: FMLClientSetupEvent?) {
            MinecraftForge.EVENT_BUS.register(KontraptionClientTickHandler())
        }

        /*@SubscribeEvent
        fun init(event: FMLClientSetupEvent?) {
            OverlayRegistry.registerOverlayAbove(ForgeIngameGui.HOTBAR_ELEMENT, "Toolgun UI", ToolgunUI())
        }*/
    }


}