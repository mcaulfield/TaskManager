package com.aloe.mtm.gui;

import com.aloe.mtm.control.TaskListControl;
import com.aloe.mtm.data.Task;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by IntelliJ IDEA.
 * User: mcaulfie
 * Date: 5/6/11
 * Time: 3:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class TaskListPanel extends JPanel implements ListSelectionListener {

    private JList list;
    private JPopupMenu popupMenu;

    private TaskListControl control;

    public TaskListPanel(TaskListControl ctrl) {
        super();
        this.control = ctrl;
        this.setLayout(new BorderLayout());

        // initialize task list
        list = new JList(control);
        list.setCellRenderer(new TaskListCellRenderer(control));
        this.add(list, BorderLayout.CENTER);

        // add selection listener
        list.addListSelectionListener(this);

        // add focus listener
        list.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent focusEvent) {
                list.setBorder(BorderFactory.createLineBorder(ColorScheme.HIGHLIGHT, 1));
            }
            public void focusLost(FocusEvent focusEvent) {
                list.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
            }
        });

        // add key listener
        list.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_DELETE &&
                        list.getSelectedValue() != null) {
                    control.removeSelectedTask();
                } else if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE &&
                        list.getSelectedValue() != null) {
                    list.clearSelection();
                }
            }
        });

        // setup popup menu
        initPopup();

        // register self to controller
        control.registerGui(this);
    }

    private void initPopup() {
        popupMenu = new JPopupMenu();

        JMenuItem addTaskItem = new JMenuItem("Add new task");
        addTaskItem.setIcon(ImageLoader.loadImage("add"));
        addTaskItem.setMnemonic(KeyEvent.VK_N);
        popupMenu.add(addTaskItem);

        popupMenu.addSeparator();

        final JMenuItem removeTaskItem = new JMenuItem("Remove task");
        removeTaskItem.setIcon(ImageLoader.loadImage("remove"));
        addTaskItem.setMnemonic(KeyEvent.VK_R);
        popupMenu.add(removeTaskItem);

        list.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }
            private void maybeShowPopup(MouseEvent e) {
                if (!e.isPopupTrigger()) {
                    return;
                }
                int index = list.locationToIndex(new Point(e.getX(), e.getY()));
                if (index < list.getModel().getSize()) {
                    list.setSelectedIndex(index);
                }
                removeTaskItem.setEnabled(list.getSelectedValue() != null);
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        addTaskItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                control.addNewTask();
            }
        });
        removeTaskItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                control.removeSelectedTask();
            }
        });
    }

    public void clearSelection() {
        list.clearSelection();
    }

    public void setSelectedIndex(int i) {
        list.setSelectedIndex(i);
        list.ensureIndexIsVisible(i);
        list.requestFocus();
    }

    public void setFocusable(boolean focusable) {
        list.setFocusable(focusable);
    }

    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        control.setSelected((Task) list.getSelectedValue());
    }
}
