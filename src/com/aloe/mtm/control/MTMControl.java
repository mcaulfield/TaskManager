package com.aloe.mtm.control;

import com.aloe.mtm.control.event.*;
import com.aloe.mtm.data.MTMData;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mcaulfie
 * Date: 5/6/11
 * Time: 12:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class MTMControl {

    private MTMData data;

    private TaskMenuBarControl taskMenuBarControl;
    private TaskToolBarControl taskToolBarControl;
    private TaskListControl taskListControl;
    private TaskDetailsControl taskDetailsControl;
    private WorkflowListControl workflowListControl;
    private UndoControl undoControl;
    private StatusControl statusControl;
    private TaskFileControl taskFileControl;
    private TaskSplitPaneControl taskSplitPaneControl;

    private List<ControlEventListener> listeners = new LinkedList<ControlEventListener>();
    private List<ControlEvent> events = new LinkedList<ControlEvent>();

    public MTMControl() {
        data = new MTMData();

        taskMenuBarControl = new TaskMenuBarControl(this);
        taskListControl = new TaskListControl(this, data.getTasks());
        taskDetailsControl = new TaskDetailsControl(this, null);
        taskToolBarControl = new TaskToolBarControl(this);
        workflowListControl = new WorkflowListControl(this, data.getWorkflows());
        undoControl = new UndoControl(this);
        statusControl = new StatusControl(this);
        taskFileControl = new TaskFileControl(this, data.getWorkflows(), data.getTasks());
        taskSplitPaneControl = new TaskSplitPaneControl(this);

        Thread eventsThread = new Thread(new Runnable() {
            public void run() {
                eventsThread();
            }
        });
        eventsThread.setDaemon(true);
        eventsThread.start();
        taskFileControl.loadDefaultFile();
    }

    public TaskMenuBarControl getTaskMenuBarControl() {
        return taskMenuBarControl;
    }

    public TaskListControl getTaskListControl() {
        return taskListControl;
    }

    public TaskToolBarControl getTaskToolBarControl() {
        return taskToolBarControl;
    }

    public TaskDetailsControl getTaskDetailsControl() {
        return taskDetailsControl;
    }

    public WorkflowListControl getWorkflowListControl() {
        return workflowListControl;
    }

    public UndoControl getUndoControl() {
        return undoControl;
    }

    public StatusControl getStatusControl() {
        return statusControl;
    }

    public TaskFileControl getTaskFileControl() {
        return taskFileControl;
    }

    public TaskSplitPaneControl getTaskSplitPaneControl() {
        return taskSplitPaneControl;
    }

    public synchronized void addControlEventListener(ControlEventListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    public void removeControlEventListener(ControlEventListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    public void scheduleControlEvent(ControlEvent e) {
        System.out.println("scheduling control event: " + e.getType());
        synchronized (events) {
            events.add(e);
            events.notify();
        }
    }

    public void scheduleControlEvent(ControlEvent e, ControlEventListener src) {
        e.setSource(src);
        scheduleControlEvent(e);
    }

    private void eventsThread() {
        LinkedList<ControlEvent> currEvents = new LinkedList<ControlEvent>();
        while (true) {
            synchronized (events) {
                while (events.size() == 0) {
                    try { events.wait(); } catch (Exception e) {}
                }
                currEvents.addAll(events);
                events.clear();
            }
            for (ControlEvent e : currEvents) {
                fireControlEvent(e);
            }
            currEvents.clear();
        }
    }

    private void fireControlEvent(ControlEvent e) {
        synchronized (listeners) {
            for (ControlEventListener l : listeners) {
                fireControlEvent(e, l);
            }
        }
    }

    private void fireControlEvent(ControlEvent e, ControlEventListener l) {
        if (e.getSource() == l) {
            return;
        }
        switch (e.getType()) {
            case ADD_NEW_TASK:
                l.handleAddNewTaskEvent();
                break;
            case REMOVE_SELECTED_TASK:
                l.handleRemoveSelectedTaskEvent();
                break;
            case TOGGLE_TASK_SIZES:
                l.handleToggleSizesEvent();
                break;
            case TASK_CREATED:
                l.handleNewTaskCreatedEvent(((TaskControlEvent)e).getTask());
                break;
            case TASK_MULTI_CREATED:
                l.handleNewTasksCreatedEvent(((TaskControlEvent)e).getTaskList());
                break;
            case TASK_DELETED:
                l.handleTaskDeletedEvent(((TaskControlEvent)e).getTask());
                break;
            case TASK_SELECTED:
                l.handleTaskSelectedEvent(((TaskControlEvent)e).getTask());
                break;
            case TASK_UPDATED:
                l.handleTaskUpdatedEvent(((TaskControlEvent)e).getTask());
                break;
            case SELECT_NEXT_TASK:
                l.handleSelectNextTaskEvent();
                break;
            case SELECT_PREV_TASK:
                l.handleSelectPrevTaskEvent();
                break;
            case UNDO:
                l.handleUndoEvent();
                break;
            case STATUS:
                StatusControlEvent sce = (StatusControlEvent)e;
                l.handleStatusEvent(sce.getStatus(), sce.getProgress());
                break;
            case EDIT_WORKFLOWS:
                l.handleEditWorkflowsEvent();
                break;
            case NEW_LIST:
                l.handleNewListEvent();
                break;
            case LOAD_LIST:
                l.handleLoadListEvent();
                break;
            case SAVE_LIST:
                l.handleSaveListEvent();
                break;
            case WORKFLOW_CREATED:
                l.handleWorkflowCreatedEvent(((WorkflowControlEvent)e).getWorkflow());
                break;
            case WF_STATE_CREATED:
                l.handleWorkflowStateCreatedEvent(((WorkflowStateControlEvent)e).getState());
                break;
        }
    }
}
