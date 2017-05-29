package io.phoenyx.sail;

class Goal {
    private int id;
    private String title, description, date, notify;
    private boolean starred, completed;

    Goal(int id, String title, String description, String date, boolean starred, boolean completed, String notify) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.starred = starred;
        this.completed = completed;
        this.notify = notify;
    }

    Goal(String title, String description, String date, boolean starred, boolean completed, String notify) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.starred = starred;
        this.completed = completed;
        this.notify = notify;
    }

    String getNotify() {
        return notify;
    }

    void setNotify(String notify) {
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

    boolean isStarred() {
        return starred;
    }

    void setStarred(boolean starred) {
        this.starred = starred;
    }

    boolean isCompleted() {
        return completed;
    }

    void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
