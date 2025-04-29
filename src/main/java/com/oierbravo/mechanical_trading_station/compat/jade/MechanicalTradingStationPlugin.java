package com.oierbravo.mechanical_trading_station.compat.jade;

import com.oierbravo.mechanical_trading_station.ModConstants;
import com.oierbravo.mechanical_trading_station.content.machines.mechanical_trading_station.MechanicalTradingStationBlock;
import com.oierbravo.mechanical_trading_station.content.machines.mechanical_trading_station.MechanicalTradingStationBlockEntity;
import com.oierbravo.mechanicals.compat.jade.MechanicalProgressComponentProvider;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class MechanicalTradingStationPlugin implements IWailaPlugin {
    public static final ResourceLocation MOD_DATA  = ModConstants.asResource("data");
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(new MechanicalProgressComponentProvider(MOD_DATA), MechanicalTradingStationBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(new MechanicalProgressComponentProvider(MOD_DATA), MechanicalTradingStationBlock.class);
    }
}
