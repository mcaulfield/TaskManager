package com.aloe.mtm.control;

import com.aloe.mtm.control.event.ControlEvent;

/**
 * Created by IntelliJ IDEA.
 * User: mcaulfie
 * Date: 5/10/11
 * Time: 10:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class TaskMenuBarControl {

    private MTMControl control;

    public TaskMenuBarControl(MTMControl control) {
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

    public void selectNextTask() {
        control.scheduleControlEvent(ControlEvent.SELECT_NEXT_TASK);
    }

    public void selectPrevTask() {
        control.scheduleControlEvent(ControlEvent.SELECT_PREV_TASK);
    }

    public void newTaskList() {
        control.scheduleControlEvent(ControlEvent.NEW_LIST);
    }

    public void saveTaskList() {
        control.scheduleControlEvent(ControlEvent.SAVE_LIST);
    }

    public void loadTaskList() {
        control.scheduleControlEvent(ControlEvent.LOAD_LIST);
    }

    public void editWorkflows() {
        control.scheduleControlEvent(ControlEvent.EDIT_WORKFLOWS);
    }

    public void undoAction() {
        control.scheduleControlEvent(ControlEvent.UNDO);
    }
}
