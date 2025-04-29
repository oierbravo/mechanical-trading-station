package com.oierbravo.mechanical_trading_station.compat.jei;

import com.oierbravo.mechanical_trading_station.ModConstants;
import com.oierbravo.mechanical_trading_station.registrate.ModBlocks;
import com.oierbravo.trading_station.compat.jei.TradingStationJEIPlugin;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JeiPlugin
@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
public class MechanicalTradingStationJEI implements IModPlugin {

    private static final ResourceLocation ID = ModConstants.asResource("jei_plugin");

    public IIngredientManager ingredientManager;
    private final List<CreateRecipeCategory<?>> modCategories = new ArrayList<>();


    private void loadCategories() {
        this.modCategories.clear();
        RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();

    }

    @Override
    @Nonnull
    public ResourceLocation getPluginUid() {
        return ID;
    }


    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.MECHANICAL_TRADING_STATION.get()), TradingStationJEIPlugin.TRADING_RECIPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.MECHANICAL_TRADING_STATION_UNBREAKABLE.get()), TradingStationJEIPlugin.TRADING_RECIPE);
    }


}
