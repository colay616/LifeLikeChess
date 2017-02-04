package com.ue.chess.entity;

/**
 * Created by hawk on 2017/1/9.
 */
//王（K）：横、直、斜都可以走，但每次限走一步。不过，
// 和中国象棋相比，王是不可以送吃的，即任何被敌方控制的格子，
// 己方王都不能走进去。否则，算“送王”犯规。累计三次犯规就要判负。
public class KingGenerator extends BaseGenerator{
    @Override
    public void showValidMoves(ChessPoint[][] qzs, ChessPoint fromPoint) {
        changeStatus(qzs,fromPoint,fromPoint.x,fromPoint.y-1);//上
        changeStatus(qzs,fromPoint,fromPoint.x,fromPoint.y+1);//下
        changeStatus(qzs,fromPoint,fromPoint.x-1,fromPoint.y);//左
        changeStatus(qzs,fromPoint,fromPoint.x+1,fromPoint.y);//右
        changeStatus(qzs,fromPoint,fromPoint.x-1,fromPoint.y-1);//左上
        changeStatus(qzs,fromPoint,fromPoint.x-1,fromPoint.y+1);//左下
        changeStatus(qzs,fromPoint,fromPoint.x+1,fromPoint.y-1);//右上
        changeStatus(qzs,fromPoint,fromPoint.x+1,fromPoint.y+1);//右下
    }
}
