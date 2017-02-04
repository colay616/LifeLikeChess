package com.ue.cnchess.entity;

import com.ue.cnchess.util.CNChessUtil;

/**
 * Created by hawk on 2017/1/7.
 */

public class GeneralGenerator extends BaseGenerator {
    @Override
    public void showValidMoves(CnChessPoint[][] qzs, CnChessPoint fromPoint) {
        //是否将帅相对且之间没子
        int oFlag, yStartIndx;
        if (fromPoint.f() == CNChessUtil.M_GENERAL) {
            yStartIndx = 0;
            oFlag = CNChessUtil.O_GENERAL;
        } else {
            yStartIndx = 7;
            oFlag = CNChessUtil.M_GENERAL;
        }
        int yy = -1;
        for (int i = 0, j = yStartIndx; i < 3; i++, j++) {
            if (null != qzs[fromPoint.x][j] && qzs[fromPoint.x][j].f() == oFlag) {
                yy = j;
                break;
            }
        }
        if (yy != -1) {//将帅相对
            boolean isBlock = false;
            int from, to;
            if (fromPoint.y < yy) {
                from = fromPoint.y + 1;
                to = yy;
            } else {
                from = yy + 1;
                to = fromPoint.y;
            }
            for (int i = from; i < to; i++) {
                if (null != qzs[fromPoint.x][i]) {
                    isBlock = true;
                    break;
                }
            }
            if (!isBlock) {//之间无子
                qzs[fromPoint.x][yy].s = CNChessUtil.STATUS_CAN_EAT;
            }
        }

        changeStatus(qzs, fromPoint, fromPoint.x, fromPoint.y - 1);//前
        changeStatus(qzs, fromPoint, fromPoint.x, fromPoint.y + 1);//后
        changeStatus(qzs, fromPoint, fromPoint.x - 1, fromPoint.y);//左
        changeStatus(qzs, fromPoint, fromPoint.x + 1, fromPoint.y);//右
    }

    @Override
    protected boolean isOutOfRange(CnChessPoint fromPoint, int xx, int yy) {
        if (xx < 3) {
            return true;
        }
        if (xx > 5) {
            return true;
        }
        if (yy < 0) {
            return true;
        }
        if (yy > 9) {
            return true;
        }
        if (yy > 2 && yy < 7) {
            return true;
        }
        return false;
    }
}
