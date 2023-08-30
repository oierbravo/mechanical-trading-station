package com.oierbravo.mechanical_trading_station.compat.jei;

import com.oierbravo.mechanical_trading_station.MechanicalTradingStation;
import com.simibubi.create.compat.jei.BlueprintTransferHandler;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
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

    private static final ResourceLocation ID = MechanicalTradingStation.asResource("jei_plugin");

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
    public void registerCategories(IRecipeCategoryRegistration registration) {
        loadCategories();
        registration.addRecipeCategories(modCategories.toArray(IRecipeCategory[]::new));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ingredientManager = registration.getIngredientManager();

        modCategories.forEach(c -> c.registerRecipes(registration));

    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        modCategories.forEach(c -> c.registerCatalysts(registration));
    }



}
