package net.illuc.kontraption

import net.minecraft.client.Minecraft
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Blocks
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.forge.runForDist


/**
 * Main mod class. Should be an `object` declaration annotated with `@Mod`.
 * The modid should be declared in this object and should match the modId entry
 * in mods.toml.
 *
 * An example for blocks is in the `blocks` package of this mod.
 */
@Mod(KontraptionMod.ID)
object KontraptionMod {
    const val ID = "kontraption"
    // the logger for our mod
    val LOGGER: Logger = LogManager.getLogger(ID)

    init {
        LOGGER.log(Level.INFO, "Hello world!")

        // Register the KDeferredRegister to the mod-specific event bus
        KontraptionBlocks.BLOCKS.register(MOD_BUS)
        KontraptionItems.ITEMS.register(MOD_BUS)

        KontraptionItems.TAB = object : CreativeModeTab("kontraption.main_tab") {
            override fun makeIcon(): ItemStack {
                return ItemStack(Blocks.ACACIA_DOOR)
            }
        }




        val obj = runForDist(
                clientTarget = {
                    MOD_BUS.addListener(KontraptionMod::onClientSetup)
                    Minecraft.getInstance()
                },
                serverTarget = {
                    MOD_BUS.addListener(KontraptionMod::onServerSetup)
                    "test"
                })

        println(obj)
    }

    /**
     * This is used for initializing client specific
     * things such as renderers and keymaps
     * Fired on the mod specific event bus.
     */
    private fun onClientSetup(event: FMLClientSetupEvent) {
        LOGGER.log(Level.INFO, "Initializing client...")
    }

    /**
     * Fired on the global Forge bus.
     */
    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
        LOGGER.log(Level.INFO, "Server starting...")
    }
}