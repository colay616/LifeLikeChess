package com.ue.chess.entity;

/**
 * Created by hawk on 2017/1/9.
 */
//车（R）：
//横、竖均可以走，步数不受限制，不能斜走（和中国象棋类似）。
// 除王车易位外不能越子。
public class RookGenerator extends BaseGenerator{
    @Override
    public void showValidMoves(boolean isMyMove,ChessPoint[][] qzs, ChessPoint fromPoint) {
        boolean isFEnabled=true,isBEnabled=true,isLEnabled=true,isREnabled=true;
        for(int i=1;i<8;i++){
            if(isFEnabled){//前
                isFEnabled=changeStatus(qzs,fromPoint,fromPoint.x,fromPoint.y-i);
            }
            if(isBEnabled){//后
                isBEnabled=changeStatus(qzs,fromPoint,fromPoint.x,fromPoint.y+i);
            }
            if(isLEnabled){//左
                isLEnabled=changeStatus(qzs,fromPoint,fromPoint.x-i,fromPoint.y);
            }
            if(isREnabled){//右
                isREnabled=changeStatus(qzs,fromPoint,fromPoint.x+i,fromPoint.y);
            }
        }
    }
}
