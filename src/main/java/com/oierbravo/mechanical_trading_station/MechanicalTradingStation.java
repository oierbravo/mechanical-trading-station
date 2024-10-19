package com.oierbravo.mechanical_trading_station;

import com.mojang.logging.LogUtils;
import com.oierbravo.mechanical_trading_station.registrate.ModBlockEntities;
import com.oierbravo.mechanical_trading_station.registrate.ModBlocks;
import com.oierbravo.trading_station.TradingStation;
import com.oierbravo.trading_station.registrate.ModCreativeTab;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MechanicalTradingStation.MODID)
public class MechanicalTradingStation
{
    public static final String MODID = "mechanical_trading_station";
    public static final String DISPLAY_NAME = "Mechanical Trading Station";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);

    static {
        REGISTRATE.setTooltipModifierFactory(item -> {
            return new ItemDescription.Modifier(item, TooltipHelper.Palette.STANDARD_CREATE)
                    .andThen(TooltipModifier.mapNull(KineticStats.create(item)));
        });
    }
    public MechanicalTradingStation()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        REGISTRATE.registerEventListeners(modEventBus);


        ModBlocks.register();
        ModBlockEntities.register();

        modEventBus.addListener(this::addCreative);

        modEventBus.addListener(EventPriority.LOWEST, MechanicalTradingStation::gatherData);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == ModCreativeTab.MAIN_TAB.getKey()){
            for (RegistryEntry<Item> entry : MechanicalTradingStation.registrate().getAll(Registries.ITEM)) {
                event.accept(entry.get());
            }
        }
    }
    public static void gatherData(GatherDataEvent event) {
        //TagGen.datagen();
        DataGenerator gen = event.getGenerator();
        if (event.includeClient()) {
            //gen.addProvider(true, new LangMerger(gen, MODID, DISPLAY_NAME, ModLangPartials.values()));
        }
        if (event.includeServer()) {

        }
    }
    public static CreateRegistrate registrate() {
        return REGISTRATE;
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MODID, path);
    }

}
