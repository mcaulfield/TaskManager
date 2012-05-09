package com.aloe.mtm.gui;

import com.aloe.mtm.control.MTMControl;
import com.aloe.mtm.control.TaskDetailsControl;
import com.aloe.mtm.data.Task;
import com.aloe.mtm.data.Workflow;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: mcaulfie
 * Date: 5/6/11
 * Time: 3:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class TaskDetailsPanel extends JPanel {

    private TaskDetailsControl control;

    private JTextField nameField;
    private JSpinner prioritySpinner;
    private JComboBox typeBox, statusBox, workflowBox, stateBox;
    private BlockListPanel blockPanel;
    private JTextArea notesArea;
    private JScrollPane notesScroll;

    public TaskDetailsPanel(TaskDetailsControl control) {
        this.control = control;

        // set up gui and listeners
        initGui();
        initListeners();

        // register self with control
        control.registerGui(this);
    }

    private void initGui() {
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(5,5,5,5);
        c.weightx = 0; c.weighty = 0;
        c.gridwidth = 1; c.gridheight = 1;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;

        // Labels
        c.gridx = 0; c.gridy = 0;
        this.add(new JLabel("Name"), c);
        c.gridx = 0; c.gridy = 1;
        this.add(new JLabel("Type"), c);
        c.gridx = 0; c.gridy = 2;
        this.add(new JLabel("Workflow"), c);
        c.gridx = 0; c.gridy = 3;
        this.add(new JLabel("Blocks"), c);
        c.gridx = 0; c.gridy = 5;
        this.add(new JLabel("Notes"), c);
        c.gridx = 2; c.gridy = 1;
        this.add(new JLabel("Priority"), c);
        c.gridx = 2; c.gridy = 2;
        this.add(new JLabel("State"), c);
        c.gridx = 4; c.gridy = 1;
        this.add(new JLabel("Status"), c);

        // Inputs
        c.weightx = 1; c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 5; c.gridheight = 1;

        nameField = new JTextField(80);
        c.gridx = 1; c.gridy = 0;
        this.add(nameField, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 1; c.gridheight = 1;

        typeBox = new JComboBox(Task.Type.values());
        c.gridx  = 1; c.gridy = 1;
        this.add(typeBox, c);

        prioritySpinner = new JSpinner();
        c.gridx  = 3; c.gridy = 1;
        this.add(prioritySpinner, c);

        statusBox = new JComboBox(Task.Status.values());
        c.gridx = 5; c.gridy = 1;
        this.add(statusBox, c);

        workflowBox = new JComboBox(control.getWorkflowListControl());
        c.gridx = 1; c.gridy = 2;
        this.add(workflowBox, c);

        stateBox = new JComboBox(control.getWorkflowListControl().getWorkflowStateListControl());
        c.gridx = 3; c.gridy = 2;
        this.add(stateBox, c);

        // Complex inputs
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0; c.weighty = 1;

        blockPanel = new BlockListPanel(control.getBlockListControl());
        c.gridx = 1; c.gridy = 3;
        c.gridwidth = 3; c.gridheight = 2;
        this.add(blockPanel, c);

        notesArea = new JTextArea();
        notesArea.setFont(nameField.getFont());
        notesArea.setLineWrap(true);
        notesScroll = new JScrollPane(notesArea);
        notesScroll.setBorder(BorderFactory.createEtchedBorder());
        notesScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        c.gridx = 1; c.gridy = 5;
        c.gridwidth = 5; c.gridheight = 2;
        this.add(notesScroll, c);

        // Spacers
        c.gridx = 6; c.gridy = 1;
        c.weightx = 1000; c.weighty = 0;
        this.add(Box.createHorizontalGlue(), c);

        setEnabled(false);
    }

    private void initListeners() {
        // Highlight Listeners
        FocusListener highlightListener = new FocusListener() {
            private HashMap<JComponent,Border> defaultBorder = new HashMap<JComponent,Border>();
            public void focusGained(FocusEvent focusEvent) {
                JComponent comp = (JComponent)focusEvent.getSource();
                defaultBorder.put(comp, comp.getBorder());
                int insets = 0;
                if (comp instanceof JTextField) {
                    insets = 2;
                }
                comp.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ColorScheme.HIGHLIGHT),
                        BorderFactory.createEmptyBorder(insets,insets,insets,insets)));
            }
            public void focusLost(FocusEvent focusEvent) {
                JComponent comp = (JComponent)focusEvent.getSource();
                if (defaultBorder.containsKey(comp)) {
                    comp.setBorder(defaultBorder.get(comp));
                }
            }
        };
        nameField.addFocusListener(highlightListener);
        typeBox.addFocusListener(highlightListener);
        statusBox.addFocusListener(highlightListener);
        workflowBox.addFocusListener(highlightListener);
        stateBox.addFocusListener(highlightListener);

        // Focus Listeners
        nameField.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent focusEvent) {
                nameField.selectAll();
            }
            public void focusLost(FocusEvent focusEvent) {
                nameField.select(0,0);
            }
        });
        final JFormattedTextField priorityField = ((JSpinner.DefaultEditor)prioritySpinner.getEditor()).getTextField();
        priorityField.addFocusListener(new FocusListener() {
            private Border defaultBorder = null;
            public void focusGained(FocusEvent focusEvent) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        priorityField.selectAll();
                    }
                });
                defaultBorder = prioritySpinner.getBorder();
                prioritySpinner.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ColorScheme.HIGHLIGHT),
                        BorderFactory.createEmptyBorder(2,2,2,2)));

            }
            public void focusLost(FocusEvent focusEvent) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        priorityField.select(0,0);
                    }
                });
                if (defaultBorder != null) {
                    prioritySpinner.setBorder(defaultBorder);
                }
            }
        });

        // Key listeners
        nameField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent keyEvent) {
                control.updateTaskName(nameField.getText());
            }
        });

        // Change listeners
        prioritySpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                control.updateTaskPriority((Integer)prioritySpinner.getValue());
            }
        });

        // Action listeners
        typeBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                control.updateTaskType((Task.Type)typeBox.getSelectedItem());
            }
        });
        statusBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                control.updateTaskStatus((Task.Status)statusBox.getSelectedItem());
            }
        });
        workflowBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                control.updateTaskWorkflow((Workflow)workflowBox.getSelectedItem());
            }
        });
        stateBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (stateBox.getSelectedItem() != null) {
                    control.updateTaskWorkflowState((Workflow.State)stateBox.getSelectedItem());
                }
            }
        });
        notesArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent documentEvent) {
                control.updateTaskNotes(notesArea.getText());
            }
            public void removeUpdate(DocumentEvent documentEvent) {
                control.updateTaskNotes(notesArea.getText());
            }
            public void changedUpdate(DocumentEvent documentEvent) {
                control.updateTaskNotes(notesArea.getText());
            }
        });

        // Popup listeners
        initWorkflowPopup();
    }

    private void initWorkflowPopup() {
        final JPopupMenu popup = new JPopupMenu();

        JMenuItem editWorkflowsItem = new JMenuItem("Edit Workflows...");
        editWorkflowsItem.setIcon(ImageLoader.loadImage("workflow"));
        editWorkflowsItem.setMnemonic(KeyEvent.VK_E);
        popup.add(editWorkflowsItem);

        workflowBox.addMouseListener(new MouseAdapter() {
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
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        editWorkflowsItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                control.editWorkflows();
            }
        });
    }

    public void setEnabled(boolean enabled) {
        nameField.setEnabled(enabled);
        prioritySpinner.setEnabled(enabled);
        typeBox.setEnabled(enabled);
        statusBox.setEnabled(enabled);
        workflowBox.setEnabled(enabled);
        stateBox.setEnabled(enabled);
        notesArea.setEnabled(enabled);
        notesArea.setBackground(enabled ? Color.WHITE : getBackground());
    }

    public void updateFields(Task t) {
        nameField.setText(t.getName());
        if (nameField.hasFocus()) {
            nameField.selectAll();
        }
        prioritySpinner.setValue(t.getPriority());
        typeBox.setSelectedItem(t.getType());
        statusBox.setSelectedItem(t.getStatus());
        workflowBox.setSelectedItem(t.getWorkflowState().getWorkflow());
        stateBox.setSelectedItem(t.getWorkflowState());
        notesArea.setText(t.getNotes());
    }

    public void clearFields() {
        nameField.setText("");
        prioritySpinner.setValue(0);
        typeBox.setSelectedItem(null);
        statusBox.setSelectedItem(null);
        workflowBox.setSelectedItem(null);
        notesArea.setText("");
    }

    public void setStatusSelection(Task.Status status) {
        statusBox.setSelectedItem(status);
    }
}
