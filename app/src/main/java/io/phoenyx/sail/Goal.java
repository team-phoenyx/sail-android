package io.phoenyx.sail;

public class Goal {
    private int id;
    private String title, description, date, notify;
    private boolean starred, completed;

    public Goal(int id, String title, String description, String date, boolean starred, boolean completed, String notify) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.starred = starred;
        this.completed = completed;
        this.notify = notify;
    }

    public Goal(String title, String description, String date, boolean starred, boolean completed, String notify) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.starred = starred;
        this.completed = completed;
        this.notify = notify;
    }

    public String getNotify() {
        return notify;
    }

    public void setNotify(String notify) {
        this.notify = notify;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isStarred() {
        return starred;
    }

    public void setStarred(boolean starred) {
        this.starred = starred;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
