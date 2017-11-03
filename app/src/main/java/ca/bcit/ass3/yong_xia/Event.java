package ca.bcit.ass3.yong_xia;

/**
 * Created by Alex on 01/11/2017.
 */

public class Event {
    private String name;
    private String date;
    private String time;

    private String description;

    public Event(String name, String date, String time, String description) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

}
