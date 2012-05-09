package com.aloe.mtm.data;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: mcaulfie
 * Date: 5/5/11
 * Time: 6:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class Roadblock {

    public static enum Status {
        WAITING, DONE;
    }

    private Date date = new Date();
    private String desc = "";
    private Status status = Status.WAITING;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Roadblock clone() {
        Roadblock clone = new Roadblock();
        clone.date = this.date;
        clone.desc = this.desc;
        clone.status = this.status;
        return clone;
    }
}
