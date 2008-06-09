/*
 * StartingPositionsList.java
 * 
 * Created on 11.9.2007, 19:33:20
 */
package net.parostroj.timetable.output;

import java.io.IOException;
import java.io.Writer;
import java.util.Formatter;
import net.parostroj.timetable.model.*;

/**
 * List of starting positions.
 * 
 * @author jub
 */
public class StartingPositionsList {
    
    private TrainDiagram diagram;
    
    private StartingPositionsTemplates templates;

    public StartingPositionsList(TrainDiagram diagram) {
        this.diagram = diagram;
        templates = new StartingPositionsTemplates();
    }

    public void writeTo(Writer writer) throws IOException {
        Formatter f = new Formatter(writer);
        f.format(templates.getHtmlHeader(), templates.getString("title"), templates.getString("title"));
        
        // engines
        f.format(templates.getSpSection(), templates.getString("section.ec"));
        for (TrainsCycle ecCycle : diagram.getCycles(TrainsCycleType.ENGINE_CYCLE)) {
            if (!ecCycle.isEmpty()) {
                TrainsCycleItem start = ecCycle.iterator().next();
                String startName = start.getFromNode().getName();
                f.format(templates.getSpLine(), ecCycle.getName(), ecCycle.getDescription(), startName, start.getTrain().getName());
            }
        }
        writer.write(templates.getSpSectionFooter());
        
        // train units
        f.format(templates.getSpSection(), templates.getString("section.tuc"));
        for (TrainsCycle tucCycle : diagram.getCycles(TrainsCycleType.TRAIN_UNIT_CYCLE)) {
            if (!tucCycle.isEmpty()) {
                TrainsCycleItem start = tucCycle.iterator().next();
                String startName = start.getFromNode().getName();
                f.format(templates.getSpLine(), tucCycle.getName(), tucCycle.getDescription(), startName, start.getTrain().getName());
            }
        }
        writer.write(templates.getSpSectionFooter());
        
        writer.write(templates.getHtmlFooter());
    }
}
