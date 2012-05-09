package com.aloe.mtm.control.event;

import com.aloe.mtm.data.Workflow;

/**
 * Created by IntelliJ IDEA.
 * User: mcaulfie
 * Date: 5/15/11
 * Time: 5:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class WorkflowStateControlEvent extends ControlEvent {

    private Workflow.State state;

    public WorkflowStateControlEvent(Type type, Workflow.State state) {
        super(type);
        this.state = state;
    }

    public Workflow.State getState() {
        return state;
    }
}
