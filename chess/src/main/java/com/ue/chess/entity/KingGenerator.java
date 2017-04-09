package com.ue.chess.entity;

import com.ue.chess.util.ChessUtil;

/**
 * Created by hawk on 2017/1/9.
 */
//王（K）：横、直、斜都可以走，但每次限走一步。
//和中国象棋相比，王是不可以送吃的，即任何被敌方控制的格子,己方王都不能走进去。否则，算“送王”犯规。累计三次犯规就要判负。(人家要送，为什么拦着人家，忽略这条规则)
public class KingGenerator extends BaseGenerator{
    private static final String TAG=KingGenerator.class.getSimpleName();
    @Override
    public void showValidMoves(boolean isMyMove,ChessPoint[][] qzs, ChessPoint fromPoint) {
//        以下情况，王车不能易位：
//        王和车已经移动过。
//        王和车之间有棋子阻隔；
//        王正在被将军；
//        王经过或到达的位置受其他棋子攻击；
//        王不可穿越被敌方攻击的格；
//        王和车不在同一横行。（此规则为国际棋联在1972年所添加，是为了制止之前有棋手所采用过的将与王在同一直行的兵行至对方底线后升变为车再使用王车易位的战术。）

        //王车易位
        if(fromPoint.isFirstMove()) {//王没有移动过
            boolean isLeftRookFirstMove=qzs[0][fromPoint.y] != null && qzs[0][fromPoint.y].f() == ChessUtil.ROOK && qzs[0][fromPoint.y].isFirstMove();
            boolean isRightRookFirstMove=qzs[7][fromPoint.y] != null && qzs[7][fromPoint.y].f() == ChessUtil.ROOK && qzs[7][fromPoint.y].isFirstMove();
            if(isLeftRookFirstMove||isRightRookFirstMove){//车没有移动过
                boolean isLeftBlocked=qzs[1][fromPoint.y]!=null||qzs[2][fromPoint.y]!=null||qzs[3][fromPoint.y]!=null;
                boolean isRightBlocked=qzs[5][fromPoint.y]!=null||qzs[6][fromPoint.y]!=null;
                if(!isLeftBlocked||!isRightBlocked){//王和车之间没有棋子阻隔
                    boolean[]isJiangedOrBlocked=isJiangedOrBlocked(qzs,fromPoint);
                    if(!isJiangedOrBlocked[0]){//王没有被将;王经过或到达的位置没有受其他棋子攻击
                        qzs[0][fromPoint.y].s=ChessUtil.STATUS_EXCHANGE;
                    }
                    if(!isJiangedOrBlocked[1]){//王没有被将;王经过或到达的位置没有受其他棋子攻击
                        qzs[7][fromPoint.y].s=ChessUtil.STATUS_EXCHANGE;
                    }
                }
            }
        }
        changeAroundStatus(qzs,fromPoint);
    }

    private void changeAroundStatus(ChessPoint[][] qzs, ChessPoint fromPoint){
        changeStatus(qzs,fromPoint,fromPoint.x,fromPoint.y-1);//上
        changeStatus(qzs,fromPoint,fromPoint.x,fromPoint.y+1);//下
        changeStatus(qzs,fromPoint,fromPoint.x-1,fromPoint.y);//左
        changeStatus(qzs,fromPoint,fromPoint.x+1,fromPoint.y);//右
        changeStatus(qzs,fromPoint,fromPoint.x-1,fromPoint.y-1);//左上
        changeStatus(qzs,fromPoint,fromPoint.x-1,fromPoint.y+1);//左下
        changeStatus(qzs,fromPoint,fromPoint.x+1,fromPoint.y-1);//右上
        changeStatus(qzs,fromPoint,fromPoint.x+1,fromPoint.y+1);//右下
    }

    //判断王是否正被将;王经过或到达的位置是否受其他棋子攻击
    private boolean[] isJiangedOrBlocked(ChessPoint[][] qzs, ChessPoint fromPoint){
        boolean isLeftBlocked=false;
        boolean isRightBlocked=false;
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                if(qzs[i][j]==null){
                    continue;
                }
                if(qzs[i][j].c==fromPoint.c){
                    continue;
                }
                if(qzs[i][j].f()==ChessUtil.KING){
                    changeAroundStatus(qzs,qzs[i][j]);
                }else{
                    qzs[i][j].showValidMoves(false,qzs);
                }
                boolean isKingJianged=qzs[fromPoint.x][fromPoint.y].s== ChessUtil.STATUS_CAN_EAT;
                if(isKingJianged){
                    resetStatus(qzs);
                    return new boolean[]{true,true};
                }
                if(!isLeftBlocked){
                    isLeftBlocked=qzs[1][fromPoint.y]!=null||qzs[2][fromPoint.y]!=null||qzs[3][fromPoint.y]!=null;
                }
                if(!isRightBlocked){
                    isRightBlocked=qzs[5][fromPoint.y]!=null||qzs[6][fromPoint.y]!=null;
                }
                if(isLeftBlocked&&isRightBlocked){
                    resetStatus(qzs);
                    return new boolean[]{true,true};
                }
                resetStatus(qzs);
            }
        }
        return new boolean[]{isLeftBlocked,isRightBlocked};
    }

    private void resetStatus(ChessPoint[][] qzs){
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                if(qzs[i][j]==null){
                    continue;
                }
                if(qzs[i][j].s==ChessUtil.STATUS_CAN_GO){
                    qzs[i][j]=null;
                    continue;
                }
                if(qzs[i][j].s==ChessUtil.STATUS_CAN_EAT){
                    qzs[i][j].s=ChessUtil.STATUS_NORMAL;
                }
            }
        }
    }
}
