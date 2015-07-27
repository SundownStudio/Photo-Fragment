package com.sundown.photofragment.interfaces;

import java.util.Map;

/**
 * Created by Sundown on 6/16/2015.
 */
public interface PropertiesHandler<T> {
    Map<String, Object> getProperties();
    T setProperties(Map<String, Object> properties);
}
