package it.unitn.disi.homemanager;

/**
 * Created by piermaria on 25/05/17.
 */

public class Contact {

    private String name;
    private String number;
    private String id;


    public String getId() {return id;}

    public void setId(String id) {this.id = id;}

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {this.number = number;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
