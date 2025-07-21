package net.illuc.kontraption.renderers
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.illuc.kontraption.Kontraption
import net.illuc.kontraption.multiblocks.largeionring.parts.LargeIonMultiblockPartBlockTemplate
import net.illuc.kontraption.multiblocks.largeionring.parts.LargeIonRingController
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.Vec3
import net.minecraftforge.client.model.data.ModelData
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class LargeIonExhaustRenderer(
    context: BlockEntityRendererProvider.Context?,
) : BlockEntityRenderer<LargeIonRingController> {
    var logger: Logger = LogManager.getLogger(Kontraption::class)
    private val exhaustresourcesz = ResourceLocation(Kontraption.MODID, "block/ion_exhaust")
    private val exhaustmodelz: BakedModel = Minecraft.getInstance().modelManager.getModel(exhaustresourcesz)

    private var xmv2 = 0.0f
    private var ymv2 = 0.0f
    private var zmv2 = 0.0f

    private var mvSCALE = 0.7f
    private var MULTI = -1.0f

    private var OUTTEST = false

    override fun render(
        blockEntity: LargeIonRingController,
        partialTicks: Float,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        combinedLight: Int,
        combinedOverlay: Int,
    ) {
        val buffer = Minecraft.getInstance().renderBuffers().bufferSource()
        val state = blockEntity.blockState.getValue(LargeIonMultiblockPartBlockTemplate.STATETYPE)
        val mbsize = blockEntity.mbSize.toFloat()
        val centre = blockEntity.centerPos
        val enabled = blockEntity.enabled
        if (state != 9) return
        if (!enabled) return

        val time = (System.currentTimeMillis() % 1500L) / 1500.0f // 0.0 to 1.0 hopefully
        val ringSpacing = 0.3f * mbsize
        val animationOffset = time * ringSpacing
        RenderSystem.disableCull()

        for (i in 0..2) {
            val baseOffset = i * ringSpacing
            val totalOffset = (baseOffset + animationOffset) % (ringSpacing * 3)
            val yOffset = totalOffset

            val normalized = 1.0f - (yOffset / (ringSpacing * 3))
            val clamped = normalized.coerceIn(0.3f, 1.0f)
            val scale = mvSCALE * clamped
            val alpha = normalized

            val offset = 0.5 * mbsize * -1
            val offSCALE = (1.0f - scale) / 2

            poseStack.pushPose()
            poseStack.translate(
                ((centre.x * MULTI) + xmv2 + (mbsize * offSCALE) + offset.toInt()).toDouble(),
                ((centre.y * MULTI) + ymv2).toDouble() - yOffset,
                (centre.z * MULTI) + zmv2 + (mbsize * offSCALE) + offset.toInt().toDouble(),
            )
            poseStack.scale(mbsize * scale, mbsize * scale, mbsize * scale)
            RenderSystem.setShaderColor(1f, 1f, 1f, alpha)

            Minecraft.getInstance().blockRenderer.modelRenderer.renderModel(
                poseStack.last(),
                buffer.getBuffer(RenderType.translucent()),
                null,
                exhaustmodelz,
                1.0f,
                1.0f,
                1.0f,
                combinedLight,
                combinedOverlay,
                ModelData.EMPTY,
                RenderType.translucent(),
            )

            poseStack.popPose()
            buffer.endLastBatch() // Batch has to be ended before any renderSystem related bs is changed again, sort of SUBMIT
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        }
        RenderSystem.enableCull()
    }

    override fun shouldRender(
        blockEntity: LargeIonRingController,
        cameraPosition: Vec3,
    ): Boolean = true

    override fun shouldRenderOffScreen(blockEntity: LargeIonRingController): Boolean = true
}
