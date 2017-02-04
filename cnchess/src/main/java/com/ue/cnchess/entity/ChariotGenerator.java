package com.ue.cnchess.entity;

/**
 * Created by hawk on 2017/1/7.
 */

public class ChariotGenerator extends BaseGenerator{
    @Override
    public void showValidMoves(CnChessPoint[][] qzs, CnChessPoint fromPoint) {
        for(int i = fromPoint.y - 1; i >= 0; i--){//前
            if(changeStatus(qzs,fromPoint,fromPoint.x,i)){
                break;
            }
        }
        for(int i = fromPoint.y + 1; i < 10; i++){//后
            if(changeStatus(qzs,fromPoint,fromPoint.x,i)){
                break;
            }
        }
        for(int i = fromPoint.x - 1; i >= 0; i--){//左
            if(changeStatus(qzs,fromPoint,i,fromPoint.y)){
                break;
            }
        }
        for(int i = fromPoint.x + 1; i < 9; i++){//右
            if(changeStatus(qzs,fromPoint,i,fromPoint.y)){
                break;
            }
        }
    }
}
