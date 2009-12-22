package net.parostroj.timetable.gui.dialogs;

import java.awt.Frame;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.parostroj.timetable.actions.TrainComparator;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEvent;
import net.parostroj.timetable.gui.components.TrainsWithConflictsPanel;
import net.parostroj.timetable.gui.helpers.TrainWrapper;
import net.parostroj.timetable.mediator.Colleague;
import net.parostroj.timetable.mediator.Mediator;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.events.GTEvent;
import net.parostroj.timetable.model.events.TrainDiagramEvent;
import net.parostroj.timetable.model.events.TrainEvent;

/**
 * Factory for creation of
 *
 * @author jub
 */
public class FloatingDialogsFactory {

    public static FloatingDialog createTrainsWithConflictsDialog(final Frame frame, final Mediator mediator, final ApplicationModel model) {
        final TrainsWithConflictsPanel panel = new TrainsWithConflictsPanel();
        panel.addTrainSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    JList list = (JList)e.getSource();
                    TrainWrapper wrapper = (TrainWrapper)list.getSelectedValue();
                    if (wrapper != null) {
                        if (wrapper.getElement() != model.getSelectedTrain()) {
                            model.setSelectedTrain(wrapper.getElement());
                        }
                    }
                }
            }
        });
        final FloatingDialog dialog = new FloatingDialog(frame, panel, "dialog.trainconflicts.title", "train.conflicts");
        mediator.addColleague(new Colleague() {
            @Override
            public void receiveMessage(Object message) {
                if (message instanceof GTEvent<?>) {
                    GTEvent<?> event = (GTEvent<?>)message;
                    do {
                        this.processGTEvent(event);
                        event = event.getNestedEvent();
                    } while (event != null);
                } else if (message instanceof ApplicationModelEvent)
                    this.processApplicationModelEvent((ApplicationModelEvent)message);
            }

            private void processGTEvent(GTEvent<?> event) {
                if (event instanceof TrainEvent) {
                    TrainEvent tEvent = (TrainEvent)event;
                    if (tEvent.getType() == TrainEvent.Type.TIME_INTERVAL_LIST || tEvent.getType() == TrainEvent.Type.TECHNOLOGICAL) {
                        panel.updateTrain(tEvent.getSource());
                    }
                } else if (event instanceof TrainDiagramEvent) {
                    TrainDiagramEvent tEvent = (TrainDiagramEvent)event;
                    TrainDiagramEvent.Type type = tEvent.getType();
                    if (type == TrainDiagramEvent.Type.TRAIN_ADDED)
                        panel.updateTrain(tEvent.getTrain());
                    else if (type == TrainDiagramEvent.Type.TRAIN_REMOVED)
                        panel.removeTrain(tEvent.getTrain());
                }
            }

            private void processApplicationModelEvent(ApplicationModelEvent event) {
                switch (event.getType()) {
                    case SELECTED_TRAIN_CHANGED:
                        panel.updateSelectedTrain((Train)event.getObject());
                        break;
                    case SET_DIAGRAM_CHANGED:
                        panel.setTrainComparator(model.getDiagram() != null ? new TrainComparator(TrainComparator.Type.ASC, model.getDiagram().getTrainsData().getTrainSortPattern()) : null);
                        panel.updateAllTrains(model.getDiagram() != null ? model.getDiagram().getTrains() : null);
                        break;
                }
            }
        });
        return dialog;
    }

    public static FloatingDialogsList createDialogs(Frame frame, Mediator mediator, ApplicationModel model) {
        FloatingDialogsList list = new FloatingDialogsList();
        list.add(createTrainsWithConflictsDialog(frame, mediator, model));
        return list;
    }
}
