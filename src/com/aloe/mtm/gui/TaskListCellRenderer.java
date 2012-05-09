package com.aloe.mtm.gui;

import com.aloe.mtm.control.TaskListControl;
import com.aloe.mtm.data.Roadblock;
import com.aloe.mtm.data.Task;
import com.aloe.mtm.data.Workflow;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: mcaulfie
 * Date: 5/6/11
 * Time: 12:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class TaskListCellRenderer implements ListCellRenderer {

    private TaskListControl control;
    private HashMap<Task.Type, ImageIcon> largeIcons = new HashMap<Task.Type, ImageIcon>();
    private HashMap<Task.Type, ImageIcon> smallIcons = new HashMap<Task.Type, ImageIcon>();

    public TaskListCellRenderer(TaskListControl control) {
        this.control = control;

        for (Task.Type type : Task.Type.values()) {
            String filename1 = "img" + File.separator + type.toString().toLowerCase() + ".png";
            String filename2 = "/img/" + type.toString().toLowerCase() + ".png";
            ImageIcon icon = null;
            if (new File(filename1).exists()) {
                icon = new ImageIcon(filename1);
            } else {
                URL rsrc = TaskListCellRenderer.class.getResource(filename2);
                icon = new ImageIcon(rsrc);
            }
            Image img = icon.getImage().getScaledInstance(32,32,Image.SCALE_SMOOTH);
            icon = new ImageIcon(img);
            largeIcons.put(type, icon);
            img = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            icon = new ImageIcon(img);
            smallIcons.put(type, icon);
        }
    }

    private class ListCellPanel extends JPanel {
        public void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D)graphics;
            GradientPaint gp = new GradientPaint(0,0,getBackground(),0,200,getBackground().darker());
            g.setPaint(gp);
            g.fillRect(0,0,this.getWidth(),this.getHeight());
        }
    }

    public Component getListCellRendererComponent(JList list, Object o, int i,
                                                  boolean isSelected, boolean isFocused) {
        if (control.isLargeMode()) {
            return getLargeCellRendererComp(list, o, i, isSelected, isFocused);
        } else {
            return getSmallCellRendererComp(list, o, i, isSelected, isFocused);
        }
    }

    private Component getLargeCellRendererComp(JList list, Object o, int i,
                                               boolean isSelected, boolean isFocused) {
        Task t = (Task)o;
        ListCellPanel cell = new ListCellPanel();
        cell.setLayout(new GridBagLayout());
        cell.setPreferredSize(new Dimension(500,60));
        GridBagConstraints c = new GridBagConstraints();

        ImageIcon icon = largeIcons.get(t.getType());
        if (t.getStatus() == Task.Status.COMPLETE) {
            icon = ImageLoader.loadImage("check",true);
        }
        JLabel iconLabel = new JLabel(icon);
        c.gridx = 0; c.gridy = 0;
        c.gridwidth = 1; c.gridheight = 2;
        c.weightx = 0; c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(5,5,5,5);
        cell.add(iconLabel, c);

        JLabel nameLabel = new JLabel(t.getName());
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        c.gridx = 1; c.gridy = 0;
        c.gridwidth = 1; c.gridheight = 1;
        c.weightx = 0; c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(6,5,0,5);
        cell.add(nameLabel, c);

        String statusStr;
        Color statusColor;
        switch (t.getStatus()) {
            case BLOCKED:
                Roadblock b = t.getLastBlock();
                if (b != null) {
                    String dateStr = DateFormat.getDateInstance(DateFormat.FULL).format(b.getDate());
                    statusStr = "blocked by " + b.getDesc() + " as of " + dateStr;
                } else {
                    statusStr = "blocked by unknown";
                }
                statusColor = ColorScheme.BLOCKED;
                break;
            case IN_PROGRESS:
                Workflow.State ws = t.getWorkflowState();
                statusStr = "currently in " + ws.getName() + " state";
                statusColor = ColorScheme.IN_PROGRESS;
                break;
            case PENDING:
                statusStr = " ";
                statusColor = ColorScheme.PENDING;
                break;
            default:
            case COMPLETE:
                String dateStr = DateFormat.getDateInstance(DateFormat.FULL).format(t.getCompleteDate());
                statusStr = "completed on " + dateStr;
                statusColor = ColorScheme.COMPLETE;
                break;
        }

        JLabel statusLabel = new JLabel(statusStr);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        c.gridx = 1; c.gridy = 1;
        c.gridwidth = 1; c.gridheight = 1;
        c.weightx = 0; c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0,5,5,5);
        cell.add(statusLabel, c);

        c.gridx = 2; c.gridy = 0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = 1;
        c.weightx = 1; c.weighty = 0;
        c.fill = GridBagConstraints.BOTH;
        cell.add(Box.createHorizontalGlue(), c);

        c.gridx = 1; c.gridy = 2;
        c.gridwidth = 1; c.gridheight = 1;
        c.weightx = 0; c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        cell.add(Box.createVerticalGlue(), c);

        // Coloring
        cell.setBackground(statusColor);
        cell.setBorder(BorderFactory.createMatteBorder(5,5,0,5, Color.WHITE));
        if (isSelected) {
            cell.setBackground(statusColor.brighter());
        }
        /*
        if (isFocused) {
            cell.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1,0,1,0,Color.GRAY),
                    BorderFactory.createEmptyBorder(0,5,0,5)));
        }
        */

        return cell;
    }

    private Component getSmallCellRendererComp(JList list, Object o, int i,
                                               boolean isSelected, boolean isFocused) {
        Task t = (Task)o;
        ListCellPanel cell = new ListCellPanel();
        cell.setLayout(new FlowLayout(FlowLayout.LEFT));
        cell.setPreferredSize(new Dimension(500,28));
        GridBagConstraints c = new GridBagConstraints();

        ImageIcon icon = smallIcons.get(t.getType());
        if (t.getStatus() == Task.Status.COMPLETE) {
            icon = ImageLoader.loadImage("check",false);
        }
        JLabel iconLabel = new JLabel(icon);
        cell.add(iconLabel);

        JLabel nameLabel = new JLabel(t.getName());
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        cell.add(nameLabel);

        String statusStr;
        Color statusColor;
        switch (t.getStatus()) {
            case BLOCKED:
                Roadblock b = t.getLastBlock();
                if (b != null) {
                    String dateStr = DateFormat.getDateInstance(DateFormat.FULL).format(b.getDate());
                    statusStr = "blocked by " + b.getDesc() + " as of " + dateStr;
                }  else {
                    statusStr = "blocked by unknown";
                }
                statusColor = ColorScheme.BLOCKED;
                break;
            case IN_PROGRESS:
                Workflow.State ws = t.getWorkflowState();
                statusStr = "currently in " + ws.getName() + " state";
                statusColor = ColorScheme.IN_PROGRESS;
                break;
            case PENDING:
                statusStr = " ";
                statusColor = ColorScheme.PENDING;
                break;
            default:
            case COMPLETE:
                String dateStr = DateFormat.getDateInstance(DateFormat.FULL).format(t.getCompleteDate());
                statusStr = "completed on " + dateStr;
                statusColor = ColorScheme.COMPLETE;
                break;
        }

        JLabel statusLabel = new JLabel(statusStr);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        cell.add(statusLabel);

        // Coloring
        cell.setBackground(statusColor);
        cell.setBorder(BorderFactory.createMatteBorder(3,3,0,3, Color.WHITE));
        if (isSelected) {
            cell.setBackground(statusColor.brighter());
        }
        /*
        if (isFocused) {
            cell.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1,0,1,0,Color.GRAY),
                    BorderFactory.createEmptyBorder(0,3,0,3)));
        }
        */

        return cell;
    }

}
