package com.ue.chess.entity;

import com.ue.chess.util.ChessUtil;

/**
 * Created by hawk on 2017/1/9.
 */
//只能向前直走（不能后退，这点和中国象棋类似），
// 每次行棋只能走一格。但是，走第一步时，可以走一格或两格。
// 兵的吃子方法与行棋方向不一样，它是直走斜吃，即如果兵的
// 前斜进一格内有对方棋子，就可以吃掉它，从而占据该格位置。
public class PawnGenerator extends BaseGenerator{
    @Override
    public void showValidMoves(boolean isMyMove,ChessPoint[][] qzs, ChessPoint fromPoint) {
        if(isMyMove){
            changeStatus(qzs,fromPoint,fromPoint.x,fromPoint.y-1);//前进一步
            changeStatus(qzs,fromPoint,fromPoint.x-1,fromPoint.y-1);//吃子
            changeStatus(qzs,fromPoint,fromPoint.x+1,fromPoint.y-1);//吃子
            if(fromPoint.isFirstMove()){
                changeStatus(qzs,fromPoint,fromPoint.x,fromPoint.y-2);//前进2步
            }
            return;
        }
        changeStatus(qzs,fromPoint,fromPoint.x,fromPoint.y+1);//前进一步
        changeStatus(qzs,fromPoint,fromPoint.x-1,fromPoint.y+1);//吃子
        changeStatus(qzs,fromPoint,fromPoint.x+1,fromPoint.y+1);//吃子
        if(fromPoint.isFirstMove()){
            changeStatus(qzs,fromPoint,fromPoint.x,fromPoint.y+2);//前进2步
        }
    }

    @Override
    protected boolean changeStatus(ChessPoint[][] qzs, ChessPoint fromPoint, int toX, int toY) {
        if(isOutOfRange(toX,toY)){
            return false;
        }
        if(fromPoint.x==toX){//前进
            if(null==qzs[toX][toY]){
                qzs[toX][toY]=new ChessPoint(fromPoint.f(),toX,toY,fromPoint.c,fromPoint.img());
            }
            return false;
        }
        //吃子
        if(null!=qzs[toX][toY]&&qzs[toX][toY].c!=fromPoint.c){
            qzs[toX][toY].s=ChessUtil.STATUS_CAN_EAT;
        }
        return false;
    }
}
