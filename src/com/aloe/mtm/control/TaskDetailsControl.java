package com.aloe.mtm.control;

import com.aloe.mtm.control.event.ControlEvent;
import com.aloe.mtm.control.event.ControlEventAdapter;
import com.aloe.mtm.control.event.TaskControlEvent;
import com.aloe.mtm.data.Roadblock;
import com.aloe.mtm.data.Task;
import com.aloe.mtm.data.Workflow;
import com.aloe.mtm.gui.TaskDetailsPanel;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: mcaulfie
 * Date: 5/6/11
 * Time: 3:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class TaskDetailsControl extends ControlEventAdapter {

    private Task task;
    private MTMControl control;
    private TaskDetailsPanel gui;
    private BlockListControl  blockListControl;

    public TaskDetailsControl(MTMControl control, Task task) {
        this.task = task;
        this.control = control;
        this.blockListControl = new BlockListControl(this, null);
        control.addControlEventListener(this);
    }

    /** GUI API **/

    public void registerGui(TaskDetailsPanel gui) {
        this.gui = gui;
    }

    public WorkflowListControl getWorkflowListControl() {
        return control.getWorkflowListControl();
    }

    public BlockListControl getBlockListControl() {
        return blockListControl;
    }

    public void updateTaskName(String name) {
        if (task == null || task.getName().equals(name)) {
            return;
        }
        task.setName(name);
        scheduleTaskUpdateEvent();
    }

    public void updateTaskPriority(int priority) {
        if (task == null || task.getPriority() == priority) {
            return;
        }
        task.setPriority(priority);
        scheduleTaskUpdateEvent();
    }

    public void updateTaskType(Task.Type type) {
        if (task == null || task.getType() == type) {
            return;
        }
        task.setType(type);
        scheduleTaskUpdateEvent();
    }

    public void updateTaskStatus(Task.Status status) {
        if (task == null || task.getStatus() == status) {
            return;
        }
        task.setStatus(status);
        if (status == Task.Status.COMPLETE) {
            task.setCompleteDate(new Date());
        }
        scheduleTaskUpdateEvent();
    }

    public void updateTaskWorkflow(Workflow workflow) {
        if (task == null || task.getWorkflowState().getWorkflow() == workflow) {
            return;
        }
        Workflow.State state = workflow.getStartState();
        updateTaskWorkflowState(state);
        gui.updateFields(task);
    }

    public void updateTaskWorkflowState(Workflow.State state) {
        if (task == null || task.getWorkflowState() == state) {
            return;
        }
        task.setWorkflowState(state);
        scheduleTaskUpdateEvent();
    }

    public void updateTaskNotes(String notes) {
        if (task == null || notes.equals(task.getNotes())) {
            return;
        }
        task.setNotes(notes);
        scheduleTaskUpdateEvent();
    }

    public void editWorkflows() {
        control.scheduleControlEvent(ControlEvent.EDIT_WORKFLOWS);
    }

    /** Block List Panel API **/

    public void blockListUpdated() {
        if (task == null) {
            return;
        }
        scheduleTaskUpdateEvent();
        boolean blocked = false;
        for (Roadblock r : task.getBlocks()) {
            if (r.getStatus() == Roadblock.Status.WAITING) {
                blocked = true;
                break;
            }
        }
        switch (task.getStatus()) {
            case BLOCKED:
                if (!blocked) {
                    gui.setStatusSelection(Task.Status.IN_PROGRESS);
                }
                break;
            case IN_PROGRESS:
                if (blocked) {
                    gui.setStatusSelection(Task.Status.BLOCKED);
                }
                break;
        }
    }

    /** Control Event Handlers **/

    public void handleTaskSelectedEvent(Task task) {
        if (this.task == task) {
            return;
        }
        this.task = task;
        if (task != null) {
            gui.updateFields(task);
            gui.setEnabled(true);
            blockListControl.handleNewBlockListEvent(task.getBlocks());
        } else {
            gui.clearFields();
            gui.setEnabled(false);
            blockListControl.handleNewBlockListEvent(null);
        }
    }

    public void handleTaskDeletedEvent(Task deletedTask) {
        if (task != deletedTask) {
            return;
        }
        this.task = null;
        gui.clearFields();
        gui.setEnabled(false);
        blockListControl.handleNewBlockListEvent(null);
    }

    public void handleTaskUpdatedEvent(Task task) {
        if (this.task != task) {
            return;
        }
        gui.updateFields(task);
        blockListControl.handleBlockListUpdatedEvent(task.getBlocks());
    }

    /** Helper Functions **/

    private void scheduleTaskUpdateEvent() {
        control.scheduleControlEvent(new TaskControlEvent(ControlEvent.Type.TASK_UPDATED, task), this);
    }
}
