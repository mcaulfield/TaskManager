package com.aloe.mtm.control.event;

import com.aloe.mtm.data.Workflow;

/**
 * Created by IntelliJ IDEA.
 * User: mcaulfie
 * Date: 5/15/11
 * Time: 5:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class WorkflowControlEvent extends ControlEvent {

    private Workflow workflow;

    public WorkflowControlEvent(Type type, Workflow workflow) {
        super(type);
        this.workflow = workflow;
    }

    public Workflow getWorkflow() {
        return workflow;
    }
}
