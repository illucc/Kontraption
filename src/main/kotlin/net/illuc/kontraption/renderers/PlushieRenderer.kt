package net.illuc.kontraption.renderers

import com.google.common.collect.Maps
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.illuc.kontraption.Kontraption
import net.illuc.kontraption.blockEntities.TileEntityPlushie
import net.illuc.kontraption.blocks.BlockPlushie
import net.illuc.kontraption.renderers.PlushieRenderer.Companion.PLUSHIEMODELS
import net.minecraft.Util
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.client.model.data.ModelData

class PlushieRenderer(
    context: BlockEntityRendererProvider.Context,
) : BlockEntityRenderer<TileEntityPlushie> {
    private val plushieModels: Map<BlockPlushie.Type, BakedModel> =
        PLUSHIEMODELS.mapValues { plush ->
            Minecraft.getInstance().modelManager.getModel(plush.value)
        }

    override fun render(
        pBlockEntity: TileEntityPlushie,
        pPartialTick: Float,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int,
    ) {
        val blockstate = pBlockEntity.blockState
        val plushie: BlockPlushie = blockstate.block as BlockPlushie
        val rotY: Float = plushie.getYRotationDegrees(blockstate)
        val plushieModel = Minecraft.getInstance().blockRenderer.getBlockModel(plushie.defaultBlockState())
        pPoseStack.pushPose()
        pPoseStack.translate(0.5, 0.0, 0.5) // Somewhy BE is pretransformed by 0.5
        pPoseStack.mulPose(Axis.YP.rotationDegrees(rotY))
        pPoseStack.translate(-0.5, 0.0, -0.5)
        Minecraft.getInstance().blockRenderer.modelRenderer.renderModel(
            pPoseStack.last(),
            pBuffer.getBuffer(RenderType.cutout()),
            null,
            plushieModel,
            1.0f,
            1.0f,
            1.0f,
            pPackedLight,
            pPackedOverlay,
            ModelData.EMPTY,
            RenderType.cutout(),
        )
        pPoseStack.popPose()
    }

    companion object {
        val PLUSHIEMODELS: Map<BlockPlushie.Type, ResourceLocation> =
            Util.make(Maps.newHashMap()) { map ->
                map[BlockPlushie.Plushies.OTTER] = ResourceLocation(Kontraption.MODID, "block/otter_plushie")
                map[BlockPlushie.Plushies.COSMOS] = ResourceLocation(Kontraption.MODID, "block/cosmic_plushie")
                map[BlockPlushie.Plushies.ILLUC] = ResourceLocation(Kontraption.MODID, "block/illuc_plushie")
            }
    }
}
