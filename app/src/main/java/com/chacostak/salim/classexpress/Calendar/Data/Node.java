package com.chacostak.salim.classexpress.Calendar.Data;

/**
 * Created by Salim on 01/02/2016.
 */
public class Node {

    private String date;
    private String title;
    private Node nextNode = null;

    public Node getNextNode() {
        return nextNode;
    }

    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
