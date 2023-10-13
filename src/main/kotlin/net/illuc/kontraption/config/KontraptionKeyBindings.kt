package net.illuc.kontraption.config

import net.minecraft.client.KeyMapping
import org.lwjgl.glfw.GLFW
import java.util.function.Consumer
import java.util.function.Supplier

object KontraptionKeyBindings {
    private val toBeRegistered = mutableListOf<Consumer<Consumer<KeyMapping>>>()

    // val shipUp = register("key.valkyrienskies.ship_up", 32, "category.valkyrienskies.driving")
    val shipUp    = register("key.kontraption.up"        , GLFW.GLFW_KEY_SPACE    , "category.kontraption.ship_controls")
    val shipDown  = register("key.kontraption.down"      , GLFW.GLFW_KEY_V        , "category.kontraption.ship_controls")

    val pitchUp   = register("key.kontraption.pitch_up"  , GLFW.GLFW_KEY_UP       , "category.kontraption.ship_controls")
    val pitchDown = register("key.kontraption.pitch_down", GLFW.GLFW_KEY_DOWN     , "category.kontraption.ship_controls")
    val yawUp     = register("key.kontraption.yaw_up"    , GLFW.GLFW_KEY_LEFT     , "category.kontraption.ship_controls")
    val yawDown   = register("key.kontraption.yaw_down"  , GLFW.GLFW_KEY_RIGHT    , "category.kontraption.ship_controls")
    val rollUp    = register("key.kontraption.roll_up"   , GLFW.GLFW_KEY_PAGE_UP  , "category.kontraption.ship_controls")
    val rollDown  = register("key.kontraption.roll_down" , GLFW.GLFW_KEY_PAGE_DOWN, "category.kontraption.ship_controls")


    // val shipForward = register("key.valkyrienskies.ship_forward", 87, "category.valkyrienskies.driving")
    // val shipBack = register("key.valkyrienskies.ship_back", 83, "category.valkyrienskies.driving")
    // val shipLeft = register("key.valkyrienskies.ship_left", 65, "category.valkyrienskies.driving")
    // val shipRight = register("key.valkyrienskies.ship_right", 68, "category.valkyrienskies.driving")

    private fun register(name: String, keyCode: Int, category: String): Supplier<KeyMapping> =
            object : Supplier<KeyMapping>, Consumer<Consumer<KeyMapping>> {
                lateinit var registered: KeyMapping

                // If this throws error ur on server
                override fun get(): KeyMapping = registered
                override fun accept(t: Consumer<KeyMapping>) {
                    registered = KeyMapping(name, keyCode, category)
                    t.accept(registered)
                }
            }.apply { toBeRegistered.add(this) }

    fun clientSetup(registerar: Consumer<KeyMapping>) {
        toBeRegistered.forEach { it.accept(registerar) }
    }
}