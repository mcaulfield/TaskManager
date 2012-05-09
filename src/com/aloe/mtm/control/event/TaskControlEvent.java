package com.aloe.mtm.control.event;

import com.aloe.mtm.data.Task;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mcaulfie
 * Date: 5/9/11
 * Time: 12:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class TaskControlEvent extends ControlEvent {

    private List<Task> taskList = new LinkedList<Task>();

    public TaskControlEvent(Type type, Task task) {
        super(type);
        this.taskList.add(task);
    }

    public TaskControlEvent(Type type, List<Task> taskList) {
        super(type);
        this.taskList.addAll(taskList);
    }

    public Task getTask() {
        return taskList.get(0);
    }

    public List<Task> getTaskList() {
        return taskList;
    }
}
