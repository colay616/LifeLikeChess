package com.ue.moon_chess.entity;

/**
 * Created by hawk on 2016/12/13.
 */

public class MoonPoint {
    public float x;
    public float y;
    public int pointFlag;
    public int color;

    public MoonPoint(float x,float y,int pointFlag,int color){
        this.x=x;
        this.y=y;
        this.pointFlag=pointFlag;
        this.color=color;
    }

    public boolean equals(Object o){
        if(this==o)return true;
        if (o == null || getClass() != o.getClass()) return false;

        MoonPoint moonPoint= (MoonPoint) o;
        if(x!=moonPoint.x)return false;
        if(y!=moonPoint.y)return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("{'x':'%s','y':'%s','pointFlag':%d,'color':%d}",String.valueOf(x),String.valueOf(y),pointFlag,color);
    }
}
