package com.ue.chess.entity;

import com.ue.chess.util.ChessUtil;

/**
 * Reference:
 * Author:
 * Date:2016/9/11.
 */
public class ChessRecord {
    public int id;
    public int fromF;
    public int fromX;
    public int fromY;
    public int fromC;
    public boolean fromFirstMove;
    public int toF;
    public int toX;
    public int toY;
    public int toC;
    public boolean toFirstMove;
    public boolean isExchange;

    public ChessRecord(){}

    public ChessRecord(ChessPoint from, ChessPoint to,boolean isExchange) {
        fromF = from.f();
        fromX = from.x;
        fromY = from.y;
        fromC = from.c;
        this.isExchange=isExchange;
        fromFirstMove =from.isFirstMove();
        if (null == to || to.s == ChessUtil.STATUS_CAN_GO) {
            toF = -1;
            toC = -1;
        } else {
            toF = to.f();
            toC = to.c;
        }
        toX = to.x;
        toY = to.y;
    }

    @Override
    public String toString() {
        return String.format("{'isExchange':%b,'fromF':%d,'fromX':%d,'fromY':%d,'fromC':%d,'fromFirstMove':%b,'toF':%d,'toX':%d,'toY':%d,'toC':%d,'toFirstMove':%b}",
                isExchange,fromF, fromX, fromY, fromC, fromFirstMove, toF, toX, toY, toC, toFirstMove);
    }
}
