package com.oierbravo.mechanical_trading_station.compat.jade;

import com.oierbravo.mechanical_trading_station.content.machines.mechanical_trading_station.MechanicalTradingStationBlock;
import com.oierbravo.mechanical_trading_station.content.machines.mechanical_trading_station.MechanicalTradingStationBlockEntity;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class MechanicalTradingStationPlugin implements IWailaPlugin {
    public static final ResourceLocation MOD_DATA = new ResourceLocation("mechanical_trading_station:data");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(new ProgressComponentProvider(), MechanicalTradingStationBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(new ProgressComponentProvider(), MechanicalTradingStationBlock.class);
    }
}
