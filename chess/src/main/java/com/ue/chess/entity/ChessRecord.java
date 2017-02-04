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
    public int toF;
    public int toX;
    public int toY;
    public int toC;

    public ChessRecord(){}

    public ChessRecord(ChessPoint from, ChessPoint to) {
        fromF = from.f();
        fromX = from.x;
        fromY = from.y;
        fromC = from.c;
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
        return String.format("{'fromF':%d,'fromX':%d,'fromY':%d,'fromC':%d,'toF':%d,'toX':%d,'toY':%d,'toC':%d}",
                fromF, fromX, fromY, fromC, toF, toX, toY, toC);
    }
}
