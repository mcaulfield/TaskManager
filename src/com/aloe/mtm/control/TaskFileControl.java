package com.aloe.mtm.control;

import com.aloe.mtm.control.event.*;
import com.aloe.mtm.data.Roadblock;
import com.aloe.mtm.data.Task;
import com.aloe.mtm.data.Workflow;
import com.aloe.mtm.gui.TaskFileDialog;

import java.beans.XMLEncoder;
import java.io.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: mcaulfie
 * Date: 5/14/11
 * Time: 10:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class TaskFileControl extends ControlEventAdapter {

    private List<Workflow> workflows;     // read-only workflows (should never be written to)
    private List<Task> tasks;             // read-only tasks (should never be written to)

    private MTMControl control;
    private TaskFileDialog gui;

    private String saveName = "";
    private boolean saveInProgress = false;
    private boolean loadInProgress = false;

    public TaskFileControl(MTMControl control, List<Workflow> workflows, List<Task> tasks) {
        this.control = control;
        this.workflows = workflows;
        this.tasks = tasks;
        control.addControlEventListener(this);
    }

    /** GUI API **/

    public void registerGui(TaskFileDialog gui) {
        this.gui = gui;
    }

    public void setStatus(String status, double progress) {
        fireStatusEvent(status, progress);
    }

    /** Control API **/

    public synchronized void loadDefaultFile() {
        if (loadInProgress) {
            return;
        }
        loadInProgress = true;
        new Thread(new Runnable() {
            public void run() {
                loadDefaultFileThread();
            }
        }).start();
    }

    /** Control Event Handlers **/

    public synchronized void handleNewListEvent() {
        saveName = "";
    }

    public synchronized void handleLoadListEvent() {
        if (loadInProgress) {
            return;
        }
        loadInProgress = true;
        new Thread(new Runnable() {
            public void run() {
                loadTaskFileThread();
            }
        }).start();
    }

    public synchronized void handleSaveListEvent() {
        if (saveInProgress) {
            return;
        }
        saveInProgress = true;
        new Thread(new Runnable() {
            public void run() {
                saveTaskFileThread();
            }
        }).start();
    }

    /** Save/Load Threads **/

    private void loadDefaultFileThread() {
        File f = new File(".tmf_default");
        if (!f.exists() || !f.isFile()) {
            loadFailed();
            return;
        }
        fireStatusEvent("Reading default file name...",0);
        String defaultFileName = "";
        try {
            Scanner scan = new Scanner(new File(".tmf_default"));
            scan.useDelimiter("\\Z");
            if (!scan.hasNext()) {
                loadFailed();
                return;
            }
            defaultFileName = scan.next().trim();
        } catch (FileNotFoundException e) {
            loadFailed();
            return;
        }
        if (loadTaskFile(new File(defaultFileName))) {
            loadSuccess(defaultFileName);
        } else {
            loadFailed();
        }
    }

    private void loadTaskFileThread() {
        File loadFile = gui.getLoadFile();
        if (loadFile == null) {
            loadFailed();
            return;
        }
        if (loadTaskFile(loadFile)) {
            try {
                PrintWriter out = new PrintWriter(new File(".tmf_default"));
                out.write(loadFile.getAbsolutePath());
                out.close();
            } catch (IOException e) {}
            loadSuccess(loadFile.getAbsolutePath());
        } else {
            loadFailed();
        }
    }

    private void saveTaskFileThread() {
        String saveName = this.saveName;
        String filename = null;
        if (saveName.isEmpty()) {
            File saveFile = gui.getSaveFile();
            if (saveFile == null) {
                saveFailed();
                return;
            }
            filename = saveFile.getAbsolutePath();
            if (!filename.endsWith(".tmf")) {
                filename += ".tmf";
            }
        } else {
            filename = saveName;
        }
        if (saveTaskFile(new File(filename))) {
            try {
                PrintWriter out = new PrintWriter(new File(".tmf_default"));
                out.write(filename);
                out.close();
            } catch (IOException e) {}
            saveSuccess(filename);
        } else {
            loadFailed();
        }
    }

    /** Helper Functions **/

    private void fireStatusEvent(String status, double progress) {
        StatusControlEvent e = new StatusControlEvent(status, progress);
        control.scheduleControlEvent(e);
    }

    private boolean loadTaskFile(File f) {
        List<Task> taskList = new LinkedList<Task>();
        control.scheduleControlEvent(ControlEvent.NEW_LIST);
        try {
            Scanner scan = new Scanner(f);
            Workflow currWorkflow = null;
            Task currTask = null;
            Roadblock currBlock = null;
            HashMap<String, Workflow> wfMap = new HashMap<String, Workflow>();
            wfMap.put("default", Workflow.getDefault());
            while (scan.hasNextLine()) {
                Scanner lineScan = new Scanner(scan.nextLine());
                lineScan.useDelimiter(":");
                String key = lineScan.next().trim();
                String value = lineScan.next().trim();
                while (lineScan.hasNext()) {
                    value += ":" + lineScan.next().trim();
                }
                if (key.equals("workflow")) {
                    currWorkflow = new Workflow();
                    currWorkflow.setName(value);
                    wfMap.put(value, currWorkflow);
                    control.scheduleControlEvent(new WorkflowControlEvent(
                            ControlEvent.Type.WORKFLOW_CREATED, currWorkflow));
                } else if (key.startsWith("workflow.")) {
                    if (currWorkflow == null) {
                        continue;
                    }
                    if (key.equals("workflow.state")) {
                        Workflow.State state = new Workflow.State(currWorkflow, value);
                        currWorkflow.addState(state);
                        control.scheduleControlEvent(new WorkflowStateControlEvent(
                                ControlEvent.Type.WF_STATE_CREATED, state));
                    }
                } else if (key.equals("task")) {
                    currTask = new Task();
                    currTask.setName(value);
                    taskList.add(currTask);
                } else if (key.startsWith("task.")) {
                    if (currTask == null) {
                        continue;
                    }
                    if (key.equals("task.type")) {
                        currTask.setType(Task.Type.valueOf(value));
                    } else if (key.equals("task.priority")) {
                        currTask.setPriority(Integer.parseInt(value));
                    } else if (key.equals("task.status")) {
                        currTask.setStatus(Task.Status.valueOf(value));
                    } else if (key.equals("task.workflow")) {
                        Workflow.State state = wfMap.get(value).getStartState();
                        currTask.setWorkflowState(state);
                    } else if (key.equals("task.state")) {
                        Workflow wf = currTask.getWorkflowState().getWorkflow();
                        currTask.setWorkflowState(wf.getState(value));
                    } else if (key.equals("task.complete-date")) {
                        currTask.setCompleteDate(new Date(Long.parseLong(value)));
                    } else if (key.equals("task.block")) {
                        currBlock = new Roadblock();
                        currBlock.setDesc(value);
                        currTask.getBlocks().add(currBlock);
                    } else if (key.startsWith("task.block.")) {
                        if (currBlock == null) {
                            continue;
                        }
                        if (key.equals("task.block.date")) {
                            currBlock.setDate(new Date(Long.parseLong(value)));
                        } else if (key.equals("task.block.status")) {
                            currBlock.setStatus(Roadblock.Status.valueOf(value));
                        }
                    } else if (key.equals("task.notes")) {
                        if (currTask.getNotes().isEmpty()) {
                            currTask.setNotes(value);
                        } else {
                            currTask.setNotes(currTask.getNotes() + "\n" + value);
                        }
                    }
                }
            }
        } catch (IOException e) {

        }

        control.scheduleControlEvent(new TaskControlEvent(ControlEvent.Type.TASK_MULTI_CREATED, taskList));

        return true;
    }

    private boolean saveTaskFile(File f) {
        LinkedList<Workflow> workflowClones = new LinkedList<Workflow>();
        synchronized (workflows) {
            for (Workflow w : workflows) {
                if (w == Workflow.getDefault()) {
                    continue;
                }
                workflowClones.add(w.clone());
            }
        }
        LinkedList<Task> taskClones = new LinkedList<Task>();
        synchronized (tasks) {
            for (Task t : tasks) {
                taskClones.add(t.clone());
            }
        }
        try {
            double items = 0;
            double totalItems = workflowClones.size()+taskClones.size();
            PrintWriter out = new PrintWriter(f);
            for (Workflow w : workflowClones) {
                out.println("workflow: " + w.getName());
                for (Workflow.State s : w.getStates()) {
                    out.println("workflow.state: " + s.getName());
                }
                fireStatusEvent("Saving " + f.getName() + "...",  ++items/totalItems);
            }
            for (Task t : taskClones) {
                out.println("task: " + t.getName());
                out.println("task.type: " + t.getType().name());
                out.println("task.priority: " + t.getPriority());
                out.println("task.status: " + t.getStatus().name());
                out.println("task.workflow: " + t.getWorkflowState().getWorkflow().getName());
                out.println("task.state: " + t.getWorkflowState().getName());
                for (Roadblock b : t.getBlocks()) {
                    out.println("task.block: " + b.getDesc());
                    out.println("task.block.date:" + b.getDate().getTime());
                    out.println("task.block.status:" + b.getStatus());
                }
                out.println("task.complete-date: " + t.getCompleteDate().getTime());
                Scanner notesScan = new Scanner(t.getNotes());
                while (notesScan.hasNextLine()) {
                    out.println("task.notes: " + notesScan.nextLine());
                }
                fireStatusEvent("Saving " + f.getName() + "...", ++items / totalItems);
            }
            out.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private synchronized void loadFailed() {
        loadInProgress = false;
        fireStatusEvent("Failed to load tasks",0);
    }

    private synchronized void saveFailed() {
        saveInProgress = false;
        fireStatusEvent("Failed to save tasks",0);
    }

    private synchronized void loadSuccess(String filename) {
        loadInProgress = false;
        saveName = filename;
        fireStatusEvent("Loaded tasks from file " + filename, 0);
    }

    private synchronized void saveSuccess(String filename) {
        saveInProgress = false;
        saveName = filename;
        fireStatusEvent("Saved tasks to file " + filename, 0);
    }
}
