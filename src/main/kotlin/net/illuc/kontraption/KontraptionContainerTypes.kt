package net.illuc.kontraption

import mekanism.common.inventory.container.tile.MekanismTileContainer
import mekanism.common.registration.impl.ContainerTypeDeferredRegister
import mekanism.common.registration.impl.ContainerTypeRegistryObject
import mekanism.common.tile.factory.TileEntityFactory
import net.illuc.kontraption.blockEntities.TileIonThruster
import net.illuc.kontraption.network.to_server.KontraptionBlocks


object KontraptionContainerTypes {
    private fun KontraptionContainerTypes() {}

    val CONTAINER_TYPES = ContainerTypeDeferredRegister(Kontraption.MODID)


    val ION_THRUSTER: ContainerTypeRegistryObject<MekanismTileContainer<TileIonThruster>> = CONTAINER_TYPES.register(KontraptionBlocks.ION_THRUSTER, TileIonThruster::class.java)

}