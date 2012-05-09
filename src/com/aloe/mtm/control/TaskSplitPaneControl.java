package com.aloe.mtm.control;

import com.aloe.mtm.control.event.ControlEventAdapter;
import com.aloe.mtm.data.Task;
import com.aloe.mtm.gui.TaskSplitPane;

/**
 * Created by IntelliJ IDEA.
 * User: mcaulfie
 * Date: 5/18/11
 * Time: 3:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class TaskSplitPaneControl extends ControlEventAdapter {

    private MTMControl control;
    private TaskSplitPane gui;

    public TaskSplitPaneControl(MTMControl control) {
        this.control = control;
        control.addControlEventListener(this);
    }

    public void registerGui(TaskSplitPane gui) {
        this.gui = gui;
    }

    public void handleTaskSelectedEvent(Task task) {
        if (gui == null) {
            return;
        }
        if (task == null) {
            gui.hideDetailsPanel();
        } else {
            gui.showDetailsPane();
        }
    }

}
