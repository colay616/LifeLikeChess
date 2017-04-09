package com.ue.chess.entity;

import com.hyphenate.easeui.game.db.DataParser;
import com.ue.common.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hawk on 2017/1/9.
 */

public class RecordParser implements DataParser<ChessRecord> {
    @Override
    public ChessRecord parse(int id, String data) {
        try {
            JSONObject dataJson = new JSONObject(data);
            ChessRecord chessRecord = new ChessRecord();
            chessRecord.id = id;
            chessRecord.fromF = dataJson.optInt("fromF", -1);
            chessRecord.fromX = dataJson.optInt("fromX", -1);
            chessRecord.fromY = dataJson.optInt("fromY", -1);
            chessRecord.fromC = dataJson.optInt("fromC", -1);
            chessRecord.fromFirstMove =dataJson.optBoolean("fromFirstMove",false);
            chessRecord.toF = dataJson.optInt("toF", -1);
            chessRecord.toX = dataJson.optInt("toX", -1);
            chessRecord.toY = dataJson.optInt("toY", -1);
            chessRecord.toC = dataJson.optInt("toC", -1);
            chessRecord.toFirstMove =dataJson.optBoolean("toFirstMove",false);
            chessRecord.isExchange=dataJson.optBoolean("isExchange",false);
            return chessRecord;
        } catch (JSONException e) {
            LogUtil.i("ChessRecord", "data parse error:" + e.getMessage());
        }
        return null;
    }
}
