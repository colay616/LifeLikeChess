package com.ue.reversi.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hawk on 2016/11/27.
 */

public class ReversiRecord {
    public int id;
    public int x;
    public int y;
    public int c;
    public List<ReversiMove> changedChess;

    public ReversiRecord(){}

    public ReversiRecord(int x,int y,int c,List<ReversiMove> changedChess){
        this.x=x;
        this.y=y;
        this.c=c;
        this.changedChess=changedChess;
    }

    @Override
    public String toString() {
        if(null==changedChess){
            changedChess=new ArrayList<>();
        }
        return String.format("{'x':%d,'y':%d,'c':%d,'changedChess':%s}",x,y,c,changedChess.toString());
    }
}
