package com.oierbravo.mechanical_trading_station.registrate;

import com.oierbravo.mechanical_trading_station.MechanicalTradingStation;
import com.oierbravo.mechanical_trading_station.content.machines.mechanical_trading_station.TradedItemCountDisplaySource;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;

import java.util.function.Supplier;

public class ModDisplaySources {
    private static final CreateRegistrate REGISTRATE = MechanicalTradingStation.registrate();

    public static final RegistryEntry<DisplaySource, TradedItemCountDisplaySource> TRADED_ITEMS_COUNT = simple("traded_items_count", TradedItemCountDisplaySource::new);


    private static <T extends DisplaySource> RegistryEntry<DisplaySource, T> simple(String name, Supplier<T> supplier) {
        return REGISTRATE.displaySource(name, supplier).register();
    }
    public static void register() {
    }
}
