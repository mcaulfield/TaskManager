package com.aloe.mtm.control.event;

/**
 * Created by IntelliJ IDEA.
 * User: mcaulfie
 * Date: 5/11/11
 * Time: 11:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class StatusControlEvent extends ControlEvent {

    private String status;
    private double progress;

    public StatusControlEvent(String status) {
        super(Type.STATUS);
        this.status = status;
        this.progress = 0;
    }

    public StatusControlEvent(String status, double progress) {
        super(Type.STATUS);
        this.status = status;
        this.progress = progress;
    }

    public String getStatus() {
        return status;
    }

    public double getProgress() {
        return progress;
    }

}
