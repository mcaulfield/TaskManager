package com.aloe.mtm.gui;

import com.aloe.mtm.control.TaskMenuBarControl;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * Created by IntelliJ IDEA.
 * User: mcaulfie
 * Date: 5/10/11
 * Time: 10:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class TaskMenuBar extends JMenuBar {

    private TaskMenuBarControl control;

    public TaskMenuBar(TaskMenuBarControl ctrl) {
        this.control = ctrl;

        /** Init Menus **/

        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        this.add(fileMenu);

        JMenuItem newListItem = new JMenuItem("New Task List");
        newListItem.setMnemonic(KeyEvent.VK_N);
        fileMenu.add(newListItem);

        fileMenu.addSeparator();

        JMenuItem openItem = new JMenuItem("Open Task List...");
        openItem.setMnemonic(KeyEvent.VK_O);
        openItem.setIcon(ImageLoader.loadImage("open"));
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
        fileMenu.add(openItem);

        JMenuItem saveItem = new JMenuItem("Save Task List");
        saveItem.setMnemonic(KeyEvent.VK_S);
        saveItem.setIcon(ImageLoader.loadImage("save"));
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
        fileMenu.add(saveItem);

        fileMenu.addSeparator();

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic(KeyEvent.VK_X);
        fileMenu.add(exitItem);

        // Task menu
        JMenu taskMenu = new JMenu("Task");
        taskMenu.setMnemonic(KeyEvent.VK_T);
        this.add(taskMenu);

        JMenuItem undoItem = new JMenuItem("Undo");
        undoItem.setMnemonic(KeyEvent.VK_U);
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_MASK));
        taskMenu.add(undoItem);

        taskMenu.addSeparator();

        JMenuItem newTaskItem = new JMenuItem("Add New Task");
        newTaskItem.setIcon(ImageLoader.loadImage("add"));
        newTaskItem.setMnemonic(KeyEvent.VK_N);
        newTaskItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK));
        taskMenu.add(newTaskItem);

        JMenuItem removeTaskItem = new JMenuItem("Remove Selected Task");
        removeTaskItem.setMnemonic(KeyEvent.VK_R);
        removeTaskItem.setIcon(ImageLoader.loadImage("remove"));
        taskMenu.add(removeTaskItem);

        taskMenu.addSeparator();

        JMenuItem selectNextItem = new JMenuItem("Next Task");
        selectNextItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.CTRL_MASK));
        taskMenu.add(selectNextItem);

        JMenuItem selectPrevItem = new JMenuItem("Previous Task");
        selectPrevItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.CTRL_MASK));
        taskMenu.add(selectPrevItem);

        // Workflow menu
        JMenu workflowMenu = new JMenu("Workflow");
        workflowMenu.setMnemonic(KeyEvent.VK_W);
        this.add(workflowMenu);

        JMenuItem editWorkflowsItem = new JMenuItem("Edit Workflows...");
        editWorkflowsItem.setIcon(ImageLoader.loadImage("workflow"));
        editWorkflowsItem.setMnemonic(KeyEvent.VK_E);
        editWorkflowsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_MASK));
        workflowMenu.add(editWorkflowsItem);

        // View menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        this.add(viewMenu);

        JMenuItem toggleItem = new JMenuItem("Toggle Task Sizes");
        toggleItem.setIcon(ImageLoader.loadImage("toggle"));
        viewMenu.add(toggleItem);

        /** Init Listeners **/

        // File listeners
        newListItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                control.newTaskList();
            }
        });
        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                control.loadTaskList();
            }
        });
        saveItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                control.saveTaskList();
            }
        });
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                System.exit(0);
            }
        });

        // Task listeners
        undoItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                control.undoAction();
            }
        });
        newTaskItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                control.addNewTask();
            }
        });
        removeTaskItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                control.removeSelectedTask();
            }
        });
        selectNextItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                control.selectNextTask();
            }
        });
        selectPrevItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                control.selectPrevTask();
            }
        });

        // Workflow listeners
        editWorkflowsItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                control.editWorkflows();
            }
        });

        // View listeners
        toggleItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                control.toggleTaskSizes();
            }
        });
    }
}
