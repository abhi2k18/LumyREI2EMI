package com.lumyverse.lumyemi;

import java.util.List;
import org.jetbrains.annotations.Nullable;


import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.client.MinecraftClient;

public class StarkForgeRecepies {
    private EmiRegistry m_reg;
    private EmiRecipeCategory m_cat;
    

    public StarkForgeRecepies(EmiRegistry registry){
        var identifier = new Identifier("lumymon", "stark_forging");
        var stack = EmiStack.of(new ItemStack(Registries.ITEM.get(identifier)));
        m_cat = new EmiRecipeCategory(identifier, stack);
        m_reg = registry;

        registry.addCategory(m_cat);
        registry.addWorkstation(m_cat, stack);
    }
    
   public void register(RecipeEntry<?> recipe) {
        System.err.println("Added Stark Recipe with ID: " + recipe.id());
        m_reg.addRecipe(new EmiRecipe() {

            @Override
            public EmiRecipeCategory getCategory() { return m_cat;}
            @Override
            public @Nullable Identifier getId() { return recipe.id(); }
            @Override
            public int getDisplayWidth() { return 120; }
            @Override
            public int getDisplayHeight() { return 60;}
            @Override
            public List<EmiIngredient> getInputs() {return recipe.value().getIngredients().stream().map(EmiIngredient::of).toList();}
            @Override
            public List<EmiStack> getOutputs() { return List.of(EmiStack.of(recipe.value().getResult(MinecraftClient.getInstance().world.getRegistryManager())));}


            @Override
            public void addWidgets(WidgetHolder widgets) {
                List<EmiIngredient> inputs = getInputs();
                for (int i = 0; i < inputs.size(); i++) {
                    int x = (i % 3) * 18;
                    int y = (i / 3) * 18;
                    widgets.addSlot(inputs.get(i), x, y);
                }
                
                widgets.addFillingArrow(60, 18, 2000);
                widgets.addSlot(getOutputs().get(0), 95, 18).recipeContext(this);
                widgets.addTexture(EmiRenderHelper.WIDGETS, 64, 1, 16, 16, 60, 40);
            }
                
        });

    }
}
