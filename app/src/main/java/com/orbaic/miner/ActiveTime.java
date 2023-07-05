package com.orbaic.miner;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

public class ActiveTime {

    private  HashMap<String,Object> t;

    public void getTime(){
        HashMap<String, Object> timestampMap = new HashMap<String, Object>();
        timestampMap.put("time", ServerValue.TIMESTAMP);
        this.t = timestampMap;
    }

    @Exclude
    public String getValueTime(){
        return  (String) t.get("time");
    }
}
