package com.aloe.mtm.gui;

import com.aloe.mtm.control.MTMControl;
import com.aloe.mtm.control.TaskToolBarControl;
import com.aloe.mtm.data.Task;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: mcaulfie
 * Date: 5/6/11
 * Time: 3:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class TaskToolBar extends JToolBar {

    private TaskToolBarControl control;

    private JButton openListButton, saveListButton;
    private JButton addTaskButton, removeTaskButton;
    private JButton editWorkflowsButton;
    private JButton toggleSizeButton;

    public TaskToolBar(TaskToolBarControl control) {
        this.control = control;

        initGui();
        initListeners();
    }

    private void initGui() {

        openListButton = new JButton(ImageLoader.loadImage("open"));
        openListButton.setToolTipText("Open task list (Ctrl+O)");
        openListButton.setFocusable(false);
        this.add(openListButton);

        saveListButton = new JButton(ImageLoader.loadImage("save"));
        saveListButton.setToolTipText("Save task list (Ctrl+S)");
        saveListButton.setFocusable(false);
        this.add(saveListButton);

        this.addSeparator();

        addTaskButton = new JButton(ImageLoader.loadImage("add"));
        addTaskButton.setToolTipText("Add new task (Ctrl+N)");
        addTaskButton.setFocusable(false);
        this.add(addTaskButton);

        removeTaskButton = new JButton(ImageLoader.loadImage("remove"));
        removeTaskButton.setToolTipText("Remove task (Del)");
        removeTaskButton.setFocusable(false);
        this.add(removeTaskButton);

        this.addSeparator();

        editWorkflowsButton = new JButton(ImageLoader.loadImage("workflow"));
        editWorkflowsButton.setToolTipText("Edit workflows (Ctrl+W)");
        editWorkflowsButton.setFocusable(false);
        this.add(editWorkflowsButton);

        this.addSeparator();

        toggleSizeButton = new JButton(ImageLoader.loadImage("toggle"));
        toggleSizeButton.setToolTipText("Toggle task sizes");
        toggleSizeButton.setFocusable(false);
        this.add(toggleSizeButton);

        this.setFloatable(false);
        this.setFocusable(false);
    }

    private void initListeners() {
        openListButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                control.loadTaskList();
            }
        });
        saveListButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                control.saveTaskList();
            }
        });
        addTaskButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                control.addNewTask();
            }
        });
        removeTaskButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                control.removeSelectedTask();
            }
        });
        editWorkflowsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                control.editWorkflows();
            }
        });
        toggleSizeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                control.toggleTaskSizes();
            }
        });
    }
}
