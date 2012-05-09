package com.aloe.mtm.control;

import com.aloe.mtm.data.Roadblock;
import com.aloe.mtm.gui.BlockListPanel;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mcaulfie
 * Date: 5/8/11
 * Time: 3:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class BlockListControl implements ListModel {

    private TaskDetailsControl control;
    private List<Roadblock> blocks;
    private BlockListPanel gui;
    private LinkedList<ListDataListener> listeners = new LinkedList<ListDataListener>();
    private Roadblock selected;

    public BlockListControl(TaskDetailsControl control, List<Roadblock> blocks) {
        this.control = control;
        this.blocks = blocks;
    }

    /** ListModel Impl **/

    public int getSize() {
        if (blocks == null) {
            return 0;
        }
        return blocks.size();
    }

    public Object getElementAt(int i) {
        if (blocks == null) {
            return null;
        }
        return blocks.get(i);
    }

    public void addListDataListener(ListDataListener listDataListener) {
        synchronized (listeners) {
            listeners.add(listDataListener);
        }
    }

    public void removeListDataListener(ListDataListener listDataListener) {
        synchronized (listeners) {
            listeners.remove(listDataListener);
        }
    }

    private void fireListDataEvent(ListDataEvent event) {
        synchronized (listeners) {
            for (ListDataListener l : listeners) {
                switch (event.getType()) {
                    case ListDataEvent.CONTENTS_CHANGED:
                        l.contentsChanged(event); break;
                    case ListDataEvent.INTERVAL_ADDED:
                        l.intervalAdded(event); break;
                    case ListDataEvent.INTERVAL_REMOVED:
                        l.intervalRemoved(event); break;
                }
            }
        }
    }

    /** GUI API **/

    public void registerGui(BlockListPanel gui) {
        this.gui = gui;
    }

    public void addNewBlock(String desc) {
        Roadblock b = new Roadblock();
        b.setDesc(desc);
        int index = 0;
        synchronized (blocks) {
            blocks.add(b);
            index = blocks.indexOf(b);
        }
        fireListDataEvent(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index));
        gui.setSelectedIndex(index, false);
        control.blockListUpdated();
    }

    public void setSelected(Roadblock r) {
        selected = r;
    }

    public void removeSelected() {
        if (selected == null) {
            return;
        }
        int index = 0;
        synchronized (blocks) {
            index = blocks.indexOf(selected);
            blocks.remove(selected);
        }
        fireListDataEvent(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index));
        index = Math.min(index,getSize()-1);
        gui.setSelectedIndex(index, true);
        control.blockListUpdated();
    }

    public void toggleSelectedStatus() {
        if (selected == null) {
            return;
        }
        int index = 0;
        synchronized (blocks) {
            index = blocks.indexOf(selected);
        }
        if (selected.getStatus() == Roadblock.Status.WAITING) {
            selected.setStatus(Roadblock.Status.DONE);
        } else {
            selected.setStatus(Roadblock.Status.WAITING);
        }
        fireListDataEvent(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index, index));
        control.blockListUpdated();
    }

    /** Control API **/

    public void handleNewBlockListEvent(List<Roadblock> blocks) {
        if (this.blocks == blocks) {
            return;
        }
        gui.clearFields();
        if (blocks != null) {
            gui.setEnabled(true);
        } else {
            gui.setEnabled(false);
        }
        if (this.blocks != null && this.blocks.size() > 0) {
            fireListDataEvent(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, 0, getSize()-1));
        }
        this.blocks = blocks;
        if (blocks != null && blocks.size() > 0) {
            fireListDataEvent(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, 0, getSize()-1));
        }
    }

    public void handleBlockListUpdatedEvent(List<Roadblock> blocks) {
        if (getSize() == 0) {
            return;
        }
        fireListDataEvent(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, getSize()-1));
    }

}
