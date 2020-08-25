package com.machine;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.FileReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

public class CoffeeMachine {

    private Long outletNumber;                        //Number of outlet
    private Map<String, Beverage> beverageMap;        //Beverages that can ne made by coffee machine
    private Map<String, Ingredient> ingredientMap;    //Ingredients present in coffee machine
    private Indicator itemIndicator;

    private static final String MAKE_BEVERAGES= "make_beverages";
    private static final String REFILL_ITEMS= "refill_items";
    private static final String FILE_PATH = "./resources/CoffeeMachineTestCases-2.json";

    public CoffeeMachine(){
        beverageMap = new HashMap<>();
        ingredientMap = new HashMap<>();
    }

    public static void main(String[] args){
        CoffeeMachine coffeeMachine = new CoffeeMachine();
        try {
            coffeeMachine.start();
            coffeeMachine.useMachine();
        }catch (InterruptedException | IOException | ParseException ex){
            System.out.println("Got interruption while using coffee Machine");
        }
    }

    /**
     * This method turn on the machine and fill the items quantities and add recipes for different beverages
     *
     * @param
     * @throws ParseException
     * @throws IOException
     */
    public void start() throws IOException, ParseException {
        itemIndicator = new Indicator();
        Object obj = new JSONParser().parse(new FileReader(FILE_PATH));
        JSONObject jo = (JSONObject)obj;
        JSONObject machine = (JSONObject)jo.get("machine");
        Map<String,Long> outletMap = (Map)machine.get("outlets");
        this.outletNumber = outletMap.get("count_n");
        Map<String,Long> totalItemQuantity = (Map)machine.get("total_items_quantity");
        for (Map.Entry<String, Long> entry : totalItemQuantity.entrySet()) {
            addIngredients(entry.getKey(), entry.getValue());
        }
        itemIndicator.prepareItemMap(ingredientMap);
        Map<String,Map<String,Long>> allBeverages = (Map)machine.get("beverages");
        for (Map.Entry<String, Map<String, Long>> entry : allBeverages.entrySet()) {
            addBeverages(entry.getKey(), entry.getValue());
        }
        itemIndicator.start();
    }

    /**
     * This method take the order which can be making beverages or refilling any ingredient.
     *
     * @param
     * @throws ParseException
     * @throws IOException
     * @throws InterruptedException
     */
    public void useMachine() throws IOException, ParseException, InterruptedException {
        Object obj = new JSONParser().parse(new FileReader(FILE_PATH));
        JSONObject jo = (JSONObject)obj;
        JSONObject machine = (JSONObject)jo.get("machine");
        JSONArray orders = (JSONArray)machine.get("orders");
        List<String> result;
        for (Object order : orders) {
            JSONObject orderObject = (JSONObject) order;
            String orderType = (String) orderObject.get("order_type");
            switch (orderType) {
                case MAKE_BEVERAGES:
                    List<String> beveragesTypes = (List<String>) orderObject.get("order_items");
                     result= makeBeverage(beveragesTypes);
                    printResult(result);
                    break;
                case REFILL_ITEMS:
                    Map<String, Long> ingredientQuantities = (Map) orderObject.get("order_items");
                    result = refillIngredients(ingredientQuantities);
                    printResult(result);

                    break;
                default:
                    break;
            }
            // Added a sleep to see gap different orders in console.
            Thread.sleep(1000);
        }
        // setting false when we are done with orders
        itemIndicator.setShowIndicator(false);
    }

    /**
     * This method  makes beverages .
     *
     * @param list of beverages to be made
     * @return list of msg whether beverages are processed or not
     */
    public List<String> makeBeverage(List<String> beverages){
        List<String> makeResult = new ArrayList<>();
        int totalBeveragesMade = 0;
        for(String item : beverages){
            if(totalBeveragesMade != outletNumber) {
                makeResult.add(make(item));
                totalBeveragesMade++;
            }else{
                makeResult.add(item + " cannot be prepared because no outlet is free.");
            }
        }
        return makeResult;
    }

