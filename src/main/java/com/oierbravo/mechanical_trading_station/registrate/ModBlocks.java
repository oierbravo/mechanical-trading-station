package com.oierbravo.mechanical_trading_station.registrate;

import com.oierbravo.mechanical_trading_station.MechanicalTradingStation;
import com.oierbravo.mechanical_trading_station.content.machines.mechanical_trading_station.MechanicalTradingStationBlock;
import com.oierbravo.mechanical_trading_station.infrastructure.config.ModStress;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;

import static com.simibubi.create.api.behaviour.display.DisplaySource.displaySource;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public class ModBlocks {

    public static final CreateRegistrate REGISTRATE = MechanicalTradingStation.registrate();

    public static final BlockEntry<MechanicalTradingStationBlock> MECHANICAL_TRADING_STATION = REGISTRATE.block("mechanical_trading_station", MechanicalTradingStationBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .lang("Mechanical Trading Station")
            .transform(pickaxeOnly())
            //.blockstate(new MechanicalTradingStationModelGen()::generate)
            .blockstate((ctx, prov) ->
                    prov.getVariantBuilder(ctx.getEntry()).forAllStates(state -> {
                        String modelFileName = "mechanical_trading_station:block/mechanical_trading_station/block";
                        if(state.getValue(BlockStateProperties.POWERED))
                            modelFileName += "_powered";
                        if(state.getValue(BlockStateProperties.LIT))
                            modelFileName += "_lit";
                        return ConfiguredModel.builder().modelFile(prov.models().getExistingFile(ResourceLocation.tryParse(modelFileName)))
                                .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360).build();

                    })
            )
            .transform(displaySource(ModDisplaySources.TRADED_ITEMS_COUNT))

            // .onRegister(assignDataBehaviour(new TradedItemCountDisplaySource(), "traded_items"))
            .transform(ModStress.setImpact(4.0))
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<MechanicalTradingStationBlock> MECHANICAL_TRADING_STATION_UNBREAKABLE = REGISTRATE.block("mechanical_trading_station_unbreakable", MechanicalTradingStationBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .lang("Mechanical Trading Station (Unbreakable)")
            .transform(pickaxeOnly())
            .blockstate((ctx, prov) ->
                    prov.getVariantBuilder(ctx.getEntry()).forAllStates(state -> {
                        String modelFileName = "mechanical_trading_station:block/mechanical_trading_station/block";
                        if(state.getValue(BlockStateProperties.POWERED))
                            modelFileName += "_powered";
                        if(state.getValue(BlockStateProperties.LIT))
                            modelFileName += "_lit";
                        return ConfiguredModel.builder().modelFile(prov.models().getExistingFile(ResourceLocation.tryParse(modelFileName)))
                                .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360).build();

                    })
            )
            .transform(displaySource(ModDisplaySources.TRADED_ITEMS_COUNT))            .transform(ModStress.setImpact(4.0))
            .item()
            .transform(customItemModel("mechanical_trading_station/item"))
            .register();

    public static void register() {}

}
