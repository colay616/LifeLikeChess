package com.ue.army_chess.entity;

import android.text.TextUtils;

import com.ue.army_chess.util.ArmyChessUtil;

/**
 * Created by hawk on 2016/12/21.
 * 暗棋
 */
public class AArmyChess{
    private int f;
    public int x;
    public int y;//arrayY
    public int c;
    public int s;//STAT_UNKNOWN,FLAG_CAN_GO,FLAG_CAN_EAT
    private String txt;
    public int oldS;//for mine
    private String signedTxt;

    public String txt(){
        return txt;
    }

    //棋局坐标y
    public int getBoardY(){
        return y>5?y+1:y;
    }

    public void setF(int f) {
        this.f = f;
        this.txt = ArmyChessUtil.getChessTxt(f);
    }

    public int getF() {
        return f;
    }

    public boolean isSigned() {
        return !TextUtils.isEmpty(signedTxt);
    }

    public void setSign(String signedTxt){
        this.signedTxt="?"+signedTxt;
    }

    public String signedTxt(){
        return signedTxt;
    }

    public void cancelSign(){
        signedTxt="";
    }

    public AArmyChess(int f, int x, int y, int c) {
        this.f = f;
        this.x = x;
        this.y=y;
        this.c = c;
        this.s = ArmyChessUtil.STAT_UNKNOWN;
        this.txt = ArmyChessUtil.getChessTxt(f);
        oldS=this.s;
        cancelSign();
    }

    public AArmyChess(int f, int x, int y, int c, int s) {
        this.f = f;
        this.x = x;
        this.y=y;
        this.c = c;
        this.s = s;
        this.txt = ArmyChessUtil.getChessTxt(f);
        oldS=s;
        cancelSign();
    }
}
