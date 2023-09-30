package net.illuc.kontraption


//import net.illuc.kontraption.blockEntities.IonThrusterBlockEntity you will be a femboy
//import net.illuc.kontraption.blockEntities.IonThrusterBlockEntity

//import net.illuc.kontraption.blockEntities.IonThrusterBlockEntity shut the fuck up

import mekanism.common.registration.impl.TileEntityTypeDeferredRegister
import net.illuc.kontraption.blockEntities.TileIonThruster
import net.illuc.kontraption.network.to_server.KontraptionBlocks
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState


public object KontraptionTileEntityTypes {

    private fun KontraptionTileEntityTypes() {}

    val TILE_ENTITY_TYPES = TileEntityTypeDeferredRegister(Kontraption.MODID)

    public val ION_THRUSTER = TILE_ENTITY_TYPES.register(KontraptionBlocks.ION_THRUSTER) { pos: BlockPos?, state: BlockState? -> TileIonThruster(pos, state) }


    //val CHARGEPAD = TILE_ENTITY_TYPES.register(MekanismBlocks.CHARGEPAD) { pos: BlockPos?, state: BlockState? -> TileEntityChargepad(pos, state) }

}