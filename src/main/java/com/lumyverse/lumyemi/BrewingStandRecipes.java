package com.lumyverse.lumyemi;

import java.util.List;
import org.jetbrains.annotations.Nullable;

import com.cobblemon.mod.common.item.crafting.brewingstand.BrewingStandRecipe;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;

public class BrewingStandRecipes {
    private final EmiRegistry m_reg;
    private final EmiRecipeCategory m_cat;

    public BrewingStandRecipes(EmiRegistry registry) {
        m_cat = new EmiRecipeCategory(new Identifier("cobblemon", "brewing_stand"), EmiStack.of(Items.BREWING_STAND));
        m_reg = registry;

        registry.addCategory(m_cat);
        registry.addWorkstation(m_cat, EmiStack.of(Items.BREWING_STAND));
    }

    public void register(RecipeEntry<?> recipeEntry) {
        if (recipeEntry.value() instanceof BrewingStandRecipe recipe) {
            m_reg.addRecipe(new EmiRecipe() {

                @Override
                public EmiRecipeCategory getCategory() { return m_cat; }

                @Override
                public @Nullable Identifier getId() { return recipeEntry.id(); }

                @Override
                public int getDisplayWidth() { return 120; }

                @Override
                public int getDisplayHeight() { return 60; }

                @Override
                public List<EmiIngredient> getInputs() {
                    return List.of(
                        EmiIngredient.of(recipe.getInput()),
                        EmiStack.of(Items.BLAZE_POWDER),
                        EmiIngredient.of(recipe.getBottle())
                    );
                }

                @Override
                public List<EmiStack> getOutputs() {
                    return List.of(EmiStack.of(recipeEntry.value().getResult(MinecraftClient.getInstance().world.getRegistryManager())));
                }

                @Override
                public void addWidgets(WidgetHolder widgets) {
                    widgets.addSlot(getInputs().get(1), 0, 4); // Blaze Powder slot
                    widgets.addSlot(getInputs().get(0), 50, 4); // Main Ingredient slot
                    widgets.addSlot(getInputs().get(2), 27, 38); // Bottle slot 1
                    widgets.addSlot(getInputs().get(2), 50, 45); // Bottle slot 2
                    widgets.addSlot(getInputs().get(2), 73, 38); // Bottle slot 3

                    widgets.addFillingArrow(76, 18, 20000); // Brewing takes around 20 seconds
                    widgets.addSlot(getOutputs().get(0), 100, 18).recipeContext(this); // Result slot
                }
            });
        }
    }
}