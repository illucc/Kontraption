package net.illuc.kontraption

import mekanism.common.Mekanism;
import mekanism.common.base.IModModule;
import mekanism.common.config.MekanismModConfig;
import mekanism.common.lib.Version;
import net.illuc.kontraption.config.KontraptionKeyBindings
import net.illuc.kontraption.config.KontraptionKeyBindings.clientSetup
import net.illuc.kontraption.entity.KontraptionShipMountingEntity
import net.illuc.kontraption.network.KontraptionPacketHandler
import net.illuc.kontraption.network.KontraptionVSGamePackets
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraftforge.client.ClientRegistry
import net.minecraftforge.client.event.EntityRenderersEvent
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import org.valkyrienskies.mod.client.EmptyRenderer
import thedarkcolour.kotlinforforge.forge.*


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
     * MekanismGenerators version number
     */
    val versionNumber: Version

    /**
     * Mekanism Generators Packet Pipeline
     */
    private val packetHandler: KontraptionPacketHandler

    private val KONTRAPTION_SHIP_MOUNTING_ENTITY_REGISTRY: RegistryObject<EntityType<KontraptionShipMountingEntity>>
    private val ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, Kontraption.MODID)

    init {



        //Mekanism.addModule(also { instance = it })
        //MekanismGeneratorsConfig.registerConfigs(ModLoadingContext.get())


        val modEventBus = MOD_BUS
        modEventBus.addListener { event: FMLCommonSetupEvent -> commonSetup(event) }
        modEventBus.addListener { configEvent: ModConfigEvent -> onConfigLoad(configEvent) }
        modEventBus.addListener { event: InterModEnqueueEvent -> imcQueue(event) }
        KontraptionItems.ITEMS.register(modEventBus)
        KontraptionBlocks.BLOCKS.register(modEventBus)
        ENTITIES.register(modEventBus)
        KontraptionContainerTypes.CONTAINER_TYPES.register(modEventBus)
        KontraptionTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus)
        KontraptionVSGamePackets.register()
        KontraptionVSGamePackets.registerHandlers()



        modEventBus.addListener(::clientSetup)
        modEventBus.addListener(::entityRenderers)
        modEventBus.addListener(::loadComplete)



        /*GeneratorsFluids.FLUIDS.register(modEventBus)
        GeneratorsSounds.SOUND_EVENTS.register(modEventBus)
        GeneratorsGases.GASES.register(modEventBus)
        GeneratorsModules.MODULES.register(modEventBus)*/
        //Set our version number to match the mods.toml file, which matches the one in our build.gradle
        versionNumber =Version(0, 0, 1)
        packetHandler = KontraptionPacketHandler()

        KONTRAPTION_SHIP_MOUNTING_ENTITY_REGISTRY = ENTITIES.register("kontraption_ship_mounting_entity") {
            EntityType.Builder.of(
                    ::KontraptionShipMountingEntity,
                    MobCategory.MISC
            ).sized(.3f, .3f)
                    .build(ResourceLocation(Kontraption.MODID, "kontraption_ship_mounting_entity").toString())
        }

    }

    private fun commonSetup(event: FMLCommonSetupEvent) {
        //1mB hydrogen + 2*bioFuel/tick*200ticks/100mB * 20x efficiency bonus
        /*MekanismGases.ETHENE.get().addAttribute(Fuel(MekanismConfig.general.ETHENE_BURN_TIME,
                FloatingLongSupplier {
                    MekanismConfig.general.FROM_H2.get().add(MekanismGeneratorsConfig.generators.bioGeneration.get()
                            .multiply(2L * MekanismConfig.general.ETHENE_BURN_TIME.get()))
                }))*/
        event.enqueueWork {

            //Ensure our tags are all initialized
            //KontraptionTags.init()
            //Register dispenser behaviors
            //GeneratorsFluids.FLUIDS.registerBucketDispenserBehavior()
            //Register extended build commands (in enqueue as it is not thread safe)
            /*BuildCommand.register("turbine", GeneratorsLang.TURBINE, TurbineBuilder())
            BuildCommand.register("fission", GeneratorsLang.FISSION_REACTOR, FissionReactorBuilder())
            BuildCommand.register("fusion", GeneratorsLang.FUSION_REACTOR, FusionReactorBuilder())*/
        }
        packetHandler.initialize()

        //Finalization
        Mekanism.logger.info("Loaded 'Mekanism Kontraption' module.")
    }

    private fun imcQueue(event: InterModEnqueueEvent) {
        //MekanismIMC.addMekaSuitHelmetModules(GeneratorsModules.SOLAR_RECHARGING_UNIT)
        //MekanismIMC.addMekaSuitPantsModules(GeneratorsModules.GEOTHERMAL_GENERATOR_UNIT)
    }

    override fun getVersion(): Version {
        return versionNumber
    }

    private fun clientSetup(event: FMLClientSetupEvent) {
        KontraptionKeyBindings.clientSetup {
            ClientRegistry.registerKeyBinding(it)
        }
    }

    private fun entityRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        event.registerEntityRenderer(KONTRAPTION_SHIP_MOUNTING_ENTITY_REGISTRY.get(), ::EmptyRenderer)
        //event.registerEntityRenderer(PHYSICS_ENTITY_TYPE_REGISTRY.get(), ::VSPhysicsEntityRenderer)
    }




    override fun getName(): String {
        return "Kontraption"
    }

    override fun resetClient() {
        //TurbineMultiblockData.clientRotationMap.clear()
    }

    private fun onConfigLoad(configEvent: ModConfigEvent) {
        //Note: We listen to both the initial load and the reload, to make sure that we fix any accidentally
        // cached values from calls before the initial loading
        val config = configEvent.config
        //Make sure it is for the same modid as us
        if (config.modId == MODID && config is MekanismModConfig) {
            config.clearCache()
        }
    }

    private fun loadComplete(event: FMLLoadCompleteEvent) {
        KONTRAPTION_SHIP_MOUNTING_ENTITY_TYPE = KONTRAPTION_SHIP_MOUNTING_ENTITY_REGISTRY.get()
    }


        companion object {

         lateinit var KONTRAPTION_SHIP_MOUNTING_ENTITY_TYPE: EntityType<KontraptionShipMountingEntity>
        const val MODID = "kontraption"
        var instance: Kontraption? = null


        //val turbineManager: MultiblockManager<TurbineMultiblockData> = MultiblockManager<TurbineMultiblockData>("industrialTurbine", Supplier<MultiblockCache<TurbineMultiblockData>> { TurbineCache() }, Supplier<IStructureValidator<TurbineMultiblockData>> { TurbineValidator() })
        //val fissionReactorManager: MultiblockManager<FissionReactorMultiblockData> = MultiblockManager<FissionReactorMultiblockData>("fissionReactor", Supplier<MultiblockCache<FissionReactorMultiblockData>> { FissionReactorCache() }, Supplier<IStructureValidator<FissionReactorMultiblockData>> { FissionReactorValidator() })
        //val fusionReactorManager: MultiblockManager<FusionReactorMultiblockData> = MultiblockManager<FusionReactorMultiblockData>("fusionReactor", Supplier<MultiblockCache<FusionReactorMultiblockData>> { FusionReactorCache() }, Supplier<IStructureValidator<FusionReactorMultiblockData>> { FusionReactorValidator() })
        fun packetHandler(): KontraptionPacketHandler {
            return instance!!.packetHandler
        }

        fun rl(path: String?): ResourceLocation {
            return ResourceLocation(MODID, path)
        }
    }
}