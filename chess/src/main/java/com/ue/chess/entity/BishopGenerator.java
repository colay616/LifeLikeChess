package com.ue.chess.entity;

/**
 * Created by hawk on 2017/1/9.
 */

//象（B）：
//只能斜走，格数不限，但是，不能越子行棋。开局时每方各有两个象，
// 一个占白格，一个占黑格。和中国象棋相比，走法类似，只是没有不
// 能过河的概念，全盘皆能走。
public class BishopGenerator extends BaseGenerator {
    private static final String TAG=BishopGenerator.class.getSimpleName();
    @Override
    public void showValidMoves(boolean isMyMove,ChessPoint[][] qzs, ChessPoint fromPoint) {
        //左上
        for (int i = 1; i < 8; i++) {
            if (!changeStatus(qzs, fromPoint, fromPoint.x - i, fromPoint.y - i)) {
                break;
            }
        }
        //左下
        for (int i = 1; i < 8; i++) {
            if (!changeStatus(qzs, fromPoint, fromPoint.x - i, fromPoint.y + i)) {
                break;
            }
        }
        //右上
        for (int i = 1; i < 8; i++) {
            if (!changeStatus(qzs, fromPoint, fromPoint.x + i, fromPoint.y - i)) {
                break;
            }
        }
        //右下
        for (int i = 1; i < 8; i++) {
            if (!changeStatus(qzs, fromPoint, fromPoint.x + i, fromPoint.y + i)) {
                break;
            }
        }
    }
}
