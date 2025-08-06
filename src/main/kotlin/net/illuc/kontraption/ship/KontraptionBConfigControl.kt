package net.illuc.kontraption.ship

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import net.illuc.kontraption.util.toJOML
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import java.util.concurrent.CopyOnWriteArrayList

@JsonIgnoreProperties(ignoreUnknown = true)
class KontraptionBConfigControl {
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes(
        JsonSubTypes.Type(value = BlockSetting.BooleanSetting::class, name = "boolean"),
        JsonSubTypes.Type(value = BlockSetting.IntSetting::class, name = "int"),
        JsonSubTypes.Type(value = BlockSetting.StringSetting::class, name = "string"),
    )
    // IMA ABOUT TO PUT SERIALIZATION INTO MY [HYPERLINK BLOCKED]
    sealed class BlockSetting<T> {
        abstract val name: String
        abstract var value: T

        data class BooleanSetting
            @JsonCreator
            constructor(
                @JsonProperty("name") override val name: String,
                @JsonProperty("value") override var value: Boolean,
            ) : BlockSetting<Boolean>()

        data class IntSetting
            @JsonCreator
            constructor(
                @JsonProperty("name") override val name: String,
                @JsonProperty("value") override var value: Int,
            ) : BlockSetting<Int>()

        data class StringSetting
            @JsonCreator
            constructor(
                @JsonProperty("name") override val name: String,
                @JsonProperty("value") override var value: String,
            ) : BlockSetting<String>()
    }

    data class ConfigBlock
        @JsonCreator
        constructor(
            @JsonProperty("pos") val pos: Vector3i,
            @JsonProperty("settings") val settings: MutableList<BlockSetting<*>>,
            @JsonProperty("blockId") val blockId: String,
        )

    private val blockSettings = CopyOnWriteArrayList<ConfigBlock>()

    fun addConfigBlock(
        pos: BlockPos,
        blockEntity: BlockEntity?,
        settings: List<BlockSetting<*>>,
    ) {
        val blockId = blockEntity?.blockState?.block?.descriptionId ?: "unknown"
        blockSettings.add(ConfigBlock(pos.toJOML(), settings.toMutableList(), blockId))
    }

    fun removeConfigBlock(pos: BlockPos) {
        blockSettings.removeAll { it.pos == pos.toJOML() }
    }

    // FUCKIN SERIALIZER AUTODETECTS GETS
    @JsonIgnore
    fun allConfigBlock(): CopyOnWriteArrayList<ConfigBlock> = blockSettings

    fun updateConfigBlock(
        pos: BlockPos,
        settingName: String,
        newValue: Any,
    ) {
        val configBlock = blockSettings.find { it.pos == pos.toJOML() } ?: return
        val setting = configBlock.settings.find { it.name == settingName } ?: return

        when (setting) {
            is BlockSetting.BooleanSetting -> if (newValue is Boolean) setting.value = newValue
            is BlockSetting.IntSetting -> if (newValue is Int) setting.value = newValue
            is BlockSetting.StringSetting -> if (newValue is String) setting.value = newValue
        }
    }

    companion object {
        fun getOrCreate(ship: ServerShip): KontraptionBConfigControl = ship.getAttachment<KontraptionBConfigControl>() ?: KontraptionBConfigControl().also { ship.saveAttachment(it) }

        @JvmStatic
        @JsonCreator
        fun create(
            @JsonProperty("blockSettings") blockSettings: CopyOnWriteArrayList<ConfigBlock>?,
        ): KontraptionBConfigControl =
            KontraptionBConfigControl().apply {
                if (blockSettings != null) {
                    this.blockSettings.addAll(blockSettings)
                }
            }
    }
}
