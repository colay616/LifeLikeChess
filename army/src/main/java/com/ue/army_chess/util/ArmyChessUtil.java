
package com.ue.army_chess.util;

import com.hyphenate.easeui.game.iterface.OnPlayListener;
import com.ue.army_chess.entity.AArmyChess;
import com.ue.army_chess.entity.FMArmyChess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by hawk on 2016/12/21.
 */

public class ArmyChessUtil {
    public static final int STAT_UNKNOWN = 0;
    public static final int STAT_NORMAL = 1;
    public static final int STAT_CAN_GO = 2;
    public static final int STAT_CAN_EAT = 3;
    public static final int STAT_EQUAL = 4;
    public static final int FLAG_NOT_OVER = 5;
    public static final int STAT_I_DIE = 6;//我方棋子死亡
    public static final int STAT_NOT_SIGN = 7;//没标记
    public static final int STAT_TO_SIGN = 8;//待标记,再次点击可进行标记
    public static final int STAT_SIGNED = 9;//已标记

    private static final String[] chessTxtArray = new String[]{
            "工 兵", "排 长", "连 长", "营 长", "团 长", "旅 长",
            "师 长", "军 长", "司 令", "地 雷", "炸 弹", "军 旗"};

    //数量：3      3   3   2    2     2    2   1    1     3     2  1
    //类型：工兵 排长 连长 营长 团长 旅长 师长 军长 司令 地雷 炸弹 军旗
    //标识： 0    1   2     3    4    5   6      7    8   9    10  11
    public static String getChessTxt(int chessFlag) {
        if (chessFlag < 0 || chessFlag > 11) {
            return "";
        }
        return chessTxtArray[chessFlag];
    }

    public static boolean canGo(int stat) {
        return stat == STAT_CAN_GO || stat == STAT_CAN_EAT || stat == STAT_EQUAL||stat==ArmyChessUtil.STAT_I_DIE;
    }

    public static int checkGameOver(FMArmyChess[][] chesses, int myColor) {
        int myChessSize = 0, oppoChessSize = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 12; j++) {
                if (null == chesses[i][j]) {
                    continue;
                }
                if (chesses[i][j].getF() == 9 || chesses[i][j].getF() == 11) {
                    continue;
                }
                if (chesses[i][j].c == myColor) {
                    myChessSize++;
                } else {
                    oppoChessSize++;
                }
                if (myChessSize >= 4 && oppoChessSize >= 4) {
                    return FLAG_NOT_OVER;
                }
            }
        }
        if (oppoChessSize == 0) {
            if(myChessSize==0){
                return OnPlayListener.FLAG_DRAW;
            }
            return OnPlayListener.FLAG_I_WIN;
        }
        if (myChessSize == 0) {
            if(oppoChessSize==0){
                return OnPlayListener.FLAG_DRAW;
            }
            return OnPlayListener.FLAG_OPPO_WIN;
        }
        return FLAG_NOT_OVER;
    }

    public static int checkGameOver(AArmyChess[][] chesses, int myColor) {
        int myChessSize = 0, oppoChessSize = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 12; j++) {
                if (null == chesses[i][j]) {
                    continue;
                }
                if (chesses[i][j].getF() == 9 || chesses[i][j].getF() == 11) {
                    continue;
                }
                if (chesses[i][j].c == myColor) {
                    myChessSize++;
                } else {
                    oppoChessSize++;
                }
                if (myChessSize >= 4 && oppoChessSize >= 4) {
                    return FLAG_NOT_OVER;
                }
            }
        }
        if (oppoChessSize == 0) {
            if(myChessSize==0){
                return OnPlayListener.FLAG_DRAW;
            }
            return OnPlayListener.FLAG_I_WIN;
        }
        if (myChessSize == 0) {
            if(oppoChessSize==0){
                return OnPlayListener.FLAG_DRAW;
            }
            return OnPlayListener.FLAG_OPPO_WIN;
        }
        return FLAG_NOT_OVER;
    }

    //生成己方25棋子排列
    public static List<Integer> genHalfChessList() {
        int flagPos, minePos1 = 0, minePos2 = 0, minePos3 = 0, boomPos1 = 0, boomPos2 = 0;
        Random random = new Random();
        //插入1军旗,军旗只能放在大本营
        flagPos = random.nextInt(2) % 2 == 0 ? 21 : 23;
        //插入3地雷，地雷只能放在最后两行，
        int[] minePosOptions = flagPos == 21 ? new int[]{15, 16, 17, 18, 19, 20, 22, 23, 24} : new int[]{15, 16, 17, 18, 19, 20, 21, 22, 24};
        while ((minePos1 == minePos2 || minePos1 == minePos3 || minePos2 == minePos3)) {
            minePos1 = random.nextInt(9);
            minePos2 = random.nextInt(9);
            minePos3 = random.nextInt(9);
        }
        minePos1 = minePosOptions[minePos1];
        minePos2 = minePosOptions[minePos2];
        minePos3 = minePosOptions[minePos3];
        //插入2炸弹,炸弹不能放在第一行
        Map<Integer, Integer> boomPosOptions = new HashMap<>();
        for (int i = 5; i < 25; i++) {
            boomPosOptions.put(i, i);
        }
        //将不满足的位置去掉
        boomPosOptions.remove(flagPos);
        boomPosOptions.remove(minePos1);
        boomPosOptions.remove(minePos2);
        boomPosOptions.remove(minePos3);
        Integer[] restPosOptions = boomPosOptions.keySet().toArray(new Integer[14]);
        //抽选
        while ((boomPos1 == boomPos2)) {
            boomPos1 = random.nextInt(14);
            boomPos2 = random.nextInt(14);
        }
        boomPos1 = restPosOptions[boomPos1];
        boomPos2 = restPosOptions[boomPos2];
        //随机生成19个其它角色的棋子的排列
        List<Integer> otherChess = new ArrayList<>();
        int[] chessFlag = new int[]{3, 3, 3, 2, 2, 2, 2, 1, 1};
        for (int i = 0, len = chessFlag.length; i < len; i++) {
            for (int j = 0; j < chessFlag[i]; j++) {
                otherChess.add(i);
            }
        }
        Collections.shuffle(otherChess);

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            list.add(-1);
        }
        //军旗
        list.set(flagPos, 11);
        //地雷
        list.set(minePos1, 9);
        list.set(minePos2, 9);
        list.set(minePos3, 9);
        //炸弹
        list.set(boomPos1, 10);
        list.set(boomPos2, 10);
        //插入19个棋子
        for (int i = 0, index = 0; i < 25; i++) {
            if (list.get(i)==-1){
                list.set(i,otherChess.get(index));
                index++;
            }
        }
        return list;
    }

    public static List<Integer> genChessList() {
        List<Integer> list = new ArrayList<>();
        int[] chessNum = new int[]{3, 3, 3, 2, 2, 2, 2, 1, 1, 3, 2, 1};
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < chessNum[i]; j++) {
                list.add(i);
                list.add(i + 12);
            }
        }
        Collections.shuffle(list);
        return list;
    }
}
