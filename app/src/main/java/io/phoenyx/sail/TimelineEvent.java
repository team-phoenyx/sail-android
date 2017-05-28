package io.phoenyx.sail;

public class TimelineEvent {
    private int id, month, day, year;
    private String title, description, date;
    String[] months = new String[]{"Jan.","Feb.","Mar.","Apr.","May","Jun.","Jul.","Aug.","Sep.","Oct.","Nov.","Dec."};

    public TimelineEvent(String title, int month, int day, int year, String description) {
        this.month = month;
        this.day = day;
        this.year = year;
        this.title = title;
        this.description = description;
        this.date = months[month - 1] + " " + day + " " + year;
    }

    public TimelineEvent(int id, String title, int month, int day, int year, String description) {
        this.id = id;
        this.month = month;
        this.day = day;
        this.year = year;
        this.title = title;
        this.description = description;
        this.date = months[month - 1] + " " + day + " " + year;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    //@param month should be from 1-12
    public void setDateWithValues(int month, int day, int year) throws IllegalArgumentException {
        this.month = month;
        this.day = day;
        this.year = year;
        this.date = months[month - 1] + " " + day + " " + year;
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

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getYear() {
        return year;
    }
}
