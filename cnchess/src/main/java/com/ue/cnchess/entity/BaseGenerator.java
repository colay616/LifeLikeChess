package com.ue.cnchess.entity;

import com.ue.cnchess.util.CNChessUtil;

/**
 * Created by hawk on 2017/1/7.
 */

public abstract class BaseGenerator {
    public abstract void showValidMoves(CnChessPoint[][] qzs, CnChessPoint fromPoint);

    protected boolean isOutOfRange(CnChessPoint fromPoint, int xx, int yy){
        if(xx<0){
            return true;
        }
        if(xx>8){
            return true;
        }
        if(yy<0){
            return true;
        }
        if(yy>9){
            return true;
        }
        return false;
    }

    /**
     * @param qzs
     * @param fromPoint
     * @param toX
     * @param toY
     * @return true:遇到墙了
     */
    protected boolean changeStatus(CnChessPoint[][] qzs, CnChessPoint fromPoint, int toX, int toY){
        if(isOutOfRange(fromPoint,toX,toY)){
            return false;
        }
        if(null==qzs[toX][toY]){
            qzs[toX][toY]=new CnChessPoint(fromPoint.f(),toX,toY,fromPoint.c,fromPoint.txt());
            return false;
        }
        if(qzs[toX][toY].c!=fromPoint.c){
            qzs[toX][toY].s = CNChessUtil.STATUS_CAN_EAT;
        }
        return true;
    }
}
