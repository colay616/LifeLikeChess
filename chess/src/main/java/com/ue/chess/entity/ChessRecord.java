package com.ue.chess.entity;

import com.hyphenate.easeui.utils.GsonHolder;
import com.ue.chess.util.ChessUtil;

/**
 * Reference:
 * Author:
 * Date:2016/9/11.
 */
public class ChessRecord {
    public int id;
    public ChessPoint fromPoint;
    public ChessPoint toPoint;
    public boolean isExchange;

    public ChessRecord(ChessPoint from, ChessPoint to,boolean isExchange) {
        this.fromPoint=from;
        if(to.s== ChessUtil.STATUS_CAN_GO){
            to.setF(-1);
        }
        this.toPoint=to;
        this.isExchange=isExchange;
    }

    @Override
    public String toString() {
        return GsonHolder.getGson().toJson(this);
    }
}