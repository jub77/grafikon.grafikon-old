package net.parostroj.timetable.model;

import java.util.HashMap;

/**
 * Class for attributes (train's or node's).
 * 
 * @author jub
 */
public class Attributes extends HashMap<String, Object> {

    /**
     * Default constructor.
     */
    public Attributes() {
    }

    /**
     * Copy constructor (shallow copy).
     * 
     * @param attributes copied attributes
     */
    public Attributes(Attributes attributes) {
        super(attributes);
    }
}
