package com.ue.cnchess.entity;

/**
 * Created by hawk on 2017/1/7.
 */

public class GuardGenerator extends BaseGenerator{
    @Override
    public void showValidMoves(CnChessPoint[][] qzs, CnChessPoint fromPoint) {
        //4*4格子，士在中心时有最多走法-4种
        changeStatus(qzs,fromPoint,fromPoint.x - 1, fromPoint.y - 1);//左上
        changeStatus(qzs,fromPoint,fromPoint.x + 1, fromPoint.y - 1);//右上
        changeStatus(qzs,fromPoint,fromPoint.x - 1, fromPoint.y + 1);//左下
        changeStatus(qzs,fromPoint,fromPoint.x + 1, fromPoint.y + 1);//右下
    }

    @Override
    protected boolean isOutOfRange(CnChessPoint fromPoint, int xx, int yy) {
        if (xx < 3) {
            return true;
        }
        if (xx > 5) {
            return true;
        }
        if (yy < 0) {
            return true;
        }
        if (yy > 9) {
            return true;
        }
        if (yy > 2 && yy < 7) {
            return true;
        }
        return false;
    }
}
