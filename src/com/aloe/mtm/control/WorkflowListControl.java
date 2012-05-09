package com.aloe.mtm.control;

import com.aloe.mtm.control.event.ControlEventAdapter;
import com.aloe.mtm.data.Workflow;
import com.aloe.mtm.gui.WorkflowDialog;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mcaulfie
 * Date: 5/8/11
 * Time: 1:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class WorkflowListControl extends ControlEventAdapter implements ComboBoxModel, TreeModel {

    private List<Workflow> workflows;
    private MTMControl control;
    private WorkflowDialog gui;

    public WorkflowListControl(MTMControl control, List<Workflow> workflows) {
        this.workflows = workflows;
        this.control = control;
        this.selected = workflows.get(0);
        this.workflowStateListControl = new WorkflowStateListControl();
        control.addControlEventListener(this);
    }

    public void registerGui(WorkflowDialog gui) {
        this.gui = gui;
    }

    /** List Model Impl **/

    private LinkedList<ListDataListener> listListeners = new LinkedList<ListDataListener>();
    private Workflow selected;

    public void setSelectedItem(Object o) {
        Workflow prev = selected;
        selected = (Workflow)o;
        workflowStateListControl.updateWorkflow(prev, selected);
    }

    public Object getSelectedItem() {
        return selected;
    }

    public int getSize() {
        return workflows.size();
    }

    public Object getElementAt(int i) {
        return workflows.get(i);
    }

    public void addListDataListener(ListDataListener listDataListener) {
        synchronized (listListeners) {
            listListeners.add(listDataListener);
        }
    }

    public void removeListDataListener(ListDataListener listDataListener) {
        synchronized (listListeners) {
            listListeners.remove(listDataListener);
        }
    }

    /** List Model for Workflow States **/

    private WorkflowStateListControl workflowStateListControl;

    public WorkflowStateListControl getWorkflowStateListControl() {
        return workflowStateListControl;
    }

    private class WorkflowStateListControl implements ComboBoxModel {

        private LinkedList<ListDataListener> listeners = new LinkedList<ListDataListener>();
        private Workflow.State selectedState;

        public int getSize() {
            if (selected == null) {
                return 0;
            }
            return selected.getStates().size();
        }

        public Object getElementAt(int i) {
            if (selected == null) {
                return null;
            }
            return selected.getStates().get(i);
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

        public void setSelectedItem(Object o) {
            selectedState = (Workflow.State)o;
        }

        public Object getSelectedItem() {
            return selectedState;
        }

        public void updateWorkflow(Workflow prev, Workflow curr) {
            if (prev != curr) {
                selectedState = null;
            }
            if (prev != null) {
                int prevSize = prev.getStates().size();
                fireListDataEvent(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, 0, prevSize));
            }
            if (curr != null) {
                int currSize = curr.getStates().size();
                fireListDataEvent(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, 0, currSize));
            }
        }

        public void updateStateName(Workflow.State state, String name) {
            state.setName(name);
            Workflow wf = state.getWorkflow();
            TreeModelEvent te = createTreeEvent(state);
            synchronized (treeListeners) {
                for (TreeModelListener l : treeListeners) {
                    l.treeNodesChanged(te);
                }
            }
            if (state.getWorkflow() != selected) {
                return;
            }
            int index = wf.getStates().indexOf(state);
            fireListDataEvent(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index, index));
        }

        public void addNewState(Workflow wf) {
            Workflow.State state = wf.createState();
            addNewState(wf, state);
        }

        public void addNewState(Workflow wf, Workflow.State state) {
            if (!wf.getStates().contains(state)) {
                wf.addState(state);
            }
            TreeModelEvent te = createTreeEvent(state);
            synchronized (treeListeners) {
                for (TreeModelListener l : treeListeners) {
                    l.treeNodesInserted(te);
                }
            }
            if (gui != null) {
                gui.editAtPath(new Object[]{WorkflowListControl.this,wf,state});
            }
            if (wf != selected) {
                return;
            }
            int index = wf.getStates().indexOf(state);
            fireListDataEvent(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index));
        }

        public void removeState(Workflow.State state) {
            TreeModelEvent te = createTreeEvent(state);
            Workflow wf = state.getWorkflow();
            int index = wf.getStates().indexOf(state);
            wf.removeState(state);
            synchronized (treeListeners) {
                for (TreeModelListener l : treeListeners) {
                    l.treeNodesRemoved(te);
                }
            }
            if (wf != selected) {
                return;
            }
            fireListDataEvent(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index));
        }

        public void reorderState(Workflow.State stateToMove, Workflow.State destState) {
            if (stateToMove.getWorkflow() != destState.getWorkflow()) {
                return;
            }
            Workflow wf = stateToMove.getWorkflow();
            int newIndex = wf.getStates().indexOf(destState);
            wf.removeState(stateToMove);
            wf.getStates().add(newIndex, stateToMove);
            WorkflowListControl root = WorkflowListControl.this;
            TreeModelEvent te = new TreeModelEvent(this, new Object[]{root,wf}, null, null);
            synchronized (treeListeners) {
                for (TreeModelListener l : treeListeners) {
                    l.treeStructureChanged(te);
                }
            }
            if (wf != selected) {
                return;
            }
            fireListDataEvent(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, wf.getStates().size()-1));
        }

        private void fireListDataEvent(ListDataEvent e) {
            synchronized (listeners) {
                for (ListDataListener l : listeners) {
                    switch (e.getType()) {
                        case ListDataEvent.CONTENTS_CHANGED:
                            l.contentsChanged(e); break;
                        case ListDataEvent.INTERVAL_ADDED:
                            l.intervalAdded(e); break;
                        case ListDataEvent.INTERVAL_REMOVED:
                            l.intervalRemoved(e); break;
                    }
                }
            }
        }

        private TreeModelEvent createTreeEvent(Workflow.State state) {
            Workflow wf = state.getWorkflow();
            int index = wf.getStates().indexOf(state);
            WorkflowListControl root = WorkflowListControl.this;
            return new TreeModelEvent(this, new Object[]{root,wf}, new int[]{index}, new Object[]{state});
        }
    }

    /** Tree Model Impl **/

    private LinkedList<TreeModelListener> treeListeners = new LinkedList<TreeModelListener>();

    public Object getRoot() {
        return this;
    }

    public Object getChild(Object o, int i) {
        if (o instanceof WorkflowListControl) {
            return workflows.get(i);
        } else if (o instanceof Workflow) {
            return ((Workflow)o).getStates().get(i);
        } else {
            return null;
        }
    }

    public int getChildCount(Object o) {
        if (o instanceof WorkflowListControl) {
            return workflows.size();
        } else if (o instanceof Workflow) {
            return ((Workflow)o).getStates().size();
        }
        return 0;
    }

    public boolean isLeaf(Object o) {
        return (o instanceof WorkflowListControl && workflows.size() == 0) ||
               (o instanceof Workflow && ((Workflow)o).getStates().size() == 0) ||
               (o instanceof Workflow.State);
    }

    public void valueForPathChanged(TreePath treePath, Object o) {
        String name = (String)o;
        Object obj = treePath.getLastPathComponent();
        if (obj instanceof Workflow) {
            updateWorkflowName((Workflow)obj, name);
        } else if (obj instanceof Workflow.State) {
            updateStateName((Workflow.State)obj, name);
        }
    }

    public int getIndexOfChild(Object parent, Object child) {
        if (parent instanceof WorkflowListControl &&
            child instanceof Workflow) {
            return workflows.indexOf(child);
        } else if (parent instanceof Workflow &&
                   child instanceof Workflow.State) {
            return ((Workflow)parent).getStates().indexOf(child);
        } else {
            return -1;
        }
    }

    public void addTreeModelListener(TreeModelListener treeModelListener) {
        synchronized (treeListeners) {
            treeListeners.add(treeModelListener);
        }
    }

    public void removeTreeModelListener(TreeModelListener treeModelListener) {
        synchronized (treeListeners) {
            treeListeners.remove(treeModelListener);
        }
    }

    /** GUI API **/

    public void updateWorkflowName(Workflow workflow, String name) {
        workflow.setName(name);
        int index = workflows.indexOf(workflow);
        TreeModelEvent te = new TreeModelEvent(this, new Object[]{this}, new int[]{index}, new Object[]{workflow});
        synchronized (treeListeners) {
            for (TreeModelListener l : treeListeners) {
                l.treeNodesChanged(te);
            }
        }
        ListDataEvent le = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index, index);
        synchronized (listListeners) {
            for (ListDataListener l : listListeners) {
                l.contentsChanged(le);
            }
        }
    }

    public void updateStateName(Workflow.State state, String name) {
        workflowStateListControl.updateStateName(state, name);
    }

    public void addNewState(Workflow workflow) {
        workflowStateListControl.addNewState(workflow);
    }

    public void addNewWorkflow() {
        Workflow wf = new Workflow();
        wf.setName("new workflow");
        addNewWorkflow(wf);
    }

    public void addNewWorkflow(Workflow wf) {
        synchronized (workflows) {
            if (!workflows.contains(wf)) {
                workflows.add(wf);
            }
        }
        int index = workflows.indexOf(wf);
        TreeModelEvent te = new TreeModelEvent(this, new Object[]{this}, new int[]{index}, new Object[]{wf});
        synchronized (treeListeners) {
            for (TreeModelListener l : treeListeners) {
                l.treeNodesInserted(te);
            }
        }
        ListDataEvent le = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index, index);
        synchronized (listListeners) {
            for (ListDataListener l : listListeners) {
                l.contentsChanged(le);
            }
        }
        if (gui != null) {
            gui.editAtPath(new Object[]{this,wf});
        }
    }

    public void removeWorkflow(Workflow workflow) {
        int index = workflows.indexOf(workflow);
        synchronized (workflows) {
            workflows.remove(workflow);
        }
        TreeModelEvent te = new TreeModelEvent(this, new Object[]{this}, new int[]{index}, new Object[]{workflow});
        synchronized (treeListeners) {
            for (TreeModelListener l : treeListeners) {
                l.treeNodesRemoved(te);
            }
        }
        ListDataEvent le = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index);
        synchronized (listListeners) {
            for (ListDataListener l : listListeners) {
                l.intervalRemoved(le);
            }
        }
    }

    public void reorderState(Workflow.State stateToMove, Workflow.State destState) {
        workflowStateListControl.reorderState(stateToMove, destState);
    }

    public void removeState(Workflow.State state) {
        workflowStateListControl.removeState(state);
    }

    /** Control Event Handlers **/

    public void handleEditWorkflowsEvent() {
        gui.setVisible(true);
    }

    public void handleWorkflowCreatedEvent(Workflow w) {
        addNewWorkflow(w);
    }

    public void handleWorkflowStateCreatedEvent(Workflow.State state) {
        workflowStateListControl.addNewState(state.getWorkflow(), state);
    }

    public void handleNewListEvent() {
        LinkedList<Workflow> wfList = new LinkedList<Workflow>(workflows);
        for (Workflow w : wfList) {
            if (w == Workflow.getDefault()) {
                continue;
            }
            removeWorkflow(w);
        }
    }
}