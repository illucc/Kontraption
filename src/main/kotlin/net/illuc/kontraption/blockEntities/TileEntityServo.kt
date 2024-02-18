package net.illuc.kontraption.blockEntities

import mekanism.common.tile.base.TileEntityMekanism
import net.illuc.kontraption.KontraptionBlocks
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.nbt.CompoundTag
import org.valkyrienskies.core.apigame.constraints.VSConstraintId

class TileEntityServo(pos: BlockPos?, state: BlockState?) : TileEntityMekanism(KontraptionBlocks.SERVO, pos, state) {

    private var shipID: Long = -1
    var hingeId : VSConstraintId? = null
    var attachmentConstraintId : VSConstraintId? = null
    var otherPos : BlockPos? = null
    var shipIds : List<Long>? = null

    override fun load(tag: CompoundTag) {
        super.load(tag)

        /*if (tag.contains("Kontraption\$shipId")) {
            shipID = tag.getLong("Kontraption\$shipId")
        }
        if (activated)
            if (shipID == -1L)
                angle = angleBefore
            else
                shipID = -1L


        shipIds = tag.getLongArray("shipIds").asList()*/
    }


}
