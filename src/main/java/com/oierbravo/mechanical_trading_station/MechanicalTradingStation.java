package com.oierbravo.mechanical_trading_station;

import com.oierbravo.mechanical_trading_station.content.machines.mechanical_trading_station.MechanicalTradingStationBlockEntity;
import com.oierbravo.mechanical_trading_station.infrastructure.data.ModDataGen;
import com.oierbravo.mechanical_trading_station.registrate.ModBlockEntities;
import com.oierbravo.mechanical_trading_station.registrate.ModBlocks;
import com.oierbravo.mechanical_trading_station.registrate.ModDisplaySources;
import com.oierbravo.mechanicals.utility.RegistrateLangBuilder;
import com.oierbravo.trading_station.registrate.ModCreativeTabs;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@net.neoforged.fml.common.Mod(ModConstants.MODID)
public class MechanicalTradingStation {

    public static final CreateRegistrate REGISTRATE =
            CreateRegistrate.create(com.oierbravo.mechanical_trading_station.ModConstants.MODID).defaultCreativeTab(ModCreativeTabs.MAIN_TAB.getKey());
    static {
        REGISTRATE.setTooltipModifierFactory(item ->
                new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                        .andThen(TooltipModifier.mapNull(KineticStats.create(item)))
        );
    }

    public MechanicalTradingStation(IEventBus modEventBus, ModContainer modContainer) {
        REGISTRATE.registerEventListeners(modEventBus);

        ModDisplaySources.register();
        ModBlocks.register();
        ModBlockEntities.register();
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(ModDataGen::gatherData);



        registerLanguageKeys();

    }
    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        MechanicalTradingStationBlockEntity.registerCapabilities(event);
    }

    private static void registerLanguageKeys(){
        new RegistrateLangBuilder<>(ModConstants.MODID,registrate())
                .addJade(ModConstants.DISPLAY_NAME)
                .add("display_source.traded_items","Traded item count");
    }
    public static CreateRegistrate registrate() {
        return REGISTRATE;
    }
}
