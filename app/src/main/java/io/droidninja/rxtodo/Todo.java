package io.droidninja.rxtodo;

/**
 * Created by Zeeshan Shabbir on 12/24/2017.
 */

public class Todo {
    public String description;
    public boolean isCompleted;

    public Todo(String description, boolean isCompleted) {
        this.description = description;
        this.isCompleted = isCompleted;
    }
}
