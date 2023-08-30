package com.oierbravo.mechanical_trading_station.registrate;

import com.oierbravo.mechanical_trading_station.MechanicalTradingStation;
import com.oierbravo.mechanical_trading_station.content.machines.mechanical_trading_station.MechanicalTradingStationBlock;
import com.oierbravo.mechanical_trading_station.content.machines.mechanical_trading_station.TradedItemCountDisplaySource;
import com.oierbravo.trading_station.registrate.ModCreativeTab;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.content.redstone.displayLink.source.AccumulatedItemCountDisplaySource;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.function.Function;

import static com.simibubi.create.content.redstone.displayLink.AllDisplayBehaviours.assignDataBehaviour;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public class ModBlocks {

    public static final CreateRegistrate REGISTRATE = MechanicalTradingStation.registrate().creativeModeTab(() -> ModCreativeTab.MAIN);

    public static final BlockEntry<MechanicalTradingStationBlock> MECHANICAL_TRADING_STATION = REGISTRATE.block("mechanical_trading_station", MechanicalTradingStationBlock::new)
            .initialProperties(SharedProperties::stone)
            .lang("Mechanical Trading Station")
            .properties(p -> p.color(MaterialColor.METAL))
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
            .onRegister(assignDataBehaviour(new TradedItemCountDisplaySource(), "traded_items"))
            .transform(BlockStressDefaults.setImpact(4.0))
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<MechanicalTradingStationBlock> MECHANICAL_TRADING_STATION_UNBREAKABLE = REGISTRATE.block("mechanical_trading_station_unbreakable", MechanicalTradingStationBlock::new)
            .initialProperties(SharedProperties::stone)
            .lang("Mechanical Trading Station (Unbreakable)")
            .properties(p -> p.color(MaterialColor.METAL))
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
            .onRegister(assignDataBehaviour(new TradedItemCountDisplaySource(), "traded_items"))
            .transform(BlockStressDefaults.setImpact(4.0))
            .item()
            .transform(customItemModel("mechanical_trading_station/item"))
            .register();

    public static void register() {}

}
