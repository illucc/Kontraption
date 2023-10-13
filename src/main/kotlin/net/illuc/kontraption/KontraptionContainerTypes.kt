package net.illuc.kontraption

import mekanism.common.inventory.container.tile.MekanismTileContainer
import mekanism.common.registration.impl.ContainerTypeDeferredRegister
import mekanism.common.registration.impl.ContainerTypeRegistryObject
import net.illuc.kontraption.blockEntities.TileEntityIonThruster


object KontraptionContainerTypes {
    private fun KontraptionContainerTypes() {}

    val CONTAINER_TYPES = ContainerTypeDeferredRegister(Kontraption.MODID)


    val ION_THRUSTER: ContainerTypeRegistryObject<MekanismTileContainer<TileEntityIonThruster>> = CONTAINER_TYPES.register(KontraptionBlocks.ION_THRUSTER, TileEntityIonThruster::class.java)

}