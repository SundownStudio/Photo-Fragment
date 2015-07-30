package com.sundown.photofragment.models;

import com.sundown.photofragment.interfaces.PropertiesHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sundown on 6/22/2015.
 */
public abstract class Field implements PropertiesHandler {

    public static final String FIELD_TYPE = "type";
    public int id;
    public void setId(int id){ this.id = id;}
    public int type;


    public Field(int id, int type){
        this.id = id;
        this.type = type;
    }

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> properties = new HashMap();
        properties.put(FIELD_TYPE, type);
        return properties;
    }

    @Override
    public Field setProperties(Map properties) {
        type = (Integer) properties.get(FIELD_TYPE);
        return this;
    }
}

