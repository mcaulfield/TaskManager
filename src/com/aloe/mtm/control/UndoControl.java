package com.aloe.mtm.control;

import com.aloe.mtm.control.event.ControlEvent;
import com.aloe.mtm.control.event.ControlEventAdapter;
import com.aloe.mtm.control.event.TaskControlEvent;
import com.aloe.mtm.data.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * User: mcaulfie
 * Date: 5/11/11
 * Time: 11:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class UndoControl extends ControlEventAdapter {

    private MTMControl control;

    private Stack<ControlEvent> events = new Stack<ControlEvent>();

    public UndoControl(MTMControl control) {
        this.control = control;
        control.addControlEventListener(this);
    }

    public void handleNewTaskCreatedEvent(Task newTask) {
        TaskControlEvent e = new TaskControlEvent(ControlEvent.Type.TASK_DELETED, newTask);
        synchronized (events) {
            events.push(e);
        }
    }

    public void handleNewTasksCreatedEvent(List<Task> newTasks) {
        for (Task t : newTasks) {
            handleNewTaskCreatedEvent(t);
        }
    }

    public void handleTaskDeletedEvent(Task deletedTask) {
        TaskControlEvent e = new TaskControlEvent(ControlEvent.Type.TASK_CREATED, deletedTask);
        synchronized (events) {
            events.push(e);
        }
    }


    public void handleUndoEvent() {
        ControlEvent e = null;
        synchronized (events) {
            if (events.size() == 0) {
                return;
            }
            e = events.pop();
        }
        control.scheduleControlEvent(e, this);
    }
}
