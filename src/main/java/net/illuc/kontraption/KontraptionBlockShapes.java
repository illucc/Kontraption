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
    public static final VoxelShape[] SHIP_CONTROL_INTERFACE = new VoxelShape[EnumUtils.HORIZONTAL_DIRECTIONS.length];

    static {
        VoxelShapeUtils.setShape(VoxelShapeUtils.combine(
                box(0, 7, 6, 16, 16, 15), // drum
                box(0, 0, 0, 16, 6, 16), // base
                box(3, 6, 5, 5, 15, 16), // ring1
                box(11, 6, 5, 13, 15, 16), // ring2
                box(0, 6, 2, 16, 16, 5), // back
                box(3, 6, 1, 5, 15, 2), // bar1
                box(11, 6, 1, 13, 15, 2), // bar2
                box(4, 6, 0, 12, 12, 2), // port
                box(0, 13, 0, 16, 14, 2), // fin7
                box(0, 15, 0, 16, 16, 2), // fin8
                box(0, 11, 0, 4, 12, 2), // fin1
                box(0, 9, 0, 4, 10, 2), // fin2
                box(0, 7, 0, 4, 8, 2), // fin3
                box(12, 11, 0, 16, 12, 2), // fin4
                box(12, 9, 0, 16, 10, 2), // fin5
                box(12, 7, 0, 16, 8, 2) // fin6
        ), ION_THRUSTER);
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
