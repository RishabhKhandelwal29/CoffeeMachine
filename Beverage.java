package com.machine;

import java.util.*;

public class Beverage {
    private String name;
    private Map<String,Long> recipe;

    public Beverage(String itemName, Map<String, Long> recipeMap){
        this.name = itemName;
        recipe = new HashMap<>();
        for (Map.Entry<String, Long> entry : recipeMap.entrySet()) {
            if (!recipe.containsKey(entry.getKey())) {
                recipe.put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * This method is used to get the recipe of the beverage
     *
     * @return map of Ingredients
     */
    public Map<String,Long> getRecipe(){
        return Collections.unmodifiableMap(recipe);
    }

    /**
     * This method is giv the beverage name
     *
     * @return beverage name
     */
    public String getName(){
        return name;
    }
}
