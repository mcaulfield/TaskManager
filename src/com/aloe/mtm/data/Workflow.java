package com.aloe.mtm.data;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mcaulfie
 * Date: 5/5/11
 * Time: 6:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class Workflow {

    public static class State {
        private Workflow workflow = null;
        private String name = "";

        public State(Workflow workflow, String name) {
            this.workflow = workflow;
            this.name = name;
        }

        public String toString() {
            return name;
        }

        public Workflow getWorkflow() {
            return workflow;
        }

        public void setWorkflow(Workflow workflow) {
            this.workflow = workflow;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public State clone() {
            return new State(this.workflow, this.name);
        }
    }

    private String name = "";
    private List<State> states = new LinkedList<State>();

    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<State> getStates() {
        return states;
    }

    public void setStates(List<State> states) {
        this.states = states;
    }

    public void addState(State state) {
        states.add(state);
    }

    public void removeState(State state) {
        states.remove(state);
    }

    public State getStartState() {
        if (states.size() == 0) {
            return null;
        }
        return states.get(0);
    }

    public State createState() {
        return new State(this, "new state");
    }

    public State getState(String name) {
        for (State s : states) {
            if (s.getName().equals(name)) {
                return s;
            }
        }
        return null;
    }

    public Workflow clone() {
        Workflow clone = new Workflow();
        clone.name = this.name;
        for (State s : this.states) {
            clone.addState(s.clone());
        }
        return clone;
    }

    /** Default Workflow **/

    private static class DefaultWorkflow extends Workflow {
        public DefaultWorkflow() {
            super();
            this.setName("default");
            State workState = new State(this, "working");
            this.addState(workState);
        }
    }
    private static Workflow defaultWorkflow = new DefaultWorkflow();
    public static Workflow getDefault() {
        return defaultWorkflow;
    }
}