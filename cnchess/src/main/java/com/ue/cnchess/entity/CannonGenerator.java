package com.ue.cnchess.entity;

import com.ue.cnchess.util.CNChessUtil;

/**
 * Created by hawk on 2017/1/7.
 */

public class CannonGenerator extends BaseGenerator {
    private int weight = 0;//0:无炮台,>0有炮台

    @Override
    public void showValidMoves(CnChessPoint[][] qzs, CnChessPoint fromPoint) {
        weight = 0;
        for (int i = fromPoint.y - 1; i >= 0; i--) {//前
            if (changeStatus(qzs, fromPoint, fromPoint.x, i)){
                break;
            }
        }
        weight = 0;
        for (int i = fromPoint.y + 1; i < 10; i++) {//后
            if (changeStatus(qzs, fromPoint, fromPoint.x, i)) {
                break;
            }
        }
        weight = 0;
        for (int i = fromPoint.x - 1; i >= 0; i--) {//左
            if (changeStatus(qzs, fromPoint, i, fromPoint.y)) {
                break;
            }
        }
        weight = 0;
        for (int i = fromPoint.x + 1; i < 9; i++) {//右
            if (changeStatus(qzs, fromPoint, i, fromPoint.y)) {
                break;
            }
        }
    }

    //true:碰到墙了,不要再往这个方向走了
    @Override
    protected boolean changeStatus(CnChessPoint[][] qzs, CnChessPoint fromPoint, int toX, int toY) {
        if (isOutOfRange(fromPoint, toX, toY)) {
            return true;
        }
        if (null == qzs[toX][toY]) {
            if (weight == 0) {
                qzs[toX][toY] = new CnChessPoint(fromPoint.f(), toX, toY, fromPoint.c,fromPoint.txt());
            }
            return false;
        }
        if (weight == 0) {//炮台
            weight++;
            return false;
        }
        if (fromPoint.c != qzs[toX][toY].c && weight == 1) {//靶子
            qzs[toX][toY].s = CNChessUtil.STATUS_CAN_EAT;
        }
        return true;//碰到墙了
    }
}
