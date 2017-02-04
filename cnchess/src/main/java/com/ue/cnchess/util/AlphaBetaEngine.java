package com.ue.cnchess.util;

import com.ue.cnchess.entity.CNChessRecord;
import com.ue.cnchess.entity.CnChessPoint;

/**
 * Alpha-Beta 搜索算法
 */
public class AlphaBetaEngine {
    private static final String TAG=AlphaBetaEngine.class.getSimpleName();
    CnChessPoint curPosition[][]=new CnChessPoint[9][10];// = new int[9][10];// 搜索时用于当前节点棋盘状态的数组
    CNChessRecord bestMove; //记录最佳走法的变量
    MoveGenerator mMoveGenerator; // 走法产生器
    Evaluation mEvaluation; //估值函数
    int searchDepth; //最大搜索深度
    int maxDepth;// 当前搜索的最大搜索深度
    int aiColor;

    public AlphaBetaEngine(int aiColor) {
        mEvaluation = new Evaluation();
        mMoveGenerator = new MoveGenerator();
        this.aiColor = aiColor;
    }

    public void searchAGoodMove(CnChessPoint position[][]) {
        curPosition=position;
        maxDepth = searchDepth; //当前最大深度
		/*
         *  调用算法，得到 BestMove
		 *  一个负 很大， 一个 正 很大
		 */
        alphaBeta(maxDepth, -20000, 20000);
    }

    /**
     * 黑方负最大 ， 红方正最大
     */
    public int alphaBeta(int depth, int alpha, int beta) {
        int i = isGameOver(curPosition, depth);
        if (i != 0)
            return i;

        if (depth <= 0)    //叶子节点取估值
            return mEvaluation.evaluate(curPosition, (maxDepth - depth) % 2, aiColor);

        int count = mMoveGenerator.createPossibleMove(curPosition, depth, (maxDepth - depth) % 2, aiColor);

        for (i = 0; i < count; i++) {
            doMove(mMoveGenerator.moveList[depth][i]);
            int score = -alphaBeta(depth - 1, -beta, -alpha); // 递归
            undoMove(mMoveGenerator.moveList[depth][i]);

            if (score > alpha) {
                alpha = score;
                if (depth == maxDepth)
                    bestMove = mMoveGenerator.moveList[depth][i];
            }
            if (alpha >= beta)
                break;
        }
        return alpha;
    }

    /**
     * 根据传入的走法,改变棋盘
     *
     * @param mov
     * @return
     */
    void doMove(CNChessRecord mov) {
        CnChessPoint fromPoint = curPosition[mov.fromX][mov.fromY];
        if (null == curPosition[mov.toX][mov.toY]) {
            curPosition[mov.toX][mov.toY] = new CnChessPoint(fromPoint.f(), mov.toX, mov.toY, fromPoint.c);
        } else {
            curPosition[mov.toX][mov.toY].copyFrom(fromPoint);
        }
        curPosition[mov.fromX][mov.fromY] = null;
    }

    /**
     * 根据传入的走法,恢复到上一个棋盘
     *
     * @param mov
     */
    void undoMove(CNChessRecord mov) {
        CnChessPoint toPoint = curPosition[mov.toX][mov.toY];
        if (null == curPosition[mov.fromX][mov.fromY]) {
            curPosition[mov.fromX][mov.fromY] = new CnChessPoint(toPoint.f(), mov.fromX, mov.fromY, toPoint.c);
        } else {
            curPosition[mov.fromX][mov.fromY].copyFrom(toPoint);
        }
        curPosition[mov.toX][mov.toY] = mov.toF == -1 ? null : new CnChessPoint(mov.toF, mov.toX, mov.toY, mov.toC);
    }

    /**
     * 判断游戏是否结束
     * 如未结束，返回0，否则返回极大/极小值
     *
     * @param position
     * @param nDepth
     * @return
     */
    public int isGameOver(CnChessPoint position[][], int nDepth) {
        int i, j;
        boolean oLive = false, iLive = false;

        for (i = 3; i < 6; i++)
            for (j = 0; j < 3; j++) {
                if (null != position[i][j] && position[i][j].f() == CNChessUtil.O_GENERAL) {
                    oLive = true;
                }
                if (null != position[i][j + 7] && position[i][j + 7].f() == CNChessUtil.M_GENERAL) {
                    iLive = true;
                }
            }

        i = (maxDepth - nDepth + 1) % 2;

        if (!oLive) {
            return i != 0 ? -19990 - nDepth : 19990 + nDepth;
        }
        if (!iLive) {
            return i != 0 ? 19990 + nDepth : -19990 - nDepth;
        }
        return 0;
    }

    public CNChessRecord getBestMove() {
        return bestMove;
    }

    public void setSearchDepth(int searchDepth) {
        this.searchDepth = searchDepth;
    }
}
