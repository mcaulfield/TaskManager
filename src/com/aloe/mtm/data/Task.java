package com.aloe.mtm.data;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: mcaulfie
 * Date: 5/5/11
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class Task implements Comparable<Task> {

    public static enum Type {
        NONE, BUG, PRESENTATION, WRITING, IDEA, EMAIL, READING, MEETING;
        public String toString() {
            return super.toString().toLowerCase().replace('_',' ');
        }
    }

    public static enum Status {
        IN_PROGRESS, BLOCKED, PENDING, COMPLETE;
        public String toString() {
            return super.toString().toLowerCase().replace('_',' ');
        }
    }

    private Type type = Type.NONE;
    private String name = "";
    private int priority = 0;
    private Status status = Status.PENDING;
    private Workflow.State workflowState = Workflow.getDefault().getStartState();
    private List<Roadblock> blocks = new LinkedList<Roadblock>();
    private String notes = "";
    private Date completeDate = new Date();

    public int compareTo(Task task) {
        if (this.getStatus() == task.getStatus()) {
            if (this.getStatus() == Task.Status.COMPLETE) {
                return task.getCompleteDate().compareTo(this.getCompleteDate());
            } else {
                return this.priority - task.priority;
            }
        }
        return this.getStatus().ordinal() - task.getStatus().ordinal();
    }

    public String toString() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Workflow.State getWorkflowState() {
        return workflowState;
    }

    public void setWorkflowState(Workflow.State workflowState) {
        this.workflowState = workflowState;
    }

    public List<Roadblock> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Roadblock> blocks) {
        this.blocks = blocks;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(Date completeDate) {
        this.completeDate = completeDate;
    }

    public Roadblock getLastBlock() {
        for (Roadblock r : blocks) {
            if (r.getStatus() == Roadblock.Status.WAITING) {
                return r;
            }
        }
        return null;
    }

    public Task clone() {
        Task clone = new Task();
        clone.type = this.type;
        clone.name = this.name;
        clone.priority = this.priority;
        clone.status = this.status;
        clone.workflowState = this.workflowState;
        for (Roadblock b : this.blocks) {
            clone.blocks.add(b.clone());
        }
        clone.notes = this.notes;
        clone.completeDate = this.completeDate;
        return clone;
    }

}
