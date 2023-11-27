package net.illuc.kontraption

import mekanism.api.chemical.ChemicalTags
import mekanism.api.chemical.gas.Gas
import mekanism.common.Mekanism
import mekanism.common.tags.LazyTagLookup
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.FluidTags
import net.minecraft.tags.TagKey
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.registries.ForgeRegistries


object KontraptionTags {
    /**
     * Call to force make sure this is all initialized
     */
    fun init() {
        Fluids.init()
        Gases.init()
    }

    object Fluids {
        fun init() {}


        /*val BIOETHANOL = forgeTag("bioethanol")
        val BIOETHANOL_LOOKUP = LazyTagLookup.create(ForgeRegistries.FLUIDS, BIOETHANOL)
        val DEUTERIUM = forgeTag("deuterium")
        val FUSION_FUEL = forgeTag("fusion_fuel")
        val TRITIUM = forgeTag("tritium")*/
        private fun forgeTag(name: String): TagKey<Fluid> {
            return FluidTags.create(ResourceLocation("forge", name))
        }
    }

    object Gases {
        fun init() {}

        val THRUSTER_FUEL = tag("thruster_fuel")
        val THRUSTER_FUEL_LOOKUP = LazyTagLookup.create(ChemicalTags.GAS, THRUSTER_FUEL)
        val AWFUL_THRUSTER_FUEL = tag("awful_thruster_fuel")
        /*val DEUTERIUM = tag("deuterium")
        val DEUTERIUM_LOOKUP = LazyTagLookup.create(ChemicalTags.GAS, DEUTERIUM)
        val TRITIUM = tag("tritium")
        val TRITIUM_LOOKUP = LazyTagLookup.create(ChemicalTags.GAS, TRITIUM)
        val FUSION_FUEL = tag("fusion_fuel")
        val FUSION_FUEL_LOOKUP = LazyTagLookup.create(ChemicalTags.GAS, FUSION_FUEL)*/
        private fun tag(name: String): TagKey<Gas> {
            return ChemicalTags.GAS.tag(Mekanism.rl(name))
        }
    }
}