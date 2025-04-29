package com.oierbravo.mechanical_trading_station.infrastructure.config;

import com.oierbravo.mechanical_trading_station.content.machines.mechanical_trading_station.MechanicalTradingStationConfigs;
import net.createmod.catnip.config.ConfigBase;

public class ModConfigServer extends ConfigBase {
    public final MechanicalTradingStationConfigs mechanicalTradingStation = nested(0, MechanicalTradingStationConfigs::new, "Sifter");

    public final ModStress stressValues = nested(0, ModStress::new, "Stress values");

    @Override
    public String getName() {
        return "server";
    }
}
