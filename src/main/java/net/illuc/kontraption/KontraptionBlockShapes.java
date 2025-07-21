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
    public static final VoxelShape[] CANNON = new VoxelShape[EnumUtils.DIRECTIONS.length];
    public static final VoxelShape[] CONNECTOR = new VoxelShape[EnumUtils.DIRECTIONS.length];
    public static final VoxelShape[] PLUSHIE = new VoxelShape[EnumUtils.DIRECTIONS.length];

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
        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                box(1, 1, 1, 15, 15, 15),
                box(6, -48, 6, 10, 16, 10)
        ), CANNON, true);
        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                box(0, 9, 0, 16, 14, 16),
                box(3, 14, 13, 13, 16, 15),
                box(3, 14, 1, 13, 16, 3),
                box(13, 14, 1, 15, 16, 15),
                box(1, 14, 1, 3, 16, 15),
                box(1, 5, 1, 15, 10, 15),
                box(0, 0, 0, 16, 5, 16)
        ),CONNECTOR, true);
        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                box(5.5, -0.1, 3.90553, 7.5, 1.9, 9.90553),
                box(5.25, -0.35, 3.65553, 7.75, 2.15, 10.15553),
                box(8.5, -0.1, 3.90553, 10.5, 1.9, 9.90553),
                box(8.25, -0.35, 3.65553, 10.75, 2.15, 10.15553),
                box(9.75, 1.15388, 8.14725, 12.25, 7.65388, 10.64725),
                box(10.0, 1.40388, 8.39725, 12.0, 7.40388, 10.39725),
                box(4.0, 1.40388, 8.39725, 6.0, 7.40388, 10.39725),
                box(3.75, 1.15388, 8.14725, 6.25, 7.65388, 10.64725),
                box(5.0, 7.0, 6.6765, 11.0, 13.0, 12.6765),
                box(4.75, 6.75, 6.4265, 11.25, 13.25, 12.9265),
                box(6.0, 0.0, 8.6765, 10.0, 7.0, 10.6765),
                box(5.8, -0.25, 8.4265, 10.3, 7.25, 10.9265)
        ), PLUSHIE, true);

    }
}
