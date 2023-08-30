package com.oierbravo.mechanical_trading_station.foundatation.utility;

import com.oierbravo.mechanical_trading_station.MechanicalTradingStation;
import com.simibubi.create.foundation.utility.LangBuilder;

public class ModLang extends com.simibubi.create.foundation.utility.Lang {
    public ModLang() {
        super();
    }
    public static LangBuilder builder() {
        return new LangBuilder(MechanicalTradingStation.MODID);
    }
    public static LangBuilder translate(String langKey, Object... args) {
        return builder().translate(langKey, args);
    }

}
