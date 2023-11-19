package net.illuc.kontraption;

import mekanism.common.util.EnumUtils;
import mekanism.common.util.VoxelShapeUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public class KontraptionBlockShapes {
    private KontraptionBlockShapes() {
    }

    private static VoxelShape box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return Block.box(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static final VoxelShape[] ION_THRUSTER = new VoxelShape[EnumUtils.DIRECTIONS.length];
    public static final VoxelShape[] GYRO = new VoxelShape[EnumUtils.DIRECTIONS.length];
    public static final VoxelShape[] SHIP_CONTROL_INTERFACE = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];

    static {
        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                box(2, 0, 2, 14, 16, 14), // cube
                box(0, 0, 0, 16, 4, 16), // the slab like thing
                box(2, -12, 2, 14, -3, 14)
        ), ION_THRUSTER, true);
        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                box(1, 1, 1, 15, 15, 15) // cube
        ), GYRO, true);
        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                box(0, 0, 0, 16, 5, 16),
                box(0, 6, 0, 16, 10, 16),
                box(1, 5, 1, 15, 6, 15),
                box(6, 10, 10, 10, 12, 13),
                box(3, 10, 1, 13, 11, 6),
                box(2, 13, 5, 14, 14, 11),
                box(1, 14, 3, 15, 16, 13)
        ), SHIP_CONTROL_INTERFACE);
    }
}
