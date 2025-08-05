package net.illuc.kontraption.ship

import com.fasterxml.jackson.annotation.JsonIgnore
import net.illuc.kontraption.blockEntities.TileEntityKey
import net.illuc.kontraption.util.toJOML
import net.minecraft.core.BlockPos
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import java.util.concurrent.CopyOnWriteArrayList

class KontraptionKeyBlockControl {
    data class KeyStone(
        val position: Vector3i,
        val keybind: Int,
        @JsonIgnore val be: TileEntityKey?,
    )

    private val keyStones = CopyOnWriteArrayList<KeyStone>()

    fun addKeys(
        pos: BlockPos,
        keybind: Int,
        be: TileEntityKey,
    ) {
        keyStones.add(KeyStone(pos.toJOML(), keybind, be))
    }

    fun removeKeys(
        pos: BlockPos,
        keybind: Int,
        be: TileEntityKey,
    ) {
        keyStones.remove(KeyStone(pos.toJOML(), keybind, be))
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
