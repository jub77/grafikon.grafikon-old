/*
 * TCListView.java
 *
 * Created on 12. září 2007, 13:35
 */
package net.parostroj.timetable.gui.views;

import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.parostroj.timetable.actions.TrainsCycleSort;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEvent;
import net.parostroj.timetable.gui.ApplicationModelListener;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.utils.IdGenerator;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * List with all engine cycles and buttons for create and remove.
 *
 * @author jub
 */
public class TCListView extends javax.swing.JPanel implements ApplicationModelListener, ListSelectionListener {
    
    private ApplicationModel model;
    
    private TCDelegate delegate;
    
    /** Creates new form ECListView */
    public TCListView() {
        initComponents();
        ecList.addListSelectionListener(this);
    }
    
    public void setModel(ApplicationModel model, TCDelegate delegate) {
        this.model = model;
        this.delegate = delegate;
        model.addListener(this);
        
        this.updateView();
    }

    @Override
    public void modelChanged(ApplicationModelEvent event) {
        TCDelegate.Action action = delegate.transformEventType(event.getType());
        switch(event.getType()) {
            case SET_DIAGRAM_CHANGED:
                this.updateView();
                break;
            default:
                // nothing
        }
        
        if (action != null) {
            switch (action) {
                case NEW_CYCLE: case DELETE_CYCLE:
                    this.updateView();
                    break;
                case MODIFIED_CYCLE:
                    ecList.repaint();
                    break;
                case SELECTED_CHANGED:
                    break;
                default:
                    // nothing
                }
        }
    }
    
    private void updateView() {
        if (model.getDiagram() != null) {
            // set list
            List<TrainsCycle> sorted = (new TrainsCycleSort(TrainsCycleSort.Type.ASC)).sort(model.getDiagram().getCycles(delegate.getType()));
            DefaultListModel listModel = new DefaultListModel();
            for (TrainsCycle cycle : sorted) {
                listModel.addElement(cycle);
            }
            ecList.setModel(listModel);
        } else {
            ecList.setModel(new DefaultListModel());
        }
        
        boolean modelExists = model.getDiagram() != null;
        createButton.setEnabled(modelExists);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting())
            return;
        // set selected engine
        Object[] selectedValues = ecList.getSelectedValues();
        if (selectedValues.length == 1)
            delegate.setSelectedCycle(model,(TrainsCycle)ecList.getSelectedValue());
        else
            delegate.setSelectedCycle(model, null);
        deleteButton.setEnabled(selectedValues.length > 0);

    }
            
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPane = new javax.swing.JScrollPane();
        ecList = new javax.swing.JList();
        newNameTextField = new javax.swing.JTextField();
        createButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();

        scrollPane.setPreferredSize(new java.awt.Dimension(0, 132));

        ecList.setPrototypeCellValue("mmmmmmmmmmm");
        ecList.setVisibleRowCount(3);
        scrollPane.setViewportView(ecList);

        newNameTextField.setColumns(10);

        createButton.setText(ResourceLoader.getString("button.new")); // NOI18N
        createButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createButtonActionPerformed(evt);
            }
        });

        deleteButton.setText(ResourceLoader.getString("button.delete")); // NOI18N
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(createButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleteButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(newNameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
            .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(newNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deleteButton)
                    .addComponent(createButton)))
        );
    }// </editor-fold>//GEN-END:initComponents

private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
    // get selected cycles ...
    Object[] selectedValues = ecList.getSelectedValues();
    for (Object selectedObject : selectedValues) {
        TrainsCycle cycle = (TrainsCycle)selectedObject;
        if (cycle != null) {
            // remove from diagram
            model.getDiagram().removeCycle(cycle);
            // fire event
            delegate.fireEvent(TCDelegate.Action.DELETE_CYCLE, model, cycle);
        }
    }
}//GEN-LAST:event_deleteButtonActionPerformed

private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createButtonActionPerformed
    // get name from text field (ignore shorter than one character
    if (newNameTextField.getText().length() > 0) {
        TrainsCycle cycle = new TrainsCycle(IdGenerator.getInstance().getId(), newNameTextField.getText(),newNameTextField.getText(), delegate.getType());
        model.getDiagram().addCycle(cycle);
        
        // clear field
        newNameTextField.setText("");
        // fire event
        delegate.fireEvent(TCDelegate.Action.NEW_CYCLE, model, cycle);
        // set selected
        ecList.setSelectedValue(cycle, true);
        delegate.fireEvent(TCDelegate.Action.SELECTED_CHANGED, model, cycle);
    }
}//GEN-LAST:event_createButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton createButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JList ecList;
    private javax.swing.JTextField newNameTextField;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
    
}
