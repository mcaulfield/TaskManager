package com.aloe.mtm.control;

import com.aloe.mtm.control.event.ControlEvent;

/**
 * Created by IntelliJ IDEA.
 * User: mcaulfie
 * Date: 5/6/11
 * Time: 3:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class TaskToolBarControl {

    private MTMControl control;

    public TaskToolBarControl(MTMControl control) {
        this.control = control;
    }

    public void addNewTask() {
        control.scheduleControlEvent(ControlEvent.ADD_NEW_TASK);
    }

    public void removeSelectedTask() {
        control.scheduleControlEvent(ControlEvent.REMOVE_SELECTED_TASK);
    }

    public void toggleTaskSizes() {
        control.scheduleControlEvent(ControlEvent.TOGGLE_TASK_SIZES);
    }

    public void editWorkflows() {
        control.scheduleControlEvent(ControlEvent.EDIT_WORKFLOWS);
    }

    public void saveTaskList() {
        control.scheduleControlEvent(ControlEvent.SAVE_LIST);
    }

    public void loadTaskList() {
        control.scheduleControlEvent(ControlEvent.LOAD_LIST);
    }
}
