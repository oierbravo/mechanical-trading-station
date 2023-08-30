package com.oierbravo.mechanical_trading_station.foundatation.data;

import com.google.common.base.Supplier;
import com.google.gson.JsonElement;
import com.oierbravo.mechanical_trading_station.MechanicalTradingStation;
import com.oierbravo.mechanical_trading_station.foundatation.utility.ModLang;
import com.simibubi.create.foundation.data.LangPartial;

public enum ModLangPartials implements LangPartial {
    UI("UI");

    private final String displayName;
    private final Supplier<JsonElement> provider;

    @Override
    public String getDisplayName() {
        return displayName;
    }
    private ModLangPartials(String displayName) {
        this.displayName = displayName;
        String fileName = ModLang.asId(name());
        this.provider = () -> LangPartial.fromResource(MechanicalTradingStation.MODID, fileName);
    }
    private ModLangPartials(String displayName, Supplier<JsonElement> provider) {
        this.displayName = displayName;
        this.provider = provider;
    }
    @Override
    public JsonElement provide() {
        return provider.get();
    }
}
