package com.oierbravo.mechanical_trading_station.content.machines.mechanical_trading_station;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.ShaftInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class MechanicalTradingStationInstance extends ShaftInstance<MechanicalTradingStationBlockEntity> implements DynamicInstance {
    public MechanicalTradingStationInstance(MaterialManager materialManager, MechanicalTradingStationBlockEntity blockEntity) {
        super(materialManager, blockEntity);
    }


    @Override
    protected Instancer<RotatingData> getModel() {
        BlockState referenceState = blockState.rotate(blockEntity.getLevel(), blockEntity.getBlockPos(), Rotation.CLOCKWISE_180);
        Direction facing = referenceState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        return getRotatingMaterial().getModel(AllPartialModels.SHAFT_HALF, referenceState, facing);
    }

    @Override
    public void beginFrame() {

    }
}
