package com.sundown.photofragment.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sundown on 6/22/2015.
 */
public abstract class Field implements PropertiesHandler {

    private static final String FIELD_TYPE = "type";

    /** used for view tagging */
    private int id;

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    private FieldType type;


    public Field(FieldType type) {
        this.type = type;
    }

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> properties = new HashMap();
        properties.put(FIELD_TYPE, type.name());
        return properties;
    }

    @Override
    public Field setProperties(Map properties) {
        type = FieldType.valueOf(properties.get(FIELD_TYPE).toString());
        return this;
    }
}

