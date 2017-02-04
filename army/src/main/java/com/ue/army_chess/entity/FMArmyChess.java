package com.ue.army_chess.entity;

import com.ue.army_chess.util.ArmyChessUtil;

/**
 * Created by hawk on 2016/12/21.
 * 翻棋
 */
public class FMArmyChess {
    private int f;
    public int x;
    public int y;//arrayY
    public int c;
    public int s;//STAT_UNKNOWN,FLAG_CAN_GO,FLAG_CAN_EAT
    private String txt;

    public FMArmyChess(int f, int x, int y, int c) {
        this.f = f;
        this.x = x;
        this.y = y;
        this.c = c;
        this.s = ArmyChessUtil.STAT_UNKNOWN;
        this.txt = ArmyChessUtil.getChessTxt(f);
    }

    public FMArmyChess(int f, int x, int y, int c, int s) {
        this.f = f;
        this.x = x;
        this.y = y;
        this.c = c;
        this.s = s;
        this.txt = ArmyChessUtil.getChessTxt(f);
    }

    //棋局坐标y
    public int getBoardY() {
        return y > 5 ? y + 1 : y;
    }

    public void setF(int f) {
        this.f = f;
        this.txt = ArmyChessUtil.getChessTxt(f);
    }

    public int getF() {
        return f;
    }

    public String txt() {
        return txt;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        FMArmyChess point = (FMArmyChess) obj;

        if (x != point.x) return false;
        if (y != point.y) return false;

        return true;
    }
}
