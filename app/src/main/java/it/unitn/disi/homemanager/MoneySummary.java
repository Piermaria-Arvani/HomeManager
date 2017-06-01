package it.unitn.disi.homemanager;

/**
 * Created by piermaria on 26/05/17.
 */

public class MoneySummary {
    private String fb_id;
    private String name;
    private String money;


    public String getFb_id() {return fb_id;}

    public void setFb_id(String fb_id){this.fb_id = fb_id;}

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {this.money = money;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
