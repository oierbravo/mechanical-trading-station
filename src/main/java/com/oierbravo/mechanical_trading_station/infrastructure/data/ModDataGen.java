package com.oierbravo.mechanical_trading_station.infrastructure.data;

import com.oierbravo.mechanical_trading_station.MechanicalTradingStation;
import com.tterrag.registrate.providers.RegistrateDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

import static com.oierbravo.mechanical_trading_station.ModConstants.MODID;

public class ModDataGen {
    public static void gatherData(GatherDataEvent event) {

        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();


        if (event.includeServer()) {

        }
        event.getGenerator().addProvider(true, MechanicalTradingStation.registrate().setDataProvider(new RegistrateDataProvider(MechanicalTradingStation.registrate(), MODID, event)));

    }
}