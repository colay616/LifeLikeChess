package com.ue.cnchess.util;

import android.graphics.Point;

import com.ue.cnchess.entity.CnChessPoint;

public class Evaluation {
    // 定义每种棋子的基本价值
    final static int VAL_SOLDIER = 100;
    final static int VAL_GUARD = 250;
    final static int VAL_ELEPHANT = 250;
    final static int VAL_CHARIOT = 500;
    final static int VAL_HORSE = 350;
    final static int VAL_CANON = 350;
    final static int VAL_GENERAL = 10000;

    // 定义各棋子的灵活性 ,每多一个可走位置应加上的分值
    final static int FLEX_SOLDIER = 15;
    final static int FLE_GUARD = 1;
    final static int FLEX_ELEPHANT = 1;
    final static int FLEX_CHARIOT = 6;
    final static int FLEX_HORSE = 12;
    final static int FLEX_CANON = 6;
    final static int FLEX_GENERAL = 0;

    //红卒的附加值矩阵
    final int oppoSoldierValues[][] = new int[][]
            {
                    {0, 90, 90, 70, 70, 0, 0, 0, 0, 0},
                    {0, 90, 90, 90, 70, 0, 0, 0, 0, 0},
                    {0, 110, 110, 110, 70, 0, 0, 0, 0, 0},
                    {0, 120, 120, 110, 70, 0, 0, 0, 0, 0},
                    {0, 120, 120, 110, 70, 0, 0, 0, 0, 0},
                    {0, 120, 120, 110, 70, 0, 0, 0, 0, 0},
                    {0, 110, 110, 110, 70, 0, 0, 0, 0, 0},
                    {0, 90, 90, 90, 70, 0, 0, 0, 0, 0},
                    {0, 90, 90, 70, 70, 0, 0, 0, 0, 0}
            };

    //黑兵的附加值矩阵
    final int[][] mySoldierValues = new int[][]
            {
                    {0, 0, 0, 0, 0, 70, 70, 90, 90, 0},
                    {0, 0, 0, 0, 0, 70, 90, 90, 90, 0},
                    {0, 0, 0, 0, 0, 70, 110, 110, 110, 0},
                    {0, 0, 0, 0, 0, 70, 110, 120, 120, 0},
                    {0, 0, 0, 0, 0, 70, 110, 120, 120, 0},
                    {0, 0, 0, 0, 0, 70, 110, 120, 120, 0},
                    {0, 0, 0, 0, 0, 70, 110, 110, 110, 0},
                    {0, 0, 0, 0, 0, 70, 90, 90, 90, 0},
                    {0, 0, 0, 0, 0, 70, 70, 90, 90, 0}
            };

    /**
     * 为每一个兵返回附加值
     *
     * @param x
     * @param y
     * @param CurSituation
     * @return
     */
    int getSoldierValue(int x, int y, CnChessPoint CurSituation[][]) {
        if (CurSituation[x][y].f() == CNChessUtil.O_SOLDIER)
            return oppoSoldierValues[x][y];

        if (CurSituation[x][y].f() == CNChessUtil.M_SOLDIER)
            return mySoldierValues[x][y];
        return 0;
    }

    int m_BaseValue[] = new int[9];//存放棋子基本价值的数组
    int m_FlexValue[] = new int[9];//存放棋子灵活性分数的数组
    int m_AttackPos[][] = new int[9][10];//存放每一位置被威胁的信息
    int m_GuardPos[][] = new int[9][10];//存放每一位置被保护的信息
    int m_FlexibilityPos[][] = new int[9][10];//存放每一位置上的棋子的灵活性分数
    int m_chessValue[][] = new int[9][10];//存放每一位置上的棋子的总价值
    int nPosCount = 0;//记录一棋子的相关位置个数
    Point relatedPos[] = new Point[20];//记录一棋子相关位子的数组

    public Evaluation() {
        m_BaseValue[CNChessUtil.CANNON] = VAL_CANON;
        m_BaseValue[CNChessUtil.CHARIOT] = VAL_CHARIOT;
        m_BaseValue[CNChessUtil.ELEPHANT] = VAL_ELEPHANT;
        m_BaseValue[CNChessUtil.O_GENERAL] = VAL_GENERAL;
        m_BaseValue[CNChessUtil.M_GENERAL] = VAL_GENERAL;
        m_BaseValue[CNChessUtil.GUARD] = VAL_GUARD;
        m_BaseValue[CNChessUtil.HORSE] = VAL_HORSE;
        m_BaseValue[CNChessUtil.O_SOLDIER] = VAL_SOLDIER;
        m_BaseValue[CNChessUtil.M_SOLDIER] = VAL_SOLDIER;

        m_FlexValue[CNChessUtil.CANNON] = FLEX_CANON;
        m_FlexValue[CNChessUtil.CHARIOT] = FLEX_CHARIOT;
        m_FlexValue[CNChessUtil.ELEPHANT] = FLEX_ELEPHANT;
        m_FlexValue[CNChessUtil.O_GENERAL] = FLEX_GENERAL;
        m_FlexValue[CNChessUtil.M_GENERAL] = FLEX_GENERAL;
        m_FlexValue[CNChessUtil.GUARD] = FLE_GUARD;
        m_FlexValue[CNChessUtil.HORSE] = FLEX_HORSE;
        m_FlexValue[CNChessUtil.O_SOLDIER] = FLEX_SOLDIER;
        m_FlexValue[CNChessUtil.M_SOLDIER] = FLEX_SOLDIER;
    }

    public int count = 0;//全局变量

