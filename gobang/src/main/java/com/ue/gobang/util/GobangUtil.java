package com.ue.gobang.util;

import android.graphics.Point;

import java.util.ArrayList;

/**
 * Reference:
 * Author:
 * Date:2016/8/9.
 */
public class GobangUtil {
    private static final int MAX_COUNT_IN_LINE=5;

    public static boolean checkFiveInLine(ArrayList<Point> points) {
        for(Point point:points){
            int x=point.x;
            int y=point.y;

            boolean win=checkHorizontal(x,y,points);
            if(win){
                return true;
            }
            win=checkVertical(x,y,points);
            if(win){
                return true;
            }
            win=checkLeftUp(x,y,points);
            if(win){
                return true;
            }
            win=checkLeftDown(x,y,points);
            if(win){
                return true;
            }
        }
        return false;
    }

    /**
     * 横向检查
     * @param x
     * @param y
     * @param points
     * @return
     */
    private static boolean checkHorizontal(int x, int y, ArrayList<Point> points) {
        int count=1;
        //左
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){//记住i要从1开始才行
            if(points.contains(new Point(x-i,y))){
                count++;
            }else{
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE){
            return true;
        }
        //右
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){//记住i要从1开始才行
            if(points.contains(new Point(x+i,y))){
                count++;
            }else{
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE){
            return true;
        }
        return false;
    }

    private static boolean checkVertical(int x, int y, ArrayList<Point> points) {
        int count=1;
        //上
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){
            if(points.contains(new Point(x,y-i))){
                count++;
            }else{
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE){
            return true;
        }
        //下
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){
            if(points.contains(new Point(x,y+i))){
                count++;
            }else{
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE){
            return true;
        }
        return false;
    }

    private static boolean checkLeftUp(int x, int y, ArrayList<Point> points) {
        int count=1;
        //左
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){
            if(points.contains(new Point(x-i,y+i))){
                count++;
            }else{
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE){
            return true;
        }
        //右
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){
            if(points.contains(new Point(x+i,y-i))){
                count++;
            }else{
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE){
            return true;
        }
        return false;
    }

    private static boolean checkLeftDown(int x, int y, ArrayList<Point> points) {
        int count=1;
        //左
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){
            if(points.contains(new Point(x-i,y-i))){
                count++;
            }else{
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE){
            return true;
        }
        //右
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){
            if(points.contains(new Point(x+i,y+i))){
                count++;
            }else{
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE){
            return true;
        }
        return false;
    }
}
