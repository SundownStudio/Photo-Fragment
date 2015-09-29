package com.sundown.photofragment.models;

import java.util.Map;

/**
 * Created by Sundown on 9/29/2015.
 */
public interface PropertiesHandler</*S, */T> {
    Map<String, Object> getProperties(/*S s*/);
    T setProperties(Map<String, Object> properties);
}