    /**
     * @param position
     * @param turnFlag 轮到谁的标志，0-ai
     * @return
     */
    public int evaluate(CnChessPoint position[][], int turnFlag, int aiColor) {
        int i, j, k;
        CnChessPoint nChessType, nTargetType;
        count++;
        boolean isAITurn = turnFlag == 0;

        for (i = 0; i < 9; i++) { // 初始化数值
            for (j = 0; j < 10; j++) {
                m_chessValue[i][j] = 0;
                m_AttackPos[i][j] = 0;
                m_GuardPos[i][j] = 0;
                m_FlexibilityPos[i][j] = 0;
            }
        }

        for (i = 0; i < 9; i++) {
            for (j = 0; j < 10; j++) {
                if (null == position[i][j]) {
                    continue;
                }
                nChessType = position[i][j];
                getRelatedPiece(position, i, j);
                for (k = 0; k < nPosCount; k++) {
                    nTargetType = position[relatedPos[k].x][relatedPos[k].y];
                    if (null == nTargetType) {
                        m_FlexibilityPos[i][j]++;
                        continue;
                    }
                    if (nChessType.c == nTargetType.c) {
                        m_GuardPos[relatedPos[k].x][relatedPos[k].y]++;
                        continue;
                    }
                    m_AttackPos[relatedPos[k].x][relatedPos[k].y]++;
                    m_FlexibilityPos[i][j]++;
                    switch (nTargetType.f()) {
                        case CNChessUtil.M_GENERAL:
                            if (isAITurn)
                                return 18888;
                            break;
                        case CNChessUtil.O_GENERAL:
                            if (!isAITurn)
                                return 18888;
                            break;
                        default:
                            m_AttackPos[relatedPos[k].x][relatedPos[k].y] += (30 + (m_BaseValue[nTargetType.f()] - m_BaseValue[nChessType.f()]) / 10) / 10;
                            break;
                    }
                }
            }
        }

        for (i = 0; i < 9; i++) {
            for (j = 0; j < 10; j++) {
                if (null == position[i][j]) {
                    continue;
                }
                nChessType = position[i][j];
                m_chessValue[i][j]++;
                m_chessValue[i][j] += m_FlexValue[nChessType.f()] * m_FlexibilityPos[i][j];
                m_chessValue[i][j] += getSoldierValue(i, j, position);
            }
        }
        int nHalfValue;
        for (i = 0; i < 9; i++) {
            for (j = 0; j < 10; j++) {
                if (null == position[i][j]) {
                    continue;
                }
                nChessType = position[i][j];
                nHalfValue = m_BaseValue[nChessType.f()] / 16;
                m_chessValue[i][j] += m_BaseValue[nChessType.f()];

                if (nChessType.c != aiColor) {
                    if (m_AttackPos[i][j] != 0) {
                        if (!isAITurn) {
                            if (nChessType.f() == CNChessUtil.M_GENERAL) {
                                m_chessValue[i][j] -= 20;
                            } else {
                                m_chessValue[i][j] -= nHalfValue * 2;
                                if (m_GuardPos[i][j] != 0)
                                    m_chessValue[i][j] += nHalfValue;
                            }
                        } else {
                            if (nChessType.f() == CNChessUtil.M_GENERAL)
                                return 18888;
                            m_chessValue[i][j] -= nHalfValue * 10;
                            if (m_GuardPos[i][j] != 0)
                                m_chessValue[i][j] += nHalfValue * 9;
                        }
                        m_chessValue[i][j] -= m_AttackPos[i][j];
                    } else {
                        if (m_GuardPos[i][j] != 0)
                            m_chessValue[i][j] += 5;
                    }
                } else {
                    if (m_AttackPos[i][j] != 0) {
                        if (isAITurn) {
                            if (nChessType.f() == CNChessUtil.O_GENERAL) {
                                m_chessValue[i][j] -= 20;
                            } else {
                                m_chessValue[i][j] -= nHalfValue * 2;
                                if (m_GuardPos[i][j] != 0)
                                    m_chessValue[i][j] += nHalfValue;
                            }
                        } else {
                            if (nChessType.f() == CNChessUtil.O_GENERAL)
                                return 18888;
                            m_chessValue[i][j] -= nHalfValue * 10;
                            if (m_GuardPos[i][j] != 0)
                                m_chessValue[i][j] += nHalfValue * 9;
                        }
                        m_chessValue[i][j] -= m_AttackPos[i][j];
                    } else {
                        if (m_GuardPos[i][j] != 0)
                            m_chessValue[i][j] += 5;
                    }
                }
            }
        }

        int playerValue = 0;
        int aiValue = 0;

        for (i = 0; i < 9; i++) {
            for (j = 0; j < 10; j++) {
                if (null == position[i][j]) {
                    continue;
                }
                if (position[i][j].c != aiColor) {//!!!!!
                    playerValue += m_chessValue[i][j];
                } else {
                    aiValue += m_chessValue[i][j];
                }
            }
        }
        return isAITurn ? aiValue - playerValue : playerValue - aiValue;
    }

    /**
     * 枚举给定位置上的所有相关位置
     *
     * @param position
     * @param j
     * @param i
     * @return
     */
    int getRelatedPiece(CnChessPoint position[][], int i, int j) {
        nPosCount = 0;

        position[i][j].showValidMoves(position);
        for (int a = 0; a < 9; a++) {
            for (int b = 0; b < 10; b++) {
                if (null == position[a][b]) {
                    continue;
                }
                if (position[a][b].s == CNChessUtil.STATUS_CAN_GO) {
                    relatedPos[nPosCount] = new Point(a, b);
                    nPosCount++;
                    position[a][b] = null;
                    continue;
                }
                if (position[a][b].s == CNChessUtil.STATUS_CAN_EAT) {
                    relatedPos[nPosCount] = new Point(a, b);
                    nPosCount++;
                    position[a][b].s = CNChessUtil.STATUS_NORMAL;
                }
            }
        }
        return nPosCount;
    }
}

