package com.ue.army_chess.entity;

import com.hyphenate.easeui.game.db.DataParser;
import com.ue.army_chess.util.ArmyChessUtil;
import com.ue.common.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hawk on 2017/1/6.
 */

public class FMRecordParser implements DataParser<FMArmyRecord> {
    @Override
    public FMArmyRecord parse(int id, String data) {
        try {
            JSONObject dataJson=new JSONObject(data);
            //from
            int f=dataJson.optInt("fromF",-1);
            int x=dataJson.optInt("fromX",-1);
            int y=dataJson.optInt("fromY",-1);
            int c=dataJson.optInt("fromC",-1);
            FMArmyChess from=new FMArmyChess(f,x,y,c, ArmyChessUtil.STAT_NORMAL);
            //to
            f=dataJson.optInt("toF",-1);
            x=dataJson.optInt("toX",-1);
            y=dataJson.optInt("toY",-1);
            c=dataJson.optInt("toC",-1);
            FMArmyChess to=new FMArmyChess(f,x,y,c,ArmyChessUtil.STAT_NORMAL);
            //return
            FMArmyRecord armyRecord=new FMArmyRecord(from,to);
            armyRecord.id=id;
            return armyRecord;
        } catch (JSONException e) {
            LogUtil.i("FMArmyRecord","parser,error="+e.getMessage());
        }
        return null;
    }
}
