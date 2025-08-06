package net.illuc.kontraption.ship

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import net.illuc.kontraption.util.toJOML
import net.minecraft.core.BlockPos
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import java.util.concurrent.CopyOnWriteArrayList

@JsonIgnoreProperties(ignoreUnknown = true)
class KontraptionKeyBlockControl {
    data class KeyStone(
        val position: Vector3i,
        val keybind: Int,
    )

    private val keyStones = CopyOnWriteArrayList<KeyStone>()

    fun addKeys(
        pos: BlockPos,
        keybind: Int,
    ) {
        keyStones.add(KeyStone(pos.toJOML(), keybind))
    }

    fun removeKeys(
        pos: BlockPos,
        keybind: Int,
    ) {
        keyStones.remove(KeyStone(pos.toJOML(), keybind))
    }

    fun removeAll(pos: BlockPos) {
        keyStones.removeAll { it.position == pos.toJOML() }
    }

    fun getKeystones(): CopyOnWriteArrayList<KeyStone> = keyStones

    companion object {
        fun getOrCreate(ship: ServerShip): KontraptionKeyBlockControl =
            ship.getAttachment<KontraptionKeyBlockControl>()
                ?: KontraptionKeyBlockControl().also { ship.saveAttachment(it) }
    }
}
