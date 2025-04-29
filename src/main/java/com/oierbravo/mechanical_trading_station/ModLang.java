package com.oierbravo.mechanical_trading_station;


import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.lang.LangBuilder;

public class ModLang extends Lang {
    public ModLang() {
        super();
    }
    public static LangBuilder builder() {
        return new LangBuilder(ModConstants.MODID);
    }
    public static LangBuilder translate(String langKey, Object... args) {
        return builder().translate(langKey, args);
    }

}
