package com.ue.chess.entity;

import com.ue.chess.util.ChessUtil;

/**
 * Created by hawk on 2017/1/9.
 */

public abstract class BaseGenerator {
    /**
     * @param isMyMove 我方行棋，此参数只为兵行棋所用到
     * @param qzs
     * @param fromPoint
     */
    public abstract void showValidMoves(boolean isMyMove,ChessPoint[][] qzs, ChessPoint fromPoint);

    protected boolean isOutOfRange(int xx, int yy) {
        if (xx < 0) {
            return true;
        }
        if (xx > 7) {
            return true;
        }
        if (yy < 0) {
            return true;
        }
        if (yy > 7) {
            return true;
        }
        return false;
    }

    /**
     * @param qzs
     * @param fromPoint
     * @param toX
     * @param toY
     * @return false:碰到墙了
     */
    protected boolean changeStatus(ChessPoint[][] qzs, ChessPoint fromPoint, int toX, int toY){
        if(isOutOfRange(toX,toY)){
            return false;
        }
        if(null==qzs[toX][toY]){
            qzs[toX][toY]=new ChessPoint(fromPoint.f(),toX,toY,fromPoint.c,fromPoint.img());
            return true;
        }
        if(qzs[toX][toY].c!=fromPoint.c){
            qzs[toX][toY].s = ChessUtil.STATUS_CAN_EAT;
        }
        return false;
    }
}
