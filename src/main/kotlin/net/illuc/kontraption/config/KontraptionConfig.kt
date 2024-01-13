package net.illuc.kontraption.config

import mekanism.api.math.FloatingLong
import mekanism.common.config.BaseMekanismConfig
import mekanism.common.config.value.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.dimension.DimensionType
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.fml.config.ModConfig.Type


class KontraptionConfig internal constructor() : BaseMekanismConfig() {
    private val configSpec: ForgeConfigSpec
    val liquidFuelThrust: CachedDoubleValue
    val liquidFuelConsumption: CachedDoubleValue
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

    init {
        val builder = ForgeConfigSpec.Builder()
        builder.comment("Kontraption Config. This config is synced between server and client.").push("kontraption")
        liquidFuelThrust = CachedDoubleValue.wrap(this, builder.comment("How powerful the liquid fuel thruster is. (3x3)")
                .define("liquidFuelThrust", 60.0),
        )

        liquidFuelConsumption = CachedDoubleValue.wrap(this, builder.comment("How much fuel does the liquid fuel thruster use")
                .define("liquidFuelConsumption", 30.0),
        )


        ionThrust = CachedDoubleValue.wrap(this, builder.comment("How powerful the ion thruster is.")
                .define("ionThrust", 1.0),
        )

        ionConsumption = CachedDoubleValue.wrap(this, builder.comment("How much energy does the ion thruster use.")
                .define("ionConsumption", 100.0),
        )

        gyroTorqueStrength = CachedDoubleValue.wrap(this, builder.comment("How powerful the gyro is.")
                .define("gyroTorqueStrength", 100.0),
        )

        thrusterSpeedLimit = CachedDoubleValue.wrap(this, builder.comment("At what speed the thruster starts slowing down")
                .define("thrusterSpeedLimit", 20.0),
        )

        toolgunActionConsumption = CachedFloatingLongValue.define(this, builder, ("How much power does the toolgun consume when used"),
                "toolgunActionConsumption", FloatingLong.createConst(1000),
        )

        toolgunAssembleConsumption = CachedFloatingLongValue.define(this, builder, ("How much power does the toolgun consume per block when assembling a ship (including air blocks!)"),
                "toolgunAssembleConsumption", FloatingLong.createConst(1000),
        )

        toolgunStorage = CachedFloatingLongValue.define(this, builder, ("How much power does the toolgun store"),
                "toolgunStorage", FloatingLong.createConst(20000000),
        )

        toolgunChargeRate = CachedFloatingLongValue.define(this, builder, ("How fast does the toolgun charge"),
                "toolgunChargeRate", FloatingLong.createConst(100000),
        )

        dampeningStrength = CachedDoubleValue.wrap(this, builder.comment("How strong the dampening is")
                .define("dampeningAmount", 2.0),
        )

        zeroGravity = CachedBooleanValue.wrap(this, builder.comment("Turns the gravity off (only gets swithced off after placing a gyro)")
                .define("zeroGravity", false),
        )





        configSpec = builder.build()
    }

    override fun getFileName(): String {
        return "kontraption"
    }

    override fun getConfigSpec(): ForgeConfigSpec {
        return configSpec
    }

    override fun getConfigType(): Type {
        return Type.SERVER
    }

    companion object {
        /*private const val TURBINE_CATEGORY = "turbine"
        private const val WIND_CATEGORY = "wind_generator"
        private const val HEAT_CATEGORY = "heat_generator"
        private const val HOHLRAUM_CATEGORY = "hohlraum"
        private const val FUSION_CATEGORY = "fusion_reactor"
        private const val FISSION_CATEGORY = "fission_reactor"*/
    }
}