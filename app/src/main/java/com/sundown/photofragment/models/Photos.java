package com.sundown.photofragment.models;

import com.sundown.photofragment.pojo.Field;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sundown on 7/27/2015.
 */
public class Photos {

    //extend and add your own fields as you see fit..
    private Map<Integer, Field> fields;

    public Photos(){ fields = new HashMap<>();}

    public Integer[] getKeys(){
        Object[] keys = fields.keySet().toArray();
        Arrays.sort(keys);
        return Arrays.copyOf(keys, keys.length, Integer[].class);
    }

    public int getNextKey(){
        Object[] keys = getKeys();
        int size = keys.length;
        int lastKey = 0;
        if (size > 0)
            lastKey = (int) keys[size-1];
        return ++lastKey;
    }

    public void remove(int id){
        fields.remove(id);
    }

    public Field get(Integer id){
        return fields.get(id);
    }

    public int addField(Field field){
        int nextKey = getNextKey();
        fields.put(nextKey, field);
        return nextKey;
    }


}
