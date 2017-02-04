package com.ue.moon_chess.util;

import com.ue.moon_chess.entity.MoonPoint;

/**
 * Created by hawk on 2016/12/13.
 */

public class MoonChessUtil {
    public static final int POINT_0=0;
    public static final int POINT_1=1;
    public static final int POINT_2=2;
    public static final int POINT_3=3;
    public static final int POINT_4=4;
    public static final int POINT_5=5;
    public static final int POINT_6=6;
    public static final int POINT_7=7;
    public static final int POINT_8=8;
    public static final int POINT_9=9;
    public static final int POINT_10=10;
    public static final int POINT_11=11;
    public static final int POINT_12=12;
    public static final int POINT_13=13;
    public static final int POINT_14=14;
    public static final int POINT_15=15;
    public static final int POINT_16=16;
    public static final int POINT_17=17;
    public static final int POINT_18=18;
    public static final int POINT_19=19;
    public static final int POINT_20=20;

    public static MoonPoint getMoonPoint(MoonPoint[]moonPoints,float x,float y,float halfChessWidth){
        MoonPoint theMoonPoint=null;
        MoonPoint tempMoonPoint;
        for(int i=0,len=moonPoints.length;i<len;i++){
            tempMoonPoint=moonPoints[i];
            //如果点击的坐标在棋子半径之内则表示点击了该点
            if((Math.abs(tempMoonPoint.x-x)<=halfChessWidth)&&(Math.abs(tempMoonPoint.y-y)<=halfChessWidth)){
                theMoonPoint=tempMoonPoint;
                break;
            }
        }
        return theMoonPoint;
    }

    public static MoonPoint[] getAroundPoints(MoonPoint[]moonPoints,int pointFlag){
        MoonPoint[]aroundPoints=null;
        switch (pointFlag){
            case POINT_0:
                aroundPoints=new MoonPoint[]{moonPoints[1],moonPoints[3],moonPoints[13]};
                break;
            case POINT_1:
                aroundPoints=new MoonPoint[]{moonPoints[0],moonPoints[2],moonPoints[3]};
                break;
            case POINT_2:
                aroundPoints=new MoonPoint[]{moonPoints[1],moonPoints[3],moonPoints[5]};
                break;
            case POINT_3:
                aroundPoints=new MoonPoint[]{moonPoints[0],moonPoints[1],moonPoints[2],moonPoints[17]};
                break;
            case POINT_4:
                aroundPoints=new MoonPoint[]{moonPoints[5],moonPoints[6],moonPoints[7],moonPoints[18]};
                break;
            case POINT_5:
                aroundPoints=new MoonPoint[]{moonPoints[2],moonPoints[4],moonPoints[6]};
                break;
            case POINT_6:
                aroundPoints=new MoonPoint[]{moonPoints[4],moonPoints[5],moonPoints[7]};
                break;
            case POINT_7:
                aroundPoints=new MoonPoint[]{moonPoints[4],moonPoints[6],moonPoints[10]};
                break;
            case POINT_8:
                aroundPoints=new MoonPoint[]{moonPoints[9],moonPoints[11],moonPoints[15]};
                break;
            case POINT_9:
                aroundPoints=new MoonPoint[]{moonPoints[8],moonPoints[10],moonPoints[11],moonPoints[19]};
                break;
            case POINT_10:
                aroundPoints=new MoonPoint[]{moonPoints[7],moonPoints[9],moonPoints[11]};
                break;
            case POINT_11:
                aroundPoints=new MoonPoint[]{moonPoints[8],moonPoints[9],moonPoints[10]};
                break;
            case POINT_12:
                aroundPoints=new MoonPoint[]{moonPoints[13],moonPoints[14],moonPoints[15]};
                break;
            case POINT_13:
                aroundPoints=new MoonPoint[]{moonPoints[0],moonPoints[12],moonPoints[14]};
                break;
            case POINT_14:
                aroundPoints=new MoonPoint[]{moonPoints[12],moonPoints[13],moonPoints[15],moonPoints[16]};
                break;
            case POINT_15:
                aroundPoints=new MoonPoint[]{moonPoints[8],moonPoints[12],moonPoints[14]};
                break;
            case POINT_16:
                aroundPoints=new MoonPoint[]{moonPoints[14],moonPoints[17],moonPoints[19],moonPoints[20]};
                break;
            case POINT_17:
                aroundPoints=new MoonPoint[]{moonPoints[3],moonPoints[16],moonPoints[18],moonPoints[20]};
                break;
            case POINT_18:
                aroundPoints=new MoonPoint[]{moonPoints[4],moonPoints[17],moonPoints[19],moonPoints[20]};
                break;
            case POINT_19:
                aroundPoints=new MoonPoint[]{moonPoints[9],moonPoints[16],moonPoints[18],moonPoints[20]};
                break;
            case POINT_20:
                aroundPoints=new MoonPoint[]{moonPoints[16],moonPoints[17],moonPoints[18],moonPoints[19]};
                break;
        }
        return aroundPoints;
    }

    public static boolean isEqualPoints(MoonPoint pointOne,MoonPoint pointTwo,float halfChessWidth){
        return ((Math.abs(pointOne.x-pointTwo.x)<=halfChessWidth)&&(Math.abs(pointOne.y-pointTwo.y)<=halfChessWidth));
    }

    //上下对称，左右对称
    public static int getOppositePointFlag(int paramFlag){
        int oppositeFlag=-1;
        switch (paramFlag){
            case POINT_0:
                oppositeFlag=POINT_10;
                break;
            case POINT_10:
                oppositeFlag=POINT_0;
                break;
            case POINT_1:
                oppositeFlag=POINT_11;
                break;
            case POINT_11:
                oppositeFlag=POINT_1;
                break;
            case POINT_2:
            oppositeFlag=POINT_8;
            break;
            case POINT_8:
                oppositeFlag=POINT_2;
                break;
            case POINT_3:
                oppositeFlag=POINT_9;
                break;
            case POINT_9:
                oppositeFlag=POINT_3;
                break;
            case POINT_4:
                oppositeFlag=POINT_14;
                break;
            case POINT_14:
                oppositeFlag=POINT_4;
                break;
            case POINT_5:
                oppositeFlag=POINT_15;
                break;
            case POINT_15:
                oppositeFlag=POINT_5;
                break;
            case POINT_6:
            oppositeFlag=POINT_12;
            break;
            case POINT_12:
                oppositeFlag=POINT_6;
                break;
            case POINT_7:
                oppositeFlag=POINT_13;
                break;
            case POINT_13:
                oppositeFlag=POINT_7;
                break;
            case POINT_16:
                oppositeFlag=POINT_18;
                break;
            case POINT_18:
                oppositeFlag=POINT_16;
                break;
            case POINT_17:
                oppositeFlag=POINT_19;
            break;
            case POINT_19:
                oppositeFlag=POINT_17;
                break;
            case POINT_20:
                oppositeFlag=POINT_20;
                break;
            default:
                oppositeFlag=paramFlag;
        }
        return oppositeFlag;
    }
}
