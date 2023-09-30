package net.illuc.kontraption

import mekanism.api.MekanismIMC;
import mekanism.api.chemical.gas.attribute.GasAttributes.Fuel;
import mekanism.api.math.FloatingLongSupplier
import mekanism.common.Mekanism;
import mekanism.common.base.IModModule;
import mekanism.common.command.builders.BuildCommand;
import mekanism.common.config.MekanismConfig;
import mekanism.common.config.MekanismModConfig;
import mekanism.common.lib.Version;
import mekanism.common.lib.multiblock.MultiblockManager;
import mekanism.common.registries.MekanismGases;
import net.illuc.kontraption.network.KontraptionPacketHandler
import net.illuc.kontraption.network.to_server.KontraptionBlocks
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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

    init {
        //Mekanism.addModule(also { instance = it })
        //MekanismGeneratorsConfig.registerConfigs(ModLoadingContext.get())
        val modEventBus = MOD_BUS
        modEventBus.addListener { event: FMLCommonSetupEvent -> commonSetup(event) }
        modEventBus.addListener { configEvent: ModConfigEvent -> onConfigLoad(configEvent) }
        modEventBus.addListener { event: InterModEnqueueEvent -> imcQueue(event) }
        KontraptionItems.ITEMS.register(modEventBus)
        KontraptionBlocks.BLOCKS.register(modEventBus)
        KontraptionContainerTypes.CONTAINER_TYPES.register(modEventBus)
        KontraptionTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus)
        /*GeneratorsFluids.FLUIDS.register(modEventBus)
        GeneratorsSounds.SOUND_EVENTS.register(modEventBus)
        GeneratorsGases.GASES.register(modEventBus)
        GeneratorsModules.MODULES.register(modEventBus)*/
        //Set our version number to match the mods.toml file, which matches the one in our build.gradle
        versionNumber =Version(0, 0, 1)
        packetHandler = KontraptionPacketHandler()
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

    companion object {
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