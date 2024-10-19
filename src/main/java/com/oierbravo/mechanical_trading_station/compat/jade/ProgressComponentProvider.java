package com.oierbravo.mechanical_trading_station.compat.jade;

import com.oierbravo.mechanical_trading_station.content.machines.mechanical_trading_station.MechanicalTradingStationBlockEntity;
import com.oierbravo.mechanical_trading_station.foundatation.utility.ModLang;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.api.ui.IProgressStyle;
import snownee.jade.util.Color;

public class ProgressComponentProvider  implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        //CompoundTag serverData = accessor.getServerData();
        if (accessor.getServerData().contains("mechanical_trading_station.progress")) {
            int progress = accessor.getServerData().getInt("mechanical_trading_station.progress");
            IElementHelper elementHelper = tooltip.getElementHelper();
            IProgressStyle progressStyle = elementHelper.progressStyle();
            if(progress > 0)
                tooltip.add(elementHelper.progress((float)progress / 100, ModLang.translate("mechanical_trading_station" + ".tooltip.progress", progress).component(),elementHelper.progressStyle().color(Color.hex("#FFFF00").toInt()), BoxStyle.DEFAULT,true));

        }

    }


    @Override
    public ResourceLocation getUid() {
        return MechanicalTradingStationPlugin.MOD_DATA;
    }


    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
        if(blockAccessor.getBlockEntity() instanceof MechanicalTradingStationBlockEntity tradingStationBlockEntity){
            compoundTag.putInt("mechanical_trading_station.progress",tradingStationBlockEntity.getProgressPercent());
        }
    }
}
