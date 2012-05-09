package com.aloe.mtm.gui;

import com.aloe.mtm.control.StatusControl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: mcaulfie
 * Date: 5/11/11
 * Time: 11:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class StatusPanel extends JPanel {

    private StatusControl control;

    private JLabel statusLabel;
    private JProgressBar progressBar;
    private Timer timer;

    public StatusPanel(StatusControl control) {
        this.control = control;
        control.registerGui(this);

        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(0,4,0,4));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0;
        c.gridwidth = 1; c.gridheight = 1;
        c.weightx = 0; c.weighty = 0;
        c.fill = GridBagConstraints.NONE;

        statusLabel = new JLabel(" ");
        Font font = statusLabel.getFont().deriveFont(10f);
        statusLabel.setFont(font);
        c.gridx = 0;
        this.add(statusLabel, c);

        progressBar = new JProgressBar(0,100);
        progressBar.setPreferredSize(new Dimension(100, 10));
        progressBar.setVisible(false);
        c.gridx = 1;
        c.insets = new Insets(0,5,0,0);
        this.add(progressBar, c);

        c.weightx = 1;
        c.gridx = 2;
        this.add(Box.createHorizontalGlue(), c);

        timer = new Timer(5000, new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                clearStatusInfo();
            }
        });
    }

    public void setStatusInfo(String status, double progress) {
        statusLabel.setText(status);
        progressBar.setVisible(progress > 0);
        progressBar.setValue((int)(100*progress));
        timer.restart();
    }

    public void clearStatusInfo() {
        statusLabel.setText(" ");
        progressBar.setVisible(false);
        progressBar.setValue(0);
    }

}
