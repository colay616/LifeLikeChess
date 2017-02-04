package com.ue.moon_chess.entity;

import android.text.TextUtils;

import com.hyphenate.easeui.game.db.DataParser;
import com.ue.common.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hawk on 2017/1/6.
 */

public class RecordParser implements DataParser<MoonRecord>{
    @Override
    public MoonRecord parse(int id, String data) {
        try {
            JSONObject dataJson=new JSONObject(data);
            MoonRecord record=new MoonRecord();
            record.id=id;
            record.fromF=dataJson.optInt("fromF",-1);
            record.fromC=dataJson.optInt("fromC",-1);
            record.toF=dataJson.optInt("toF",-1);
            String killedFsStr=dataJson.optString("killedFs",null);
            if(!TextUtils.isEmpty(killedFsStr)){
                record.killedFs=new JSONArray(killedFsStr);
            }
            return record;
        } catch (JSONException e) {
            LogUtil.i("RecordParser",e.getMessage());
        }
        return null;
    }
}
