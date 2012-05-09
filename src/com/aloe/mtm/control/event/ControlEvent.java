package com.aloe.mtm.control.event;

/**
 * Created by IntelliJ IDEA.
 * User: mcaulfie
 * Date: 5/8/11
 * Time: 9:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class ControlEvent {

    public final static ControlEvent ADD_NEW_TASK = new ControlEvent(Type.ADD_NEW_TASK);
    public final static ControlEvent REMOVE_SELECTED_TASK = new ControlEvent(Type.REMOVE_SELECTED_TASK);
    public final static ControlEvent TOGGLE_TASK_SIZES = new ControlEvent(Type.TOGGLE_TASK_SIZES);
    public final static ControlEvent SELECT_NEXT_TASK = new ControlEvent(Type.SELECT_NEXT_TASK);
    public final static ControlEvent SELECT_PREV_TASK = new ControlEvent(Type.SELECT_PREV_TASK);
    public final static ControlEvent UNDO = new ControlEvent(Type.UNDO);
    public final static ControlEvent EDIT_WORKFLOWS = new ControlEvent(Type.EDIT_WORKFLOWS);
    public final static ControlEvent NEW_LIST = new ControlEvent(Type.NEW_LIST);
    public final static ControlEvent SAVE_LIST = new ControlEvent(Type.SAVE_LIST);
    public final static ControlEvent LOAD_LIST = new ControlEvent(Type.LOAD_LIST);


    public static enum Type {
        ADD_NEW_TASK, REMOVE_SELECTED_TASK, TOGGLE_TASK_SIZES,
        TASK_CREATED, TASK_MULTI_CREATED, TASK_DELETED, TASK_SELECTED, TASK_UPDATED,
        WORKFLOW_CREATED, WF_STATE_CREATED,
        SELECT_NEXT_TASK, SELECT_PREV_TASK,
        UNDO, STATUS, EDIT_WORKFLOWS,
        NEW_LIST, SAVE_LIST, LOAD_LIST;
    }

    private Type type;
    private Object source;

    public ControlEvent(Type type) {
        this.type = type;
    }

    public ControlEvent(Type type, Object source) {
        this.type = type;
        this.source = source;
    }

    public Type getType() {
        return type;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }
}
