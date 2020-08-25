package com.machine;

import java.util.HashMap;
import java.util.Map;

public class Indicator extends Thread{

    Map<String, Ingredient> items;            //hold reference of the ingredients used to check current quantity
    Map<String, Long> thresholdQuantities;    //hold threshold value of the ingredients that need to be present in machine
    boolean showIndicator;                    //to show indicator or not

    public Indicator(){
        thresholdQuantities = new HashMap<>();
        items = new HashMap<>();
        showIndicator = false;
    }

    public void run(){
        showIndicator = true;
        try {
            checkItemQuantities();
        } catch (InterruptedException e) {
            System.out.println("Caught error while showing notification");
        }
    }

    /**
     * This method check the ingredients and show notification if any one of them is less than threshold
     * @param
     * @return
     */
    private void checkItemQuantities() throws InterruptedException {
        while(showIndicator){
            for(Map.Entry<String, Long> entry : thresholdQuantities.entrySet()){
                String itemName = entry.getKey();
                Long thresholdValue = entry.getValue();
                if(items.containsKey(itemName) && items.get(itemName).getQuantity() < thresholdValue){
                    System.out.println("[NOTIFICATION]" + itemName + " is running low.");
                }
            }
            Thread.sleep(2000);
        }
    }

    /**
     * This method is set the show indicator flag
     * @param currState
     */
    public void setShowIndicator(boolean currState){
        showIndicator = currState;
    }

    /**
     * This method is check and set the threshold value of the ingredient required in coffee machine
     * @param recipe
     */
    public void setThreshold(Map<String , Long> recipe){
        for(Map.Entry<String, Long> entry : recipe.entrySet()) {
            String itemName = entry.getKey();
            Long quantity = entry.getValue();
            if(thresholdQuantities.containsKey(itemName)){
                if(thresholdQuantities.get(itemName) < quantity){
                    thresholdQuantities.put(itemName, quantity);
                }
            }else{
                thresholdQuantities.put(itemName, quantity);
            }
        }
    }

    /**
     * This method is prepare the ingredient map
     * @param itemMap
     */
    public void prepareItemMap(Map<String, Ingredient> itemMap){
        for(Map.Entry<String, Ingredient> entry : itemMap.entrySet()) {
            String itemName = entry.getKey();
            Ingredient ingredient = entry.getValue();
            items.put(itemName, ingredient);
        }
    }
}
