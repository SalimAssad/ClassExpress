package com.chacostak.salim.classexpress.Calendar.Data;

/**
 * Created by Salim on 15/12/2015.
 */
public class HomeworkData extends CalendarData {

    private String course;
    private String description;
    private int priority;

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
