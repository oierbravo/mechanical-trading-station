package com.oierbravo.mechanical_trading_station.content.machines.mechanical_trading_station;

import com.oierbravo.mechanical_trading_station.registrate.ModBlocks;
import com.oierbravo.mechanical_trading_station.registrate.ModMenus;
import com.oierbravo.trading_station.foundation.gui.AbstractTradingMenu;
import com.oierbravo.trading_station.foundation.gui.Coords2D;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class MechanicalTradingStationMenu extends AbstractTradingMenu {

    public MechanicalTradingStationMenu(int pContainerId, Inventory pInv, BlockEntity pBlockEntity, ContainerData pData) {
        super(ModMenus.MECHANICAL_TRADING_STATION.get(), pContainerId, pInv, pBlockEntity, pData);
    }

    public MechanicalTradingStationMenu(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        super(ModMenus.MECHANICAL_TRADING_STATION.get(), pContainerId, inventory, buf);
    }

    public static MechanicalTradingStationMenu factory(@Nullable MenuType<MechanicalTradingStationMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        return new MechanicalTradingStationMenu(pContainerId, inventory, buf);
    }
    @Override
    public boolean stillValid(Player pPlayer) {
        if(stillValid(ContainerLevelAccess.create(level, getBlockPos()), pPlayer, ModBlocks.MECHANICAL_TRADING_STATION.get()))
            return true;
        if(stillValid(ContainerLevelAccess.create(level, getBlockPos()), pPlayer, ModBlocks.MECHANICAL_TRADING_STATION_UNBREAKABLE.get()))
            return true;
        return false;
    }
    @Override
    public Coords2D[] getInputSlotCoords() {
        return new Coords2D[]{
                Coords2D.of(19,49),
                Coords2D.of(42,49)
        };
    }
    @Override
    public Coords2D[] getInputRecipeCoords() {
        return new Coords2D[]{
                Coords2D.of(19,25),
                Coords2D.of(42,25)
        };
    }
    @Override
    public Coords2D getOutputSlotCoords() {
        return Coords2D.of(131,49);
    }

    @Override
    public Coords2D getTargetSlotCoords() {
        return Coords2D.of(87,40);
    }


}