    /**
     * This method  refills ingredients .
     *
     * @param map of ingredients and their quantity
     * @return list of result message
     */
    public List<String>  refillIngredients(Map<String, Long> ingredientQuantities){
        List<String> result = new ArrayList<>();
        for(Map.Entry<String, Long> entry : ingredientQuantities.entrySet()) {
            Ingredient ingredient = ingredientMap.get(entry.getKey());
            ingredient.refill(entry.getValue());
        }
        result.add("Ingredients are successfully refilled");
        return result;
    }

    /**
     * This method used to print the result of making beverages
     *
     * @param list of result message
     */
    private void printResult(List<String> result){
        result.forEach(msg->{
            System.out.println(msg);
        });
    }

    /**
     * This method  is to add the beverages recipe in the system.
     *
     * @param map of ingredients and their quantity
     */
    private void addBeverages(String beverageName, Map<String, Long> recipesMap){
        Beverage beverage = new Beverage(beverageName , recipesMap);
        if(!beverageMap.containsKey(beverageName)){
            beverageMap.put(beverageName, beverage);
            itemIndicator.setThreshold(recipesMap);
        }
    }

    /**
     * This method  is to add the Ingredients in the coffeeMachine at the time when machine is started
     *
     * @param map of ingredients and their quantity
     */
    private void addIngredients(String itemName, Long quantity){
        Ingredient item = new Ingredient(itemName, quantity);
        if(!ingredientMap.containsKey(item.getName())){
            ingredientMap.put(item.getName(), item);
        }
    }

    /**
     * This method  is to make beverage
     *
     * @param beverageName
     */
    private String make(String beverageName){
        StringBuilder result = new StringBuilder();
        if(beverageMap.containsKey(beverageName)) {
            Beverage beverage = beverageMap.get(beverageName);
            Map<String, Long> recipe = beverage.getRecipe();
            result.append(checkAllIngredientsAreSufficient(beverageName, recipe));
            if (result.length() > 0) {
                return result.toString();
            }
            for (Map.Entry<String, Long> entry : recipe.entrySet()) {
                String ingredientName = entry.getKey();
                Long ingredientRequired = entry.getValue();
                Ingredient item = ingredientMap.get(ingredientName);
                item.consume(ingredientRequired);
            }
            result.append(beverageName).append(" is prepared.");
        }else{
            result.append(beverageName).append(" is not present in coffee machine.");
        }
        return result.toString();
    }

    /**
     * This method  is to add the Ingredients in the coffeeMachine at the time when machine is started
     *
     * @param beverageName recipe
     * @return String
     */
    private String checkAllIngredientsAreSufficient(String beverageName, Map<String,Long> recipe){
        ArrayList<String> itemUnavailable = new ArrayList<>();
        ArrayList<String> itemNotEnough = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        for(Map.Entry<String, Long> entry : recipe.entrySet()){
            String ingredientName = entry.getKey();
            Long ingredientRequired = entry.getValue();
            if(ingredientMap.containsKey(ingredientName)) {
                Ingredient item = ingredientMap.get(ingredientName);
                if (item.isAvailable()) {
                    if (!item.isEnoughQuantity(ingredientRequired)) {
                        itemNotEnough.add(item.getName());
                    }
                } else {
                    itemUnavailable.add(item.getName());
                }
            }else{
                itemUnavailable.add(ingredientName);
            }
        }
        if(!itemUnavailable.isEmpty() || !itemNotEnough.isEmpty()){
            result.append(beverageName).append(" cannot be prepared because ");
            for(String item : itemUnavailable){
                result.append(item).append(" is not available, ");
            }
            for(String item : itemNotEnough){
                result.append(item).append(" is not sufficient, ");
            }
            result.delete(result.length()-2, result.length());
        }
        return result.toString();
    }

}
