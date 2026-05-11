package com.lumyverse.lumyemi;

import com.cobblemon.mod.common.item.crafting.brewingstand.BrewingStandRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BrewingStandDisplay extends BasicDisplay {

    public BrewingStandDisplay(RecipeEntry<BrewingStandRecipe> recipeEntry) {
        this(recipeEntry.value(), recipeEntry.id());
    }

    public BrewingStandDisplay(BrewingStandRecipe recipe, net.minecraft.util.Identifier id) {
        super(
                // Input: [Ingrediente], [Blaze Powder], [Pozioni]
                createInputs(recipe),
                // Output
                Collections.singletonList(EntryIngredients.of(recipe.getResult())),
                Optional.ofNullable(id)
        );
    }

    private static List<EntryIngredient> createInputs(BrewingStandRecipe recipe) {
        List<EntryIngredient> inputs = new ArrayList<>();

        // 1. Ingrediente Principale
        inputs.add(EntryIngredients.ofIngredient(recipe.getInput()));

        // 2. Blaze Powder (combustibile)
        inputs.add(EntryIngredients.of(Items.BLAZE_POWDER));

        // 3. Bottiglia/Pozione di partenza
        inputs.add(EntryIngredients.ofIngredient(recipe.getBottle()));

        return inputs;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return BrewingStandCategory.BREWING;
    }
}