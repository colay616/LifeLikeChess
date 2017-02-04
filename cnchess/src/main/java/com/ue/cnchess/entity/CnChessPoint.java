package com.ue.cnchess.entity;


import com.ue.cnchess.util.CNChessUtil;

/*
 * 这是一个棋子类
 * */
public class CnChessPoint {
    private int f;//棋子标识
    public int x;
    public int y;
    public int c;//棋子颜色
    public int s; //-1为将要下的位置，2处于将要被移除的状态 0正常状态 , 1代表原先这里是有棋子的
    private String txt;
    private BaseGenerator moveGenerator;

    @Override
    public String toString() {
        return String.format("{'f':%d,'x':%d,'y':%d,'c':%d,'s':%d,'txt':%s}",f,x,y,c,s,txt);
    }

    public int f() {
        return f;
    }

    public String txt() {
        return txt;
    }

    /**
     * 棋子移动时候使用,节省开销
     *
     * @param fromPoint
     */
    public void copyFrom(CnChessPoint fromPoint) {
        f = fromPoint.f();
        txt = fromPoint.txt;
        c = fromPoint.c;
        moveGenerator = fromPoint.moveGenerator;
        s = CNChessUtil.STATUS_NORMAL;
    }

    public CnChessPoint copyAll(){
        CnChessPoint copyPoint=new CnChessPoint();
        copyPoint.f=f;
        copyPoint.x=x;
        copyPoint.y=y;
        copyPoint.c=c;
        copyPoint.s=s;
        copyPoint.txt=txt;
        copyPoint.moveGenerator=moveGenerator;
        return copyPoint;
    }

    private CnChessPoint(){}

    public CnChessPoint(int f, int x, int y, int c) {
        this.f = f;
        this.x = x;
        this.y = y;
        this.c = c;
        this.s = CNChessUtil.STATUS_NORMAL;
        initTxtGenerator();
    }

    /**
     * Generator中生成CAN_GO位置时使用,不用initTxtGenerator(),节省开销
     * @param f
     * @param x
     * @param y
     * @param c
     * @param txt
     */
    public CnChessPoint(int f, int x, int y, int c, String txt) {
        this.f = f;
        this.x = x;
        this.y = y;
        this.c = c;
        this.s = CNChessUtil.STATUS_CAN_GO;
        this.txt = txt;
    }

    private void initTxtGenerator() {
        switch (f) {
            case CNChessUtil.O_GENERAL:
            case CNChessUtil.M_GENERAL:
                txt = c == CNChessUtil.RED ? "帥" : "将";
                moveGenerator = new GeneralGenerator();
                break;
            case CNChessUtil.CHARIOT:
                txt = "車";
                moveGenerator = new ChariotGenerator();
                break;
            case CNChessUtil.HORSE:
                txt = "馬";
                moveGenerator = new HorseGenerator();
                break;
            case CNChessUtil.CANNON:
                txt = "炮";
                moveGenerator = new CannonGenerator();
                break;
            case CNChessUtil.GUARD:
                txt = c == CNChessUtil.RED ? "仕" : "士";
                moveGenerator = new GuardGenerator();
                break;
            case CNChessUtil.ELEPHANT:
                txt = c == CNChessUtil.RED ? "相" : "象";
                moveGenerator = new ElephantGenerator();
                break;
            case CNChessUtil.O_SOLDIER:
            case CNChessUtil.M_SOLDIER:
                txt = c == CNChessUtil.RED ? "兵" : "卒";
                moveGenerator = new SoldierGenerator();
                break;
        }
    }

    public void showValidMoves(CnChessPoint[][] qzs) {
        moveGenerator.showValidMoves(qzs, this);
    }
}