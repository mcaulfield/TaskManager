package com.aloe.mtm.gui;

import com.aloe.mtm.control.TaskFileControl;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.lang.reflect.Field;

/**
 * Created by IntelliJ IDEA.
 * User: mcaulfie
 * Date: 5/15/11
 * Time: 12:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class TaskFileDialog {

    private TaskFileControl control;
    private JFileChooser chooser;
    private boolean initialized = false;

    public TaskFileDialog(TaskFileControl control) {
        this.control = control;
        control.registerGui(this);

        new Thread(new Runnable() {
            public void run() {
                chooser = new JFileChooser();
                chooser.setFileFilter(new FileFilter() {
                    public boolean accept(File file) {
                        if (file == null) {
                            return false;
                        }
                        String name = file.getName();
                        int i = name.lastIndexOf(".");
                        if (i < 0) {
                            return false;
                        }
                        String ext = name.substring(i);
                        if (!ext.equals(".tmf")) {
                            return false;
                        }
                        return true;
                    }
                    public String getDescription() {
                        return "Task Manager File (*.tmf)";
                    }
                });
                synchronized (TaskFileDialog.this) {
                    initialized = true;
                    TaskFileDialog.this.notifyAll();
                }
            }
        }).start();
    }

    public File getLoadFile() {
        awaitInit();
        int rv = chooser.showOpenDialog(null);
        if (rv == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }

    public File getSaveFile() {
        awaitInit();
        int rv = chooser.showSaveDialog(null);
        if (rv == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }

    private synchronized void awaitInit() {
        double progress = 0.0;
        while (!initialized) {
            control.setStatus("Launching file chooser...", progress);
            try { this.wait(250); } catch (Exception e) {}
            progress = (progress+0.1)%1.0;
        }
        control.setStatus(" ",0);
    }
}
