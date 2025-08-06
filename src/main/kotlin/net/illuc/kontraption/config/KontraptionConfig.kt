package net.illuc.kontraption.config

import mekanism.api.math.FloatingLong
import mekanism.common.config.BaseMekanismConfig
import mekanism.common.config.value.*
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.fml.config.ModConfig.Type

class KontraptionConfig internal constructor() : BaseMekanismConfig() {
    private val configSpec: ForgeConfigSpec
    val liquidFuelThrust: CachedDoubleValue
    val liquidFuelConsumption: CachedDoubleValue
    val largeIonThrust: CachedDoubleValue
    val largeIonEnergyConsumption: CachedDoubleValue
    val ionThrust: CachedDoubleValue
    val ionConsumption: CachedDoubleValue
    val gyroTorqueStrength: CachedDoubleValue
    val thrusterSpeedLimit: CachedDoubleValue
    val toolgunActionConsumption: CachedFloatingLongValue
    val toolgunAssembleConsumption: CachedFloatingLongValue
    val toolgunStorage: CachedFloatingLongValue
    val toolgunChargeRate: CachedFloatingLongValue
    val dampeningStrength: CachedDoubleValue
    val zeroGravity: CachedBooleanValue
    val dampeningP: CachedDoubleValue
    val dampeningI: CachedDoubleValue
    val dampeningD: CachedDoubleValue

    init {
        val builder = ForgeConfigSpec.Builder()
        builder.comment("Kontraption Config. This config is synced between server and client.").push("kontraption")

        // Thrusters Config
        builder.comment("Large Thrusters Config").push("thrusters")
        liquidFuelThrust =
            CachedDoubleValue.wrap(
                this,
                builder
                    .comment("How powerful the liquid fuel thruster is. (3x3)")
                    .defineInRange("liquidFuelThrust", 90.0, 0.0, Double.MAX_VALUE),
            )

        liquidFuelConsumption =
            CachedDoubleValue.wrap(
                this,
                builder
                    .comment("How much fuel does the liquid fuel thruster use")
                    .defineInRange("liquidFuelConsumption", 30.0, 0.0, Double.MAX_VALUE),
            )

        largeIonThrust =
            CachedDoubleValue.wrap(
                this,
                builder
                    .comment("How powerful the large ion thruster is.")
                    .defineInRange("largeIonThrust", 900.0, 0.0, Double.MAX_VALUE),
            )

        largeIonEnergyConsumption =
            CachedDoubleValue.wrap(
                this,
                builder
                    .comment("How much energy does the large ion thruster use")
                    .defineInRange("largeIonEnergyConsumption", 300.0, 0.0, Double.MAX_VALUE),
            )
        builder.pop()

        // Small Thrusters Config
        builder.comment("Small Thrusters Config").push("small_thrusters")
        ionThrust =
            CachedDoubleValue.wrap(
                this,
                builder
                    .comment("How powerful the ion thruster is.")
                    .defineInRange("ionThrust", 400.0, 0.0, Double.MAX_VALUE),
            )

        ionConsumption =
            CachedDoubleValue.wrap(
                this,
                builder
                    .comment("How much energy does the ion thruster use.")
                    .defineInRange("ionConsumption", 100.0, 0.0, Double.MAX_VALUE),
            )
        builder.pop()

        // Gyro Config
        builder.comment("Gyroscope Settings").push("gyro")
        gyroTorqueStrength =
            CachedDoubleValue.wrap(
                this,
                builder
                    .comment("How powerful the gyro is.")
                    .defineInRange("gyroTorqueStrength", 100.0, 0.0, Double.MAX_VALUE),
            )
        builder.pop()

        // Physics Settings
        builder.comment("Physics Settings").push("physics")
        thrusterSpeedLimit =
            CachedDoubleValue.wrap(
                this,
                builder
                    .comment("At what speed the thruster starts slowing down")
                    .defineInRange("thrusterSpeedLimit", 20.0, 0.0, Double.MAX_VALUE),
            )

        dampeningStrength =
            CachedDoubleValue.wrap(
                this,
                builder
                    .comment("Base dampening strength (air resistance-like effect)")
                    .defineInRange("dampeningStrength", 1.0, 0.0, Double.MAX_VALUE),
            )

        zeroGravity =
            CachedBooleanValue.wrap(
                this,
                builder
                    .comment("Turns the gravity off (only gets switched off after placing a gyro)")
                    .define("zeroGravity", false),
            )
        builder.pop()

        // PID Settings for Inertia Dampener
        builder.comment("Inertia Dampener PID Settings").push("dampener")
        dampeningP =
            CachedDoubleValue.wrap(
                this,
                builder
                    .comment("Proportional gain for inertia dampener - higher values make more aggressive corrections")
                    .defineInRange("dampeningP", 1.0, 0.0, 10.0),
            )

        dampeningI =
            CachedDoubleValue.wrap(
                this,
                builder
                    .comment("Integral gain for inertia dampener - helps eliminate steady-state error")
                    .defineInRange("dampeningI", 0.1, 0.0, 2.0),
            )

        dampeningD =
            CachedDoubleValue.wrap(
                this,
                builder
                    .comment("Derivative gain for inertia dampener - reduces overshoot and oscillation")
                    .defineInRange("dampeningD", 0.5, 0.0, 5.0),
            )
        builder.pop()

        // Toolgun Settings
        builder.comment("Toolgun Settings").push("toolgun")
        toolgunActionConsumption =
            CachedFloatingLongValue.define(
                this,
                builder,
                "How much power does the toolgun consume when used",
                "toolgunActionConsumption",
                FloatingLong.createConst(1000),
            )

        toolgunAssembleConsumption =
            CachedFloatingLongValue.define(
                this,
                builder,
                "How much power does the toolgun consume per block when assembling a ship (including air blocks!)",
                "toolgunAssembleConsumption",
                FloatingLong.createConst(1000),
            )

        toolgunStorage =
            CachedFloatingLongValue.define(
                this,
                builder,
                "How much power does the toolgun store",
                "toolgunStorage",
                FloatingLong.createConst(20000000),
            )

        toolgunChargeRate =
            CachedFloatingLongValue.define(
                this,
                builder,
                "How fast does the toolgun charge",
                "toolgunChargeRate",
                FloatingLong.createConst(100000),
            )
        builder.pop() // HUH i was wondering how ya do that fancy config :P

        configSpec = builder.build()
    }

    override fun getFileName(): String = "kontraption"

    override fun getConfigSpec(): ForgeConfigSpec = configSpec

    override fun getConfigType(): Type = Type.SERVER
}
