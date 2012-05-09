package com.aloe.mtm.gui;

import com.aloe.mtm.control.TaskSplitPaneControl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Created by IntelliJ IDEA.
 * User: mcaulfie
 * Date: 5/18/11
 * Time: 3:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class TaskSplitPane extends JSplitPane {

    private TaskSplitPaneControl control;
    private boolean detailsHidden;

    public TaskSplitPane(TaskSplitPaneControl control, TaskListPanel list, TaskDetailsPanel details) {
        JScrollPane taskListScroll = new JScrollPane(list);
        taskListScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollPane detailsScroll = new JScrollPane(details);
        detailsScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        detailsHidden = true;
        this.setDividerLocation(1000);

        this.setOrientation(VERTICAL_SPLIT);
        this.setTopComponent(taskListScroll);
        this.setBottomComponent(detailsScroll);

        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                if (detailsHidden) {
                    TaskSplitPane.this.setDividerLocation(1.0);
                } else {
                    TaskSplitPane.this.setDividerLocation(0.5);
                }
            }
        });

        control.registerGui(this);
    }

    public void hideDetailsPanel() {
        if (detailsHidden) {
            return;
        }
        this.setDividerLocation(1.0);
        detailsHidden = true;
    }

    public void showDetailsPane() {
        if (!detailsHidden) {
            return;
        }
        this.setDividerLocation(0.5);
        detailsHidden = false;
    }


}
