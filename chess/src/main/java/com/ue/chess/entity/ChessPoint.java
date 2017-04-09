package com.ue.chess.entity;

import com.ue.chess.R;
import com.ue.chess.util.ChessUtil;
import com.ue.common.util.LogUtil;

/*
 * 这是一个棋子类
 * */
public class ChessPoint {
    private int f;//棋子标识
    public int x;
    public int y;
    public int c;//棋子颜色
    public int s; //-1为将要下的位置，2处于将要被移除的状态 0正常状态 , 1代表原先这里是有棋子的
    private int img;
    private boolean firstMove;
    private BaseGenerator mGenerator;

    public int f() {
        return f;
    }

    public void setF(int f){
        this.f=f;
        initImgGenerator(f,c);
    }

    public void setFirstMove(boolean firstMove) {
        this.firstMove = firstMove;
    }

    public boolean isFirstMove() {
        return firstMove;
    }

    public int img() {
        return img;
    }

    public void copyFrom(ChessPoint fromPoint) {
        c = fromPoint.c;
        s = ChessUtil.STATUS_NORMAL;
        img = fromPoint.img;
        mGenerator = fromPoint.mGenerator;
        firstMove=false;
        f = fromPoint.f;
    }

    public ChessPoint(int x,int y){
        this.x=x;
        this.y=y;
    }

    public ChessPoint(int f, int x, int y, int c) {
        this.f = f;
        this.x = x;
        this.y = y;
        this.c = c;
        this.s = ChessUtil.STATUS_NORMAL;
        this.firstMove=true;
        initImgGenerator(f, c);
    }

    public ChessPoint(int f, int x, int y, int c, int img) {
        this.f = f;
        this.x = x;
        this.y = y;
        this.c = c;
        this.s = ChessUtil.STATUS_CAN_GO;
        this.img = img;
        this.firstMove=true;
    }

    private void initImgGenerator(int f, int c) {
        switch (f) {
            case ChessUtil.ROOK:
                img = c == ChessUtil.WHITE_CHESS ? R.drawable.chess_wrook : R.drawable.chess_brook;
                mGenerator = new RookGenerator();
                break;
            case ChessUtil.QUEEN:
                img = c == ChessUtil.WHITE_CHESS ? R.drawable.chess_wqueen : R.drawable.chess_bqueen;
                mGenerator = new QueenGenerator();
                break;
            case ChessUtil.PAWN:
                img = c == ChessUtil.WHITE_CHESS ? R.drawable.chess_wpawn : R.drawable.chess_bpawn;
                mGenerator = new PawnGenerator();
                break;
            case ChessUtil.KNIGHT:
                img = c == ChessUtil.WHITE_CHESS ? R.drawable.chess_wknight : R.drawable.chess_bknight;
                mGenerator = new KnightGenerator();
                break;
            case ChessUtil.KING:
                img = c == ChessUtil.WHITE_CHESS ? R.drawable.chess_wking : R.drawable.chess_bking;
                mGenerator = new KingGenerator();
                break;
            case ChessUtil.BISHOP:
                img = c == ChessUtil.WHITE_CHESS ? R.drawable.chess_wbishop : R.drawable.chess_bbishop;
                mGenerator = new BishopGenerator();
                break;
        }
    }

    public void showValidMoves(boolean isMyMove,ChessPoint[][] qzs) {
        LogUtil.e("showValidMoves","mGenerator="+mGenerator);
        mGenerator.showValidMoves(isMyMove,qzs, this);
    }

    @Override
    public String toString() {
        return "ChessPoint{" +
                "f=" + f +
                ", x=" + x +
                ", y=" + y +
                ", c=" + c +
                ", s=" + s +
                ", img=" + img +
                ", firstMove=" + firstMove +
                ", mGenerator=" + mGenerator +
                '}';
    }
}