package it.unitn.disi.homemanager;

/**
 * Created by piermaria on 28/05/17.
 */

public class Expense {

    private String expenseDescription;
    private String name;
    private String money;
    private String date;



    public String getExpenseDescription() {
        return expenseDescription;
    }

    public void setExpenseDescription(String expenseDescription) {this.expenseDescription = expenseDescription;}

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {return date;}

    public void setDate(String date) {this.date = date;}
}
