package com.ue.chess.entity;

/**
 * Created by hawk on 2017/1/9.
 */
//后（Q）：
//横、直、斜都可以走，步数不受限制，不能越子行棋。
// 该棋也是棋力最强的棋子。
public class QueenGenerator extends BaseGenerator{
    @Override
    public void showValidMoves(boolean isMyMove,ChessPoint[][] qzs, ChessPoint fromPoint) {
        //上
        for(int i=fromPoint.y-1;i>=0;i--){
            if(!changeStatus(qzs,fromPoint,fromPoint.x,i)){//changeStatus=false表示碰到墙了
                break;
            }
        }
        //下
        for(int i=fromPoint.y+1;i<8;i++){
            if(!changeStatus(qzs,fromPoint,fromPoint.x,i)){
                break;
            }
        }
        //左
        for(int i=fromPoint.x-1;i>=0;i--){
            if(!changeStatus(qzs,fromPoint,i,fromPoint.y)){
                break;
            }
        }
        //右
        for(int i=fromPoint.x+1;i<8;i++){
            if(!changeStatus(qzs,fromPoint,i,fromPoint.y)){
                break;
            }
        }
        //左上
        for(int i=fromPoint.x-1,j=fromPoint.y-1;i>=0&&j>=0;i--,j--){
            if(!changeStatus(qzs,fromPoint,i,j)){
                break;
            }
        }
        //左下
        for(int i=fromPoint.x-1,j=fromPoint.y+1;i>=0&&j<8;i--,j++){
            if(!changeStatus(qzs,fromPoint,i,j)){
                break;
            }
        }
        //右上
        for(int i=fromPoint.x+1,j=fromPoint.y-1;i<8&&j>=0;i++,j--){
            if(!changeStatus(qzs,fromPoint,i,j)){
                break;
            }
        }
        //右下
        for(int i=fromPoint.x+1,j=fromPoint.y+1;i<8&&j<8;i++,j++){
            if(!changeStatus(qzs,fromPoint,i,j)){
                break;
            }
        }
    }
}
