package com.ue.cnchess.entity;

/**
 * Created by hawk on 2017/1/7.
 */

public class HorseGenerator extends BaseGenerator {
    @Override
    public void showValidMoves(CnChessPoint[][] qzs, CnChessPoint fromPoint) {
        //8个位置
        //前
        if (!isBlock(qzs, fromPoint, fromPoint.x, fromPoint.y - 1)) {
            changeStatus(qzs, fromPoint, fromPoint.x - 1, fromPoint.y - 2);
            changeStatus(qzs, fromPoint, fromPoint.x + 1, fromPoint.y - 2);
        }
        //后
        if (!isBlock(qzs, fromPoint, fromPoint.x, fromPoint.y + 1)) {
            changeStatus(qzs, fromPoint, fromPoint.x - 1, fromPoint.y + 2);
            changeStatus(qzs, fromPoint, fromPoint.x + 1, fromPoint.y + 2);
        }
        //左
        if (!isBlock(qzs, fromPoint, fromPoint.x - 1, fromPoint.y)) {
            changeStatus(qzs, fromPoint, fromPoint.x - 2, fromPoint.y + 1);
            changeStatus(qzs, fromPoint, fromPoint.x - 2, fromPoint.y - 1);
        }
        //右
        if (!isBlock(qzs, fromPoint, fromPoint.x + 1, fromPoint.y)) {
            changeStatus(qzs, fromPoint, fromPoint.x + 2, fromPoint.y - 1);
            changeStatus(qzs, fromPoint, fromPoint.x + 2, fromPoint.y + 1);
        }
    }

    private boolean isBlock(CnChessPoint[][] qzs, CnChessPoint fromPoint, int x, int y) {
        if (isOutOfRange(fromPoint, x, y)) {
            return true;
        }
        if (null != qzs[x][y]) {
            return true;
        }
        return false;
    }
}
