package com.aloe.mtm.data;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: mcaulfie
 * Date: 5/5/11
 * Time: 8:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class MTMData {

    private List<Workflow> workflows = new LinkedList<Workflow>();
    private List<Task> tasks = new LinkedList<Task>();

    public MTMData() {
        workflows.add(Workflow.getDefault());
    }

    public List<Workflow> getWorkflows() {
        return workflows;
    }

    public List<Task> getTasks() {
        return tasks;
    }

}
