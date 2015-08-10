package com.sundown.photofragment.models;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sundown on 7/27/2015.
 *
 * Basically acts as a wrapper for the map of fields with a few additional functions
 */
public class Photos {

    //maps Field to ID, extend and add your own fields as you see fit..
    private Map<Integer, Field> fields;

    public Photos(){ fields = new HashMap<>();}

    //returns the next available ID for a new Field..
    private int getNextKey(){
        Object[] keys = getKeys();
        int size = keys.length;
        int lastKey = 0;
        if (size > 0) lastKey = (int) keys[size-1];
        return ++lastKey;
    }

    public void removeField(int id){
        fields.remove(id);
    }

    public Field getField(Integer id){ return fields.get(id);}

    public int addField(Field field){
        int nextKey = getNextKey();
        fields.put(nextKey, field);
        return nextKey;
    }

    //returns all IDs in ascending order
    public Integer[] getKeys(){
        Object[] keys = fields.keySet().toArray();
        Arrays.sort(keys);
        return Arrays.copyOf(keys, keys.length, Integer[].class);
    }


}
