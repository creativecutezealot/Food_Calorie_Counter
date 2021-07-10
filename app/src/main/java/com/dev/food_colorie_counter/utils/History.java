package com.dev.food_colorie_counter.utils;

public class History {

    private String food_ID;
    private String foodimage;
    private String timestatue;
    private String calorie;
    private String foodname;
    private String foodgroup;
    private String foodimageremain;
    private String calorie_remain;
    private String servingWeight;

    public History(String food_ID, String foodimage, String timestatue, String calorie, String foodname, String foodgroup, String foodimageremain, String calorie_remain, String servingWeight) {
        this.food_ID = food_ID;
        this.foodimage = foodimage;
        this.timestatue = timestatue;
        this.calorie = calorie;
        this.foodname = foodname;
        this.foodgroup = foodgroup;
        this.foodimageremain = foodimageremain;
        this.calorie_remain = calorie_remain;
        this.servingWeight = servingWeight;
    }

    public String getFood_ID() {
        return food_ID;
    }

    public void setFood_ID(String food_ID) {
        this.food_ID = food_ID;
    }

    public String getFoodimage(){
        return foodimage;
    }

    public void setFoodimage(String foodimage){
        this.foodimage = foodimage;
    }

    public String getTimestatue() {
        return timestatue;
    }

    public void setTimestatue(String timestatue) {
        this.timestatue = timestatue;
    }

    public String getCalorie() {
        return calorie;
    }

    public void setCalorie(String calorie) {
        this.calorie = calorie;
    }

    public String getFoodname() {
        return foodname;
    }

    public void setFoodname(String foodname) {
        this.foodname = foodname;
    }

    public String getFoodgroup() {
        return foodgroup;
    }

    public void setFoodgroup(String foodgroup) {
        this.foodgroup = foodgroup;
    }

    public String getFoodimageremain() {
        return foodimageremain;
    }

    public void setFoodimageremain(String foodimageremain) {
        this.foodimageremain = foodimageremain;
    }

    public String getCalorie_remain() {
        return calorie_remain;
    }

    public void setCalorie_remain(String calorie_remain) {
        this.calorie_remain = calorie_remain;
    }

    public String getServingWeight() {
        return servingWeight;
    }

    public void setServingWeight(String servingWeight) {
        this.servingWeight = servingWeight;
    }
}
