package com.oierbravo.mechanical_trading_station.registrate;

import com.oierbravo.mechanical_trading_station.MechanicalTradingStation;
import com.oierbravo.mechanical_trading_station.content.machines.mechanical_trading_station.MechanicalTradingStationMenu;
import com.oierbravo.mechanical_trading_station.content.machines.mechanical_trading_station.MechanicalTradingStationScreen;
import com.oierbravo.trading_station.TradingStation;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.MenuEntry;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

public class ModMenus {

    public static final CreateRegistrate REGISTRATE = MechanicalTradingStation.registrate();

    public static final MenuEntry<MechanicalTradingStationMenu> MECHANICAL_TRADING_STATION =  TradingStation.registrate()
            .menu("mechanical_trading_station", MechanicalTradingStationMenu::factory, () -> MechanicalTradingStationScreen::new)
            .register();


    public static void register() {}
}
