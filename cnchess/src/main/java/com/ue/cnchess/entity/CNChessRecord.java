package com.ue.cnchess.entity;

import com.ue.cnchess.util.CNChessUtil;

/**
 * 移动棋子的一步走法类
 */
public class CNChessRecord {
    public int id;
    public int fromF;
    public int fromX;
    public int fromY;
    public int fromC;
    public int toF;
    public int toX;
    public int toY;
    public int toC;

    public CNChessRecord(){}

    public CNChessRecord(CnChessPoint from, CnChessPoint to) {
        fromF = from.f();
        fromX = from.x;
        fromY = from.y;
        fromC = from.c;
        toX = to.x;
        toY = to.y;
        if (null == to || to.s == CNChessUtil.STATUS_CAN_GO) {//STATUS_CAN_GO表示该棋为虚棋，不要保存
            toF = -1;
            toC = -1;
        } else {
            toF = to.f();
            toC = to.c;
        }
    }

    @Override
    public String toString() {
        return String.format("{'fromF':%d,'fromX':%d,'fromY':%d,'fromC':%d,'toF':%d,'toX':%d,'toY':%d,'toC':%d}",
                fromF, fromX, fromY, fromC, toF, toX, toY, toC);
    }
}