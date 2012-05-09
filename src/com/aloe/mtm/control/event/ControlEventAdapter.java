package com.aloe.mtm.control.event;

import com.aloe.mtm.control.event.ControlEventListener;
import com.aloe.mtm.data.Task;
import com.aloe.mtm.data.Workflow;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mcaulfie
 * Date: 5/8/11
 * Time: 9:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class ControlEventAdapter implements ControlEventListener {

    public void handleAddNewTaskEvent() {}
    public void handleRemoveSelectedTaskEvent() {}
    public void handleToggleSizesEvent() {}
    public void handleNewTaskCreatedEvent(Task newTask) {}
    public void handleNewTasksCreatedEvent(List<Task> newTasks) {}
    public void handleTaskDeletedEvent(Task deletedTask) {}
    public void handleTaskSelectedEvent(Task task) {}
    public void handleTaskUpdatedEvent(Task task) {}
    public void handleSelectNextTaskEvent() {}
    public void handleSelectPrevTaskEvent() {}
    public void handleUndoEvent() {}
    public void handleStatusEvent(String status, double progress) {}
    public void handleEditWorkflowsEvent() {}
    public void handleNewListEvent() {}
    public void handleLoadListEvent() {}
    public void handleSaveListEvent() {}
    public void handleWorkflowCreatedEvent(Workflow newWorkflow) {}
    public void handleWorkflowStateCreatedEvent(Workflow.State state) {}
}
