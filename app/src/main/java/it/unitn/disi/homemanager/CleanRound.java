package it.unitn.disi.homemanager;

/**
 * Created by piermaria on 25/05/17.
 */

public class CleanRound {
    private String name;
    private String facebook_id;
    private String description;
    private boolean done;



    public String getFacebook_id() {
        return facebook_id;
    }

    public void setFacebook_id(String facebook_id) {this.facebook_id = facebook_id;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCleanDescription() {
        return description;
    }

    public void setCleanDescription(String description) {
        this.description = description;
    }

    public boolean getDone () {return done;}

    public void setDone (boolean done){this.done = done;}
}
