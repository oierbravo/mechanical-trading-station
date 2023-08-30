package com.oierbravo.mechanical_trading_station.content.machines.mechanical_trading_station;

import com.oierbravo.trading_station.TradingStation;
import com.oierbravo.trading_station.foundation.gui.AbstractTradingScreen;
import com.oierbravo.trading_station.foundation.gui.Coords2D;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MechanicalTradingStationScreen extends AbstractTradingScreen<MechanicalTradingStationMenu> {

    public Coords2D getProgressArrowCoords() {
        return Coords2D.of(79, 47);
    }


    protected Coords2D getTargetSelectButtonCoords() {
        return Coords2D.of(131, 31);
    }

    protected Coords2D getRedstoneButtonCoords() { return Coords2D.of(151,31); }

    private static final ResourceLocation TEXTURE = TradingStation.asResource("textures/gui/trading_station.png");

    public MechanicalTradingStationScreen(MechanicalTradingStationMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);

    }
    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }
}