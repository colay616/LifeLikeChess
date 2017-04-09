package com.ue.chess.entity;

/**
 * Created by hawk on 2017/1/9.
 */
//马（N）：
//每行一步棋，先横走或直走一格，然后再往外斜走一格；或者先斜走一格，
// 最后再往外横走或竖走一格（即走 日 字，这点也和中国象棋类似）。
// 可以越子行走，而没有 中国象棋 的 蹩马腿 的限制。
public class KnightGenerator extends BaseGenerator{
    @Override
    public void showValidMoves(boolean isMyMove,ChessPoint[][] qzs, ChessPoint fromPoint) {
        changeStatus(qzs,fromPoint,fromPoint.x-1,fromPoint.y-2);
        changeStatus(qzs,fromPoint,fromPoint.x-1,fromPoint.y+2);
        changeStatus(qzs,fromPoint,fromPoint.x-2,fromPoint.y+1);
        changeStatus(qzs,fromPoint,fromPoint.x-2,fromPoint.y-1);
        changeStatus(qzs,fromPoint,fromPoint.x+1,fromPoint.y-2);
        changeStatus(qzs,fromPoint,fromPoint.x+1,fromPoint.y+2);
        changeStatus(qzs,fromPoint,fromPoint.x+2,fromPoint.y-1);
        changeStatus(qzs,fromPoint,fromPoint.x+2,fromPoint.y+1);
    }
}
