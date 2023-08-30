package com.oierbravo.mechanical_trading_station.registrate;

import com.simibubi.create.AllShapes;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

import static net.minecraft.core.Direction.UP;

public class ModShapes {
    public static final VoxelShaper DRYER =shape(2, 0, 2, 14, 3, 14).forDirectional(UP);
           // shape(2, 0, 2, 14, 2, 4).add(2, 0, 11, 14, 2, 4).forDirectional(UP);

    private static AllShapes.Builder shape(VoxelShape shape) {
        return new AllShapes.Builder(shape);
    }
    private static AllShapes.Builder shape(double x1, double y1, double z1, double x2, double y2, double z2) {
        return shape(cuboid(x1, y1, z1, x2, y2, z2));
    }

    private static VoxelShape cuboid(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Block.box(x1, y1, z1, x2, y2, z2);
    }
}
