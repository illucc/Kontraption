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

        thrusterSpeedLimit = CachedDoubleValue.wrap(this, builder.comment("At what speed the thruster starts slowing down (wip, currently just stops the thruster)")
                .define("thrusterSpeedLimit", 100.0),
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