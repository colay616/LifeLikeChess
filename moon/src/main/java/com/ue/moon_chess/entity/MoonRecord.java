package com.ue.moon_chess.entity;

import org.json.JSONArray;

/**
 * Created by hawk on 2016/12/14.
 */

public class MoonRecord {
    public int id;
    public int fromF;
    public int fromC;
    public int toF;
    public JSONArray killedFs;

    @Override
    public String toString() {
        if (null == killedFs) {
            killedFs = new JSONArray();
        }
        return String.format("{'fromF':%d,'fromC':%d,'toF':%d,'killedFs':%s}",
                fromF,fromC,toF, killedFs.toString());
    }
}
