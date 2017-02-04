package com.ue.cnchess.entity;

import com.ue.cnchess.util.CNChessUtil;

/**
 * Created by hawk on 2017/1/7.
 */

public class ElephantGenerator extends BaseGenerator{
    @Override
    public void showValidMoves(CnChessPoint[][] qzs, CnChessPoint fromPoint) {
        changeStatus(qzs,fromPoint,fromPoint.x-2,fromPoint.y-2);//左上
        changeStatus(qzs,fromPoint,fromPoint.x+2,fromPoint.y-2);//右上
        changeStatus(qzs,fromPoint,fromPoint.x-2,fromPoint.y+2);//左下
        changeStatus(qzs,fromPoint,fromPoint.x+2,fromPoint.y+2);//右下
    }

    @Override
    protected boolean isOutOfRange(CnChessPoint fromPoint, int xx, int yy) {
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
        if(fromPoint.y<5&&yy>4){//象不能过河
            return true;
        }
        if(fromPoint.y>4&&yy<5){//象不能过河
            return true;
        }
        return false;
    }

    @Override
    protected boolean changeStatus(CnChessPoint[][] qzs, CnChessPoint fromPoint, int toX, int toY) {
        if(isOutOfRange(fromPoint,toX,toY)){
            return false;
        }
        //塞象眼
        if(null!=qzs[(fromPoint.x+toX)/2][(fromPoint.y+toY)/2]){
            return false;
        }
        if(null==qzs[toX][toY]){
            qzs[toX][toY]=new CnChessPoint(fromPoint.f(),toX,toY,fromPoint.c,fromPoint.txt());
            return false;
        }
        if(qzs[toX][toY].c!=fromPoint.c){
            qzs[toX][toY].s = CNChessUtil.STATUS_CAN_EAT;
        }
        return false;
    }
}
