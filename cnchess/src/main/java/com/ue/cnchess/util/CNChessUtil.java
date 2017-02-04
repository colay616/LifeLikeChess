package com.ue.cnchess.util;

/**
 * Reference:
 * Author:
 * Date:2016/9/11.
 */
public class CNChessUtil {
    public static final int F_NULL=-1;
    public static final int CANNON =0;
    public static final int CHARIOT =1;
    public static final int ELEPHANT =2;
    public static final int O_GENERAL =3;//oppo
    public static final int M_GENERAL =4;//my
    public static final int GUARD =5;
    public static final int HORSE =6;
    public static final int O_SOLDIER =7;//oppo
    public static final int M_SOLDIER =8;//my

    public static final int STATUS_CAN_GO = -1;//可以下的位置
    public static final int STATUS_NORMAL = 0;//正常状态
    public static final int STATUS_CAN_EAT = 1;//可以吃掉的位置
    public static final int STATUS_CHOSEN = 2;//被选中的棋子
    public static final int RED = 0;//红棋
    public static final int BLACK = 1;//黑棋

//    public static CnChessPoint getPoint(int f, int x, int y, int c) {
//        switch (f) {
//            case M_GENERAL:
//            case O_GENERAL:
//                return new GeneralPoint(f, x, y, c);
//            case CHARIOT:
//                return new ChariotPoint(f, x, y, c);
//            case HORSE:
//                return new HorsePoint(f, x, y, c);
//            case CANNON:
//                return new CannonPoint(f, x, y, c);
//            case GUARD:
//                return new AdvisorPoint(f, x, y, c);
//            case ELEPHANT:
//                return new ElephantPoint(f, x, y, c);
//            case O_SOLDIER:
//            case M_SOLDIER:
//                return new SoldierPoint(f, x, y, c);
//        }
//        return null;
//    }

    public static boolean isBlack(int chessId) {
        if (chessId >= 1 && chessId <= 7) {
            return true;
        }
        return false;
    }

    public static boolean isRed(int chessId) {
        if (chessId >= 8 && chessId <= 14) {
            return true;
        }
        return false;
    }

    public static boolean isSameSide(int chessId1, int chessId2) {
        if ((isRed(chessId1) && isRed(chessId2)) || (isBlack(chessId1) && isBlack(chessId2))) {
            return true;
        }
        return false;
    }
}
