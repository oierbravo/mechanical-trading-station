package com.oierbravo.mechanical_trading_station.content.machines.mechanical_trading_station;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlock;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.NumericSingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class TradedItemCountDisplaySource extends NumericSingleLineDisplaySource {

    @Override
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        return Component.translatable(String.valueOf(context.sourceConfig()
                .getInt("Traded")));
    }

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }

    public void itemReceived(DisplayLinkBlockEntity be, int amount) {
        if (be.getBlockState()
                .getOptionalValue(DisplayLinkBlock.POWERED)
                .orElse(true))
            return;

        int collected = be.getSourceConfig()
                .getInt("Traded");
        be.getSourceConfig()
                .putInt("Traded", collected + amount);
        be.updateGatheredData();
    }
    @Override
    protected String getTranslationKey() {
        return "traded_items";
    }

    @Override
    public void onSignalReset(DisplayLinkContext context) {
        context.sourceConfig()
                .remove("Traded");
    }
}
