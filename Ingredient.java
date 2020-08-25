package com.machine;

public class Ingredient {
    private String name;
    private Long quantity;

    public Ingredient(String name){
        this.name = name;
    }

    public Ingredient(String itemName, Long quantity){
        this.name = itemName;
        this.quantity = quantity;
    }

    /**
     * This method is checks whether ingredient quantity is sufficient or not
     * @param amount
     * @return boolean
     */
    public boolean isEnoughQuantity(Long amount){
        return (quantity >= amount);
    }

    /**
     * This method is to consume ingredient
     * @param amount
     */
    public void consume(Long amount){
        quantity = quantity - amount;
    }

    /**
     * This method is uses to refill ingredient
     * @param amount
     */
    public void refill(Long amount) {
        if (amount > 0) {
            quantity = quantity + amount;
        }
    }

    /**
     * This method is checks whether ingredient quantity is available or not
     * @param
     * @return boolean
     */
    public boolean isAvailable(){
        return quantity > 0;
    }

    /**
     * This method is to get ingredient name
     * @param
     * @return boolean
     */
    public String getName(){
        return name;
    }

    /**
     * This method is used to get ingredient quantity
     * @param
     * @return Long
     */
    public Long getQuantity(){
        return quantity;
    }
}

