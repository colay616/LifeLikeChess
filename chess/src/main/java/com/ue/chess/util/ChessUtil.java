package com.ue.chess.util;

/**
 * Reference:
 * Author:
 * Date:2016/9/11.
 */
public class ChessUtil {
    //这里PAWN_FIRST和PAWN要保持大于1的关系，因为在ChessView中是默认这样的
    public static final int O_PAWN_F =1;
    public static final int O_PAWN =2;
    public static final int M_PAWN_F =3;
    public static final int M_PAWN =4;
    public static final int BISHOP =5;
    public static final int KING =6;
    public static final int KNIGHT =7;
    public static final int QUEEN =8;
    public static final int ROOK =9;

    public static final int STATUS_CAN_GO =-1;//可以下的位置
    public static final int STATUS_NORMAL=0;//正常状态
    public static final int STATUS_CAN_EAT =1;//可以吃掉的位置
    public static final int STATUS_CHOSEN =2;//被选中的棋子

    public static final int BLACK_CHESS=1;//黑棋
    public static final int WHITE_CHESS=2;//白棋
}
