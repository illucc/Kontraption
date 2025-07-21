package net.illuc.kontraption.multiblocks.largeionring.parts

import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartBlock
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartTypeProperties
import net.illuc.kontraption.GlobalRegistry
import net.illuc.kontraption.multiblocks.largeionring.IIonRingPartType
import net.illuc.kontraption.multiblocks.largeionring.LargeIonRingMultiBlock
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.common.util.NonNullSupplier

enum class IonRingPartTypes(
    private val tileTypeSupplier: NonNullSupplier<NonNullSupplier<BlockEntityType<*>>>,
    private val blockFactory: (MultiblockPartBlock.MultiblockPartProperties<IIonRingPartType>) -> LargeIonMultiblockPartBlockTemplate<LargeIonRingMultiBlock, IIonRingPartType>,
    private val translationKey: String = "",
) : IIonRingPartType {
    Casing(
        NonNullSupplier { NonNullSupplier(GlobalRegistry.TileEntities.LARGE_ION_THRUSTER_CASING::get) },
        { props -> LargeIonMultiblockPartBlockTemplate(props) },
        "block.kontraption.largeionringcasing",
    ),
    Coil(
        NonNullSupplier { NonNullSupplier(GlobalRegistry.TileEntities.LARGE_ION_THRUSTER_COIL::get) },
        { props -> LargeIonMultiblockPartBlockTemplate(props) },
        "block.kontraption.coppercoil",
    ),

    Controller(
        NonNullSupplier { NonNullSupplier(GlobalRegistry.TileEntities.LARGE_ION_THRUSTER_CONTROLLER::get) },
        { props -> LargeIonMultiblockPartBlockTemplate(props) },
        "block.kontraption.largeionringcontroller",
    ),

    PowerPortRF(
        NonNullSupplier { NonNullSupplier(GlobalRegistry.TileEntities.LARGE_ION_THRUSTER_VALVE::get) },
        { props -> LargeIonMultiblockPartBlockTemplate(props) },
        "block.kontraption.largeionringpowerportrf",
    ),
    ;

    override fun getPartTypeProperties(): MultiblockPartTypeProperties<LargeIonRingMultiBlock, IIonRingPartType> = properties

    override fun getSerializedName(): String = name

    private val properties =
        MultiblockPartTypeProperties(
            tileTypeSupplier,
            blockFactory,
            translationKey,
            { props -> props.noOcclusion() },
        )
}
