package com.oierbravo.mechanical_trading_station.registrate;

import com.oierbravo.mechanical_trading_station.MechanicalTradingStation;
import com.oierbravo.mechanical_trading_station.content.machines.mechanical_trading_station.MechanicalTradingStationBlockEntity;
import com.oierbravo.mechanical_trading_station.content.machines.mechanical_trading_station.MechanicalTradingStationRenderer;
import com.oierbravo.mechanical_trading_station.content.machines.mechanical_trading_station.MechanicalTradingStationVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class ModBlockEntities {
    public static final BlockEntityEntry<MechanicalTradingStationBlockEntity> MECHANICAL_TRADING_STATION = MechanicalTradingStation.registrate()
            .blockEntity("mechanical_trading_station", MechanicalTradingStationBlockEntity::new)
            .visual(() -> MechanicalTradingStationVisual::new)
            .validBlocks(ModBlocks.MECHANICAL_TRADING_STATION,ModBlocks.MECHANICAL_TRADING_STATION_UNBREAKABLE)
            .renderer(() -> MechanicalTradingStationRenderer::new)
            .register();

    public static void register() {}
}