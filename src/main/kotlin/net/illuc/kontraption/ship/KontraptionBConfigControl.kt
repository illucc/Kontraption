package net.illuc.kontraption.ship

import com.fasterxml.jackson.annotation.JsonIgnore
import net.illuc.kontraption.util.toJOML
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import java.util.concurrent.CopyOnWriteArrayList

class KontraptionBConfigControl {
    sealed class BlockSetting {
        abstract val name: String
        open val displayValue: String = "" // ughh i want my any type but somewhy VS HATES IT, prev is just used much simpler BlockSetting class and Type subclass, not i have this bss

        data class BooleanSetting(
            override val name: String,
            var value: Boolean,
        ) : BlockSetting() {
            override val displayValue get() = value.toString()
        }

        data class IntSetting(
            override val name: String,
            var value: Int,
        ) : BlockSetting() {
            override val displayValue get() = value.toString()
        }

        data class StringSetting(
            override val name: String,
            var value: String,
        ) : BlockSetting() {
            override val displayValue get() = value
        }

        data class TittleSetting(
            override val name: String,
            var value: String,
        ) : BlockSetting() {
            override val displayValue get() = value
        }
    }

    data class ConfigBlock(
        val pos: Vector3i,
        @JsonIgnore val blockEntity: BlockEntity?, // Quess who forgot that be only exist server sidee ;3
        val settings: MutableList<BlockSetting>,
    )

    private val blockSettings = CopyOnWriteArrayList<ConfigBlock>()

    fun addConfigBlock(
        pos: BlockPos,
        blockEntity: BlockEntity?,
        settings: List<BlockSetting>,
    ) {
        blockSettings.add(ConfigBlock(pos.toJOML(), blockEntity, settings.toMutableList()))
    }

    fun removeConfigBlock(pos: BlockPos) {
        blockSettings.removeAll { it.pos == pos.toJOML() }
    }

    fun getConfigBlock(): CopyOnWriteArrayList<ConfigBlock> = blockSettings

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
            is BlockSetting.TittleSetting -> {} // bleh, wonder if ANY was ze issue . . .
        }
    }

    companion object {
        fun getOrCreate(ship: ServerShip): KontraptionBConfigControl = ship.getAttachment<KontraptionBConfigControl>() ?: KontraptionBConfigControl().also { ship.saveAttachment(it) }
    }
}
