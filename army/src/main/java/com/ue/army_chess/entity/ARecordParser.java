package com.ue.army_chess.entity;

import com.hyphenate.easeui.game.db.DataParser;
import com.ue.common.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hawk on 2017/1/6.
 */

public class ARecordParser implements DataParser<AArmyRecord>{
    @Override
    public AArmyRecord parse(int id, String data) {
        try {
            JSONObject dataJson=new JSONObject(data);
            //from
            int f=dataJson.optInt("fromF",-1);
            int x=dataJson.optInt("fromX",-1);
            int y=dataJson.optInt("fromY",-1);
            int c=dataJson.optInt("fromC",-1);
            int s=dataJson.optInt("fromS",-1);
            int oldS=dataJson.optInt("fromOldS",-1);
            String signedTxt=dataJson.optString("fromSignedTxt","");
            AArmyChess from=new AArmyChess(f,x,y,c,s);
            from.oldS=oldS;
            from.setSign(signedTxt);
            //to
            f=dataJson.optInt("toF",-1);
            x=dataJson.optInt("toX",-1);
            y=dataJson.optInt("toY",-1);
            c=dataJson.optInt("toC",-1);
            s=dataJson.optInt("toS",-1);
            oldS=dataJson.optInt("toOldS",-1);
            signedTxt=dataJson.optString("toSignedTxt","");
            AArmyChess to=new AArmyChess(f,x,y,c,s);
            to.oldS=oldS;
            to.setSign(signedTxt);
            //return
            AArmyRecord armyRecord=new AArmyRecord(from,to);
            armyRecord.id=id;
            return armyRecord;
        } catch (JSONException e) {
            LogUtil.i("AArmyRecord","parser,error="+e.getMessage());
        }
        return null;
    }
}
