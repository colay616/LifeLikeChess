package com.ue.cnchess.util;

import com.ue.cnchess.entity.CNChessRecord;
import com.ue.cnchess.entity.CnChessPoint;

// 走发产生器
public class MoveGenerator {
    public CNChessRecord[][] moveList = new CNChessRecord[8][80];//存放每一层的所有走法
    public int moveCount;// 记录m_MoveList中走法的数量

    /**
     * @param position 当前棋子的二维数组
     * @param nPly     当前搜索的层数 ， 每层将走法存在不同的位置
     * @param nSide    指明产生哪一方的走法，1为红方，0为黑方
     * @return
     */

    public int createPossibleMove(CnChessPoint position[][], int nPly, int nSide,int aiColor) {
        CnChessPoint fromPoint;
        int i, j;
        moveCount = 0;

        for (i = 0; i < 9; i++) {
            for (j = 0; j < 10; j++) {
                if (null == position[i][j]) {
                    continue;
                }
                fromPoint = position[i][j];
                if (nSide == 0 && fromPoint.c!=aiColor)
                    continue;// 如果产生黑棋走法，跳过红棋
                if (nSide != 0 && fromPoint.c==aiColor)
                    continue;//如果产生红棋走法，跳过黑棋

                fromPoint.showValidMoves(position);
                addValidMoves(position,fromPoint,nPly);
            }
        }

        return moveCount; // 返回总的走法数目
    }

    private void addValidMoves(CnChessPoint[][]position,CnChessPoint fromPoint,int nPly){
        for(int i=0;i<9;i++){
            for(int j=0;j<10;j++){
                if(null==position[i][j]){
                    continue;
                }
                if(position[i][j].s==CNChessUtil.STATUS_CAN_GO){
                    moveList[nPly][moveCount] = new CNChessRecord(fromPoint,position[i][j]);
                    moveCount++;
                    position[i][j]=null;
                    continue;
                }
                if(position[i][j].s==CNChessUtil.STATUS_CAN_EAT){
                    moveList[nPly][moveCount] = new CNChessRecord(fromPoint,position[i][j]);
                    moveCount++;
                    position[i][j].s=CNChessUtil.STATUS_NORMAL;
                }
            }
        }
    }
}
