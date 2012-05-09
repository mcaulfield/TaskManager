package com.aloe.mtm.control;

import com.aloe.mtm.control.event.ControlEvent;
import com.aloe.mtm.control.event.ControlEventAdapter;
import com.aloe.mtm.control.event.TaskControlEvent;
import com.aloe.mtm.data.Task;
import com.aloe.mtm.gui.TaskListPanel;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: mcaulfie
 * Date: 5/6/11
 * Time: 12:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class TaskListControl extends ControlEventAdapter implements ListModel {

    private List<Task> tasks;
    private Task selected;
    private boolean largeMode = false;
    private LinkedList<ListDataListener> listeners = new LinkedList<ListDataListener>();
    private HashMap<Task, Integer> priorities = new HashMap<Task, Integer>();
    private HashMap<Task, Task.Status> statuses = new HashMap<Task, Task.Status>();

    private MTMControl control;
    private TaskListPanel gui;

    public TaskListControl(MTMControl control, List<Task> tasks) {
        this.tasks = tasks;
        this.control = control;
        control.addControlEventListener(this);
    }

    /** List model API **/

    public int getSize() {
        return tasks.size();
    }

    public Object getElementAt(int i) {
        for (Task t : tasks) {
            if (i-- == 0) {
                return t;
            }
        }
        return null;
    }

    public void addListDataListener(ListDataListener listDataListener) {
        synchronized (listeners) {
            listeners.add(listDataListener);
        }
    }

    public void removeListDataListener(ListDataListener listDataListener) {
        synchronized (listeners) {
            listeners.remove(listDataListener);
        }
    }

    private void fireListDataEvent(ListDataEvent event) {
        synchronized (listeners) {
            for (ListDataListener l : listeners) {
                switch (event.getType()) {
                    case ListDataEvent.CONTENTS_CHANGED:
                        l.contentsChanged(event); break;
                    case ListDataEvent.INTERVAL_ADDED:
                        l.intervalAdded(event); break;
                    case ListDataEvent.INTERVAL_REMOVED:
                        l.intervalRemoved(event); break;
                }
            }
        }
    }

    /** GUI API **/

    public void registerGui(TaskListPanel gui) {
        this.gui = gui;
    }

    public boolean isLargeMode() {
        return largeMode;
    }

    public void setSelected(Task t) {
        selected = t;
        control.scheduleControlEvent(new TaskControlEvent(ControlEvent.Type.TASK_SELECTED, selected), this);
    }

    public void removeSelectedTask() {
        control.scheduleControlEvent(ControlEvent.REMOVE_SELECTED_TASK);
    }

    public void addNewTask() {
        control.scheduleControlEvent(ControlEvent.ADD_NEW_TASK);
    }

    /** Control Event Handlers **/

    public void handleAddNewTaskEvent() {
        Task newTask = new Task();
        newTask.setName("New Task");
        // new task will be added to tasks when new task event received
        control.scheduleControlEvent(new TaskControlEvent(ControlEvent.Type.TASK_CREATED, newTask));
    }

    public void handleRemoveSelectedTaskEvent() {
        Task selection = this.selected;
        if (selection == null) {
            return;
        }
        // task deleted event will remove task from list
        control.scheduleControlEvent(new TaskControlEvent(ControlEvent.Type.TASK_DELETED, selection));
    }

    public void handleToggleSizesEvent() {
        largeMode = !largeMode;
        if (tasks.size() == 0) {
            return;
        }
        fireListDataEvent(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, tasks.size()-1));
    }

    public void handleNewTaskCreatedEvent(Task newTask) {
        ListDataEvent event = null;
        int index = 0;
        synchronized (tasks) {
            if (!tasks.contains(newTask)) {
                tasks.add(newTask);
                Collections.sort(tasks);
                priorities.put(newTask, newTask.getPriority());
                statuses.put(newTask, newTask.getStatus());
                index = indexOf(newTask);
                event = new ListDataEvent(this,ListDataEvent.INTERVAL_ADDED, index, index);
            } else {
                index = indexOf(newTask);
            }
        }
        if (event != null) {
            fireListDataEvent(event);
        }
        selected = newTask;
        if (gui != null) {
            gui.setSelectedIndex(index);
        }
    }

    public void handleNewTasksCreatedEvent(List<Task> newTasks) {
        ListDataEvent event;
        synchronized (tasks) {
            for (Task t : newTasks) {
                if (tasks.contains(t)) {
                    continue;
                }
                tasks.add(t);
                priorities.put(t, t.getPriority());
                statuses.put(t, t.getStatus());
            }
            Collections.sort(tasks);
            event = new ListDataEvent(this,ListDataEvent.CONTENTS_CHANGED,0,tasks.size());
        }
        if (event != null) {
            fireListDataEvent(event);
        }
    }

    public void handleTaskDeletedEvent(Task deletedTask) {
        ListDataEvent event;
        int index = 0;
        boolean selectedDeleted = selected == deletedTask;
        synchronized (tasks) {
            index = indexOf(deletedTask);
            if (index < 0) {
                return;
            }
            tasks.remove(deletedTask);
            Collections.sort(tasks);
            priorities.remove(deletedTask);
            statuses.remove(deletedTask);
            event = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index);
        }
        fireListDataEvent(event);
        if (selectedDeleted && gui != null) {
            if (getSize() > 0) {
                gui.setSelectedIndex(Math.min(getSize()-1, index));
            } else {
                gui.clearSelection();
            }
        }
    }

    public void handleTaskSelectedEvent(Task t) {
        if (selected == t) {
            return;
        }
        selected = t;
        int index = indexOf(t);
        if (index < 0) {
            return;
        }
        if (gui != null) {
            gui.setSelectedIndex(index);
        }
    }

    public void handleTaskUpdatedEvent(Task t) {
        int index = indexOf(t);
        if (index < 0) {
            return;
        }
        if (priorities.get(t) == t.getPriority() && statuses.get(t) == t.getStatus()) {
            fireListDataEvent(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index, index));
            return;
        }
        synchronized (tasks) {
            Collections.sort(tasks);
            index = indexOf(t);
        }
        if (gui != null) {
            gui.setFocusable(false);
            fireListDataEvent(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, getSize() - 1));
            gui.setSelectedIndex(index);
            gui.setFocusable(true);
        }
    }

    public void handleSelectNextTaskEvent() {
        int max = getSize();
        if (max == 0) {
            return;
        }
        int newIndex = 0;
        if (selected != null) {
            newIndex = indexOf(selected);
            newIndex = Math.min(newIndex+1, max-1);
        }
        if (gui != null) {
            gui.setFocusable(false);
            gui.setSelectedIndex(newIndex);
            gui.setFocusable(true);
        }
    }

    public void handleSelectPrevTaskEvent() {
        int max = getSize();
        if (max == 0) {
            return;
        }
        int newIndex = 0;
        if (selected != null) {
            newIndex = indexOf(selected);
            newIndex = Math.max(newIndex-1, 0);
        }
        if (gui != null) {
            gui.setFocusable(false);
            gui.setSelectedIndex(newIndex);
            gui.setFocusable(true);
        }
    }

    public void handleNewListEvent() {
        int prevSize = 0;
        synchronized (tasks) {
            prevSize = tasks.size();
            tasks.clear();
            priorities.clear();
            statuses.clear();
        }
        if (prevSize == 0) {
            return;
        }
        fireListDataEvent(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, 0, prevSize-1));
    }

    /** Helper Functions **/

    private int indexOf(Task task) {
        int i = 0;
        for (Task t : tasks) {
            if (t == task) {
                return i;
            }
            i++;
        }
        return -1;
    }
}
