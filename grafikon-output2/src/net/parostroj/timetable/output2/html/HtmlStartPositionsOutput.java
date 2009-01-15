package net.parostroj.timetable.output2.html;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.StartPositionsOutput;
import net.parostroj.timetable.output2.impl.Position;
import net.parostroj.timetable.output2.impl.PositionsExtractor;
import net.parostroj.timetable.output2.util.ResourceHelper;
import org.mvel2.templates.TemplateRuntime;

/**
 * Starting positions output to html.
 *
 * @author jub
 */
public class HtmlStartPositionsOutput implements StartPositionsOutput {

    private Locale locale;
    private TrainDiagram diagram;

    HtmlStartPositionsOutput(TrainDiagram diagram, Locale locale) {
        this.locale = locale;
        this.diagram = diagram;
    }

    @Override
    public void writeTo(Writer writer) throws IOException {
        // extract positions
        PositionsExtractor pe = new PositionsExtractor(diagram);
        List<Position> engines = pe.getStartPositionsEngines();
        List<Position> trainUnits = pe.getStartPositionsTrainUnits();

        // call template
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("engines", engines);
        map.put("train_units", trainUnits);
        ResourceHelper.addTextsToMap(map, "start_positions_", locale, "texts/html_texts");

        String template = ResourceHelper.readResource("/templates/start_positions.html");
        String ret = (String) TemplateRuntime.eval(template, map);

        writer.write(ret);
        writer.flush();
    }
}
