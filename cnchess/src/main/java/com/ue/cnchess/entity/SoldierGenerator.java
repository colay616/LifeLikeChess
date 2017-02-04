package com.ue.cnchess.entity;

import com.ue.cnchess.util.CNChessUtil;

/**
 * Created by hawk on 2017/1/7.
 */

public class SoldierGenerator extends BaseGenerator{
    @Override
    public void showValidMoves(CnChessPoint[][] qzs, CnChessPoint fromPoint) {
        //只能走一步:前/左/右
        //我方
        if (fromPoint.f() == CNChessUtil.M_SOLDIER) {
            if(fromPoint.y>4){//过河前
                changeStatus(qzs,fromPoint,fromPoint.x,fromPoint.y-1);//前行
            }else{//过河后
                changeStatus(qzs,fromPoint,fromPoint.x,fromPoint.y-1);//前行
                changeStatus(qzs,fromPoint,fromPoint.x-1,fromPoint.y);//左行
                changeStatus(qzs,fromPoint,fromPoint.x+1,fromPoint.y);//右行
            }
            return;
        }
        //对方
        if(fromPoint.y<5){//过河前
            changeStatus(qzs,fromPoint,fromPoint.x,fromPoint.y+1);//前行
        }else{//过河后
            changeStatus(qzs,fromPoint,fromPoint.x,fromPoint.y+1);//前行
            changeStatus(qzs,fromPoint,fromPoint.x-1,fromPoint.y);//左行
            changeStatus(qzs,fromPoint,fromPoint.x+1,fromPoint.y);//右行
        }
        return;
    }
}
