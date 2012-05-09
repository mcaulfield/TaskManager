package com.aloe.mtm.gui;

import com.aloe.mtm.control.WorkflowListControl;
import com.aloe.mtm.data.Workflow;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by IntelliJ IDEA.
 * User: mcaulfie
 * Date: 5/11/11
 * Time: 10:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class WorkflowDialog extends JDialog {

    private WorkflowListControl control;
    private JTree tree;
    private JPopupMenu statePopup, workflowPopup, blankPopup;
    JMenuItem addStateItem, renameStateItem, removeStateItem;
    JMenuItem addStateItem2, addWorkflowItem, renameWorkflowItem, removeWorkflowItem;
    JMenuItem addWorkflowItem2;

    public WorkflowDialog(WorkflowListControl control, JFrame owner) {
        super(owner);
        this.control = control;
        control.registerGui(this);
        initGui();
        initListeners();
    }

    private void initGui() {
        this.setTitle("Workflow Editor");
        this.setLayout(new BorderLayout());

        tree = new JTree(control);
        tree.setEditable(true);
        tree.setRootVisible(false);
        tree.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
        this.add(tree, BorderLayout.CENTER);

        this.setPreferredSize(new Dimension(300,300));
        this.pack();
        this.setLocationRelativeTo(null);

        addStateItem = new JMenuItem("Add New State");
        addStateItem2 = new JMenuItem("Add New State");
        renameStateItem = new JMenuItem("Rename State");
        removeStateItem = new JMenuItem("Remove State");
        addWorkflowItem = new JMenuItem("Add New Workflow");
        addWorkflowItem2 = new JMenuItem("Add New Workflow");
        renameWorkflowItem = new JMenuItem("Rename Workflow");
        removeWorkflowItem = new JMenuItem("Remove Workflow");

        statePopup = new JPopupMenu();
        statePopup.add(addStateItem);
        statePopup.addSeparator();
        statePopup.add(renameStateItem);
        statePopup.add(removeStateItem);

        workflowPopup = new JPopupMenu();
        workflowPopup.add(addWorkflowItem);
        workflowPopup.addSeparator();
        workflowPopup.add(addStateItem2);
        workflowPopup.add(renameWorkflowItem);
        workflowPopup.add(removeWorkflowItem);

        blankPopup = new JPopupMenu();
        blankPopup.add(addWorkflowItem2);
    }

    private void initListeners() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            private Workflow.State dragState = null;
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                    return;
                }
                TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                if (selPath == null) {
                    return;
                }
                if (selPath.getLastPathComponent() instanceof Workflow.State) {
                    dragState = (Workflow.State)selPath.getLastPathComponent();
                }
            }
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                    return;
                }
                dragState = null;
            }
            public void mouseDragged(MouseEvent e) {
                TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                if (selPath == null) {
                    return;
                }
                if (dragState != null && selPath.getLastPathComponent() instanceof Workflow.State) {
                    control.reorderState(dragState, (Workflow.State)selPath.getLastPathComponent());
                    tree.setSelectionPath(selPath);
                }
            }
            public void showPopup(MouseEvent e) {
                TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                if (selPath == null) {
                    tree.setSelectionPath(null);
                    blankPopup.show(e.getComponent(),e.getX(),e.getY());
                    return;
                }
                Object node = selPath.getLastPathComponent();
                tree.setSelectionPath(selPath);
                if (node instanceof Workflow) {
                    workflowPopup.show(e.getComponent(), e.getX(), e.getY());
                } else if (node instanceof Workflow.State) {
                    statePopup.show(e.getComponent(), e.getX(), e.getY());
                } else {
                    tree.setSelectionPath(null);
                    blankPopup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        };
        tree.addMouseListener(mouseAdapter);
        tree.addMouseMotionListener(mouseAdapter);

        ActionListener addStateListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Object selected = tree.getSelectionPath().getLastPathComponent();
                if (selected == null) {
                    return;
                }
                if (selected instanceof Workflow) {
                    control.addNewState((Workflow)selected);
                } else if (selected instanceof Workflow.State) {
                    control.addNewState(((Workflow.State)selected).getWorkflow());
                } else {
                    return;
                }
            }
        };
        addStateItem.addActionListener(addStateListener);
        addStateItem2.addActionListener(addStateListener);

        ActionListener addWorkflowListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                control.addNewWorkflow();
            }
        };
        addWorkflowItem.addActionListener(addWorkflowListener);
        addWorkflowItem2.addActionListener(addWorkflowListener);

        ActionListener renameListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                TreePath selPath = tree.getSelectionPath();
                tree.startEditingAtPath(selPath);
            }
        };
        renameStateItem.addActionListener(renameListener);
        renameWorkflowItem.addActionListener(renameListener);

        ActionListener removeListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Object selected = tree.getSelectionPath().getLastPathComponent();
                if (selected instanceof Workflow) {
                    control.removeWorkflow((Workflow)selected);
                } else if (selected instanceof Workflow.State) {
                    control.removeState((Workflow.State)selected);
                }
            }
        };
        removeWorkflowItem.addActionListener(removeListener);
        removeStateItem.addActionListener(removeListener);
    }

    public void editAtPath(final Object[] path) {
        TreePath tp = new TreePath(path);
        tree.expandPath(tp);
        tree.startEditingAtPath(tp);
    }

    public void paint(Graphics graphics) {
        super.paint(graphics);
    }
}
