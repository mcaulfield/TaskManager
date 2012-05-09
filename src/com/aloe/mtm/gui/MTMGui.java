package com.aloe.mtm.gui;

import com.aloe.mtm.control.MTMControl;
import com.aloe.mtm.control.TaskSplitPaneControl;
import com.aloe.mtm.control.event.ControlEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by IntelliJ IDEA.
 * User: mcaulfie
 * Date: 5/5/11
 * Time: 11:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class MTMGui {

    private MTMControl control;

    public MTMGui() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        JWindow splash = new JWindow();
        JProgressBar progress = new JProgressBar(0,8);
        progress.setPreferredSize(new Dimension(300,20));
        splash.add(progress);
        splash.pack();
        splash.setLocationRelativeTo(null);
        splash.setVisible(true);

        // initialize control
        control = new MTMControl();
        progress.setValue(progress.getValue()+1);

        // initialize frame
        JFrame frame = new JFrame("Aloe Task Manager");
        frame.setIconImage(ImageLoader.loadImage("check").getImage());
        JPanel panel = new JPanel(new BorderLayout());
        frame.getContentPane().add(panel);
        progress.setValue(progress.getValue() + 1);

        // initialize menu bar
        frame.setJMenuBar(new TaskMenuBar(control.getTaskMenuBarControl()));
        progress.setValue(progress.getValue() + 1);

        // initialize toolbar
        JToolBar toolBar = new TaskToolBar(control.getTaskToolBarControl());
        panel.add(toolBar, BorderLayout.PAGE_START);
        progress.setValue(progress.getValue() + 1);

        // initialize split pane
        TaskListPanel taskListPanel = new TaskListPanel(control.getTaskListControl());
        TaskDetailsPanel detailsPanel = new TaskDetailsPanel(control.getTaskDetailsControl());
        JSplitPane splitPane = new TaskSplitPane(control.getTaskSplitPaneControl(), taskListPanel, detailsPanel);
        panel.add(splitPane, BorderLayout.CENTER);
        progress.setValue(progress.getValue() + 1);

        //initialize status area
        JPanel statusPanel = new StatusPanel(control.getStatusControl());
        panel.add(statusPanel, BorderLayout.PAGE_END);
        progress.setValue(progress.getValue() + 1);

        // initialize workflow dialog
        JDialog workflowDialog = new WorkflowDialog(control.getWorkflowListControl(), frame);
        progress.setValue(progress.getValue() + 1);

        // initialize task file dialog
        new TaskFileDialog(control.getTaskFileControl());
        progress.setValue(progress.getValue() + 1);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1000, 600));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        splash.setVisible(false);
        splash.dispose();
    }

    public static void main(String [] args) {
        new MTMGui();
   }
}
