package com.aloe.mtm.gui;

import com.aloe.mtm.control.BlockListControl;
import com.aloe.mtm.control.MTMControl;
import com.aloe.mtm.data.Roadblock;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mcaulfie
 * Date: 5/8/11
 * Time: 2:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class BlockListPanel extends JPanel {

    private BlockListControl control;

    private JList blockList;
    private JScrollPane blockScroll;
    private JTextField newBlockField;
    private JButton addButton;

    private class BlockCellRenderer implements ListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object o, int index,
                                                      boolean isSelected, boolean isFocused) {
            Roadblock block = (Roadblock)o;
            JPanel cell = new JPanel();
            cell.setLayout(new FlowLayout(FlowLayout.LEFT));

            JCheckBox doneCheck = new JCheckBox();
            doneCheck.setBorder(null);
            doneCheck.setSelected(block.getStatus() == Roadblock.Status.DONE);
            doneCheck.setEnabled(list.isEnabled());
            cell.add(doneCheck);

            JLabel blockLabel = new JLabel(block.getDesc());
            blockLabel.setBorder(null);
            cell.add(blockLabel);

            if (isSelected) {
                cell.setBackground(new Color(230,230,255));
            } else {
                cell.setOpaque(false);
            }

            return cell;
        }
    }

    public BlockListPanel(BlockListControl control) {
        this.control = control;
        initGui();
        initListeners();
        control.registerGui(this);
    }

    public void initGui() {
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(0,0,0,0);
        c.weightx = 1; c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.WEST;

        blockList = new JList(control);
        blockList.setCellRenderer(new BlockCellRenderer());
        blockList.setVisibleRowCount(1);
        blockScroll = new JScrollPane(blockList);
        blockScroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        c.gridx = 0; c.gridy = 0;
        c.gridwidth = 2; c.gridheight = 1;
        c.insets = new Insets(0,0,1,0);
        this.add(blockScroll, c);

        newBlockField = new JTextField();
        c.gridx = 0; c.gridy = 1;
        c.weightx = 1; c.weighty = 0;
        c.gridwidth = 1; c.gridheight = 1;
        c.insets = new Insets(1,0,0,1);
        this.add(newBlockField, c);

        addButton = new JButton("Add");
        c.gridx = 1; c.gridy = 1;
        c.weightx = 0; c.weighty = 0;
        c.insets = new Insets(1,1,0,0);
        this.add(addButton, c);

        setEnabled(false);
    }

    public void initListeners() {
        // Focus Listener
        newBlockField.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent focusEvent) {
                newBlockField.selectAll();
            }
            public void focusLost(FocusEvent focusEvent) {
                newBlockField.select(0,0);
            }
        });

        // Highlight Listeners
        newBlockField.addFocusListener(new FocusListener() {
            private Border defaultBorder = null;
            public void focusGained(FocusEvent focusEvent) {
                defaultBorder = newBlockField.getBorder();
                newBlockField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ColorScheme.HIGHLIGHT),
                        BorderFactory.createEmptyBorder(2, 2, 2, 2)));
            }
            public void focusLost(FocusEvent focusEvent) {
                if (defaultBorder != null) {
                    newBlockField.setBorder(defaultBorder);
                }
            }
        });
        blockList.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent focusEvent) {
                blockScroll.setBorder(BorderFactory.createLineBorder(ColorScheme.HIGHLIGHT));
            }
            public void focusLost(FocusEvent focusEvent) {
                blockScroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            }
        });

        // Action listeners
        ActionListener addListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                String text = newBlockField.getText();
                newBlockField.setText("");
                if (text.trim().isEmpty()) {
                    return;
                }
                control.addNewBlock(text);
            }
        };
        newBlockField.addActionListener(addListener);
        addButton.addActionListener(addListener);

        // Selection listener
        blockList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                control.setSelected((Roadblock)blockList.getSelectedValue());
            }
        });

        // Key listener
        blockList.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent keyEvent) {
                if (blockList.getSelectedValue() == null) {
                    return;
                }
                if (keyEvent.getKeyCode() == KeyEvent.VK_DELETE) {
                    control.removeSelected();
                } else if (keyEvent.getKeyCode() == KeyEvent.VK_SPACE) {
                    control.toggleSelectedStatus();
                }
            }
        });

        // Mouse listener
        blockList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    control.toggleSelectedStatus();
                }
            }
        });
    }

    public void setEnabled(boolean enabled) {
        blockList.setEnabled(enabled);
        blockList.setBackground(enabled ? Color.WHITE : getBackground());
        newBlockField.setEnabled(enabled);
        addButton.setEnabled(enabled);
    }

    public void clearFields() {
        newBlockField.setText("");
    }

    public void setSelectedIndex(int i, boolean takeFocus) {
        if (!takeFocus) {
            blockList.setFocusable(false);
        }
        blockList.setSelectedIndex(i);
        blockList.ensureIndexIsVisible(i);
        if (!takeFocus) {
            blockList.setFocusable(true);
        }
    }

}
