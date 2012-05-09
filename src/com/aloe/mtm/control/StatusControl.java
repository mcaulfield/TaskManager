package com.aloe.mtm.control;

import com.aloe.mtm.control.event.ControlEventAdapter;
import com.aloe.mtm.gui.StatusPanel;

/**
 * Created by IntelliJ IDEA.
 * User: mcaulfie
 * Date: 5/11/11
 * Time: 11:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class StatusControl extends ControlEventAdapter {

    private MTMControl control;
    private StatusPanel gui;

    public StatusControl(MTMControl control) {
        this.control = control;
        control.addControlEventListener(this);
    }

    public void registerGui(StatusPanel gui) {
        this.gui = gui;
    }

    public void handleStatusEvent(String status, double progress) {
        if (gui == null) {
            return;
        }
        gui.setStatusInfo(status, progress);
    }
}
