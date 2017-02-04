package com.ue.reversi.bean;

import android.text.TextUtils;

import com.hyphenate.easeui.game.db.DataParser;
import com.ue.common.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hawk on 2017/1/6.
 */

public class RecordParser implements DataParser<ReversiRecord>{
    @Override
    public ReversiRecord parse(int id, String data) {
        try {
            JSONObject dataJson=new JSONObject(data);
//            return String.format("{'x':%d,'y':%d,'c':%d,'changedChess':%s}",x,y,c,changedChess.toString());
            ReversiRecord record=new ReversiRecord();
            record.id=id;
            record.x=dataJson.optInt("x",-1);
            record.y=dataJson.optInt("y",-1);
            record.c=dataJson.optInt("c",-1);

            String changedChessStr=dataJson.optString("changedChess",null);
            if (!TextUtils.isEmpty(changedChessStr)) {
                JSONArray changedChessArr=new JSONArray(changedChessStr);
                List<ReversiMove>changedChess=new ArrayList<>(changedChessArr.length());
                JSONObject changedChessJson;
                for(int i=0,len=changedChessArr.length();i<len;i++){
                    changedChessJson=changedChessArr.getJSONObject(i);
                    int x=changedChessJson.optInt("x",-1);
                    int y=changedChessJson.optInt("y",-1);
                    changedChess.add(new ReversiMove(x,y));
                }
                record.changedChess=changedChess;
            }

            return record;
        } catch (JSONException e) {
            LogUtil.i("RecordParser",e.getMessage());
        }
        return null;
    }
}
