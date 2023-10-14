package net.illuc.kontraption


import mekanism.common.util.EnumUtils
import mekanism.common.util.VoxelShapeUtils
import mekanism.common.util.VoxelShapeUtils.setShape
import net.minecraft.world.level.block.Block.box
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.phys.shapes.VoxelShape


object KontraptionBlockShapes {
    val WHEEL = arrayOfNulls<VoxelShape>(EnumUtils.DIRECTIONS.size)

    init {
        setShape(VoxelShapeUtils.combine(
                box(0.0, 7.0, 6.0, 16.0, 16.0, 15.0),
                box(0.0, 7.0, 6.0, 16.0, 16.0, 15.0),
                box(0.0, 7.0, 6.0, 16.0, 16.0, 15.0),
                box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0)
        ), WHEEL)
    }
}