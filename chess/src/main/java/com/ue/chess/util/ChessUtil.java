package com.ue.chess.util;

/**
 * Reference:
 * Author:
 * Date:2016/9/11.
 */
public class ChessUtil {
    public static final int PAWN =4;
    public static final int BISHOP =5;
    public static final int KING =6;
    public static final int KNIGHT =7;
    public static final int QUEEN =8;
    public static final int ROOK =9;

    public static final int STATUS_CAN_GO =-1;//可以下的位置
    public static final int STATUS_NORMAL=0;//正常状态
    public static final int STATUS_CAN_EAT =1;//可以吃掉的位置
    public static final int STATUS_CHOSEN =2;//被选中的棋子
    public static final int STATUS_EXCHANGE =3;//易位

    public static final int FLAG_EXCHANGE=111;

    public static final int BLACK_CHESS=1;//黑棋
    public static final int WHITE_CHESS=2;//白棋
}
