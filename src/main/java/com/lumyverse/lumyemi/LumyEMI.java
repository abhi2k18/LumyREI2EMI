package com.lumyverse.lumyemi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import net.minecraft.recipe.RecipeManager;
public class LumyEMI implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        var cookingPot = new CookingPotRecepies(registry);
        var starkForg = new StarkForgeRecepies(registry);
        var brewingStand = new BrewingStandRecipes(registry);

        RecipeManager manager = registry.getRecipeManager();
        for(var r :manager.values()){
            String typeString = net.minecraft.registry.Registries.RECIPE_TYPE.getId(r.value().getType()).toString();
            if(typeString.contains("cooking_pot"))
                cookingPot.register(r);
            else if(typeString.contains("stark")) 
                starkForg.register(r);
            else if(typeString.contains("brewing_stand")) 
                brewingStand.register(r);
        }
    }
     
}