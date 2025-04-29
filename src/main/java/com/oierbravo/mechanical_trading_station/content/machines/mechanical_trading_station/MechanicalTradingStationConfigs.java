package com.oierbravo.mechanical_trading_station.content.machines.mechanical_trading_station;

import net.createmod.catnip.config.ConfigBase;
import org.jetbrains.annotations.NotNull;

public class MechanicalTradingStationConfigs extends ConfigBase {

    public final ConfigFloat minimumSpeed = f(1,1,"minimumSpeed", Comments.minimumSpeed);
    public final ConfigInt outputCapacity = i(16,1,"outputCapacity", Comments.outputCapacity);


    private static class Comments {
        static String minimumSpeed = "Minimum required speed.";
        static String outputCapacity = "Output item capacity.";
    }

    @Override
    public @NotNull String getName() {
        return "Sifter";
    }

}
