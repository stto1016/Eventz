package de.bpsz.android.eventz;

/**
 * Created by Calvin on 28.10.2014.
 */

public class Event {

    private String title;
    private String content;

    public Event(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return title;
    }

}
