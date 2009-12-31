package net.parostroj.timetable.model;

import net.parostroj.timetable.visitors.TrainDiagramVisitor;

/**
 * Line track.
 * 
 * @author jub
 */
public class LineTrack extends Track {

    private NodeTrack fromStraightTrack;
    private NodeTrack toStraightTrack;
    
    // reference to line
    Line line;

    /**
     * Constructor.
     * 
     * @param id id
     */
    public LineTrack(String id) {
        super(id);
    }

    /**
     * Constructor with number of the track.
     * 
     * @param id id
     * @param number track number
     */
    public LineTrack(String id, String number) {
        super(id, number);
    }

    public NodeTrack getFromStraightTrack() {
        return fromStraightTrack;
    }

    public void setFromStraightTrack(NodeTrack fromStraightTrack) {
        this.fromStraightTrack = fromStraightTrack;
        this.fireAttributeChanged("fromStraightTrack");
    }

    public NodeTrack getToStraightTrack() {
        return toStraightTrack;
    }

    public void setToStraightTrack(NodeTrack toStraightTrack) {
        this.toStraightTrack = toStraightTrack;
        this.fireAttributeChanged("toStraightTrack");
    }

    public NodeTrack getFromStraightTrack(TimeIntervalDirection direction) {
        return (direction == TimeIntervalDirection.FORWARD) ? fromStraightTrack : toStraightTrack;
    }

    public NodeTrack getToStraightTrack(TimeIntervalDirection direction) {
        return (direction == TimeIntervalDirection.FORWARD) ? toStraightTrack : fromStraightTrack;
    }

    @Override
    void fireAttributeChanged(String attributeName) {
        if (line != null)
            line.fireTrackAttributeChanged(attributeName, this);
    }

    /**
     * accepts visitor.
     *
     * @param visitor visitor
     */
    void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
    }
}
