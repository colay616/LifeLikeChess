package com.ue.cnchess.entity;

import com.hyphenate.easeui.game.db.DataParser;
import com.ue.common.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hawk on 2017/1/6.
 */
public class RecordParser implements DataParser<CNChessRecord> {
    @Override
    public CNChessRecord parse(int id, String data) {
        try {
            JSONObject dataJson=new JSONObject(data);
            CNChessRecord chessRecord = new CNChessRecord();
            chessRecord.id = id;
            chessRecord.fromF = dataJson.optInt("fromF", -1);
            chessRecord.fromX = dataJson.optInt("fromX", -1);
            chessRecord.fromY = dataJson.optInt("fromY", -1);
            chessRecord.fromC = dataJson.optInt("fromC", -1);
            chessRecord.toF = dataJson.optInt("toF", -1);
            chessRecord.toX = dataJson.optInt("toX", -1);
            chessRecord.toY = dataJson.optInt("toY", -1);
            chessRecord.toC = dataJson.optInt("toC", -1);
            return chessRecord;
        } catch (JSONException e) {
            LogUtil.i("CNChessRecord", "data parse error:" + e.getMessage());
        }
        return null;
    }
}