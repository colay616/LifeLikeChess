package com.ue.moon_chess.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.hyphenate.easeui.game.GameConstants;
import com.hyphenate.easeui.game.base.BaseGameView;
import com.hyphenate.easeui.game.db.DBConstants;
import com.hyphenate.easeui.game.db.GameDbManager;
import com.ue.common.util.DisplayUtil;
import com.ue.common.util.LogUtil;
import com.ue.moon_chess.R;
import com.ue.moon_chess.entity.MoonPoint;
import com.ue.moon_chess.entity.MoonRecord;
import com.ue.moon_chess.entity.RecordParser;
import com.ue.moon_chess.util.MoonChessUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hawk on 2016/12/13.
 */

public class MoonView extends BaseGameView {
    private static final int NULL_COLOR = -1;
    private static final int BLACK_COLOR = 0;
    private static final int WHITE_COLOR = 1;
    private float mPanelWidth;
    private float mPanelHeight;
    private Paint mPaint;
    private float halfPanelWidth;
    private float mSquareWidth;//棋盘格子宽高
    private float halfSquareWidth;
    private float width_15;
    private float width_11;
    private float width_41;
    private float width_13;
    private float width_1;
    private float width_3;
    private float width_6;
    private float width_5;
    private float width_9;
    private int chessWidth;
    private float halfChessWidth;

    private Bitmap mWhitePiece;
    private Bitmap mBlackPiece;
    private Bitmap background;

    private Bitmap myBitmap;
    private Bitmap oppoBitmap;
    private int myColor = BLACK_COLOR;
    private int oppoColor = WHITE_COLOR;

    private MoonPoint[] mMoonPoints = new MoonPoint[21];

    private MoonPoint selectedPoint;
    private MoonRecord lastChessMove;

    public MoonView(Context context) {
        this(context, null, 0);
    }

    public MoonView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MoonView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);//抖动处理，平滑处理
        mPaint.setStrokeWidth(5f);

        mWhitePiece = BitmapFactory.decodeResource(getResources(), R.mipmap.white1);
        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.mipmap.black1);
        background = BitmapFactory.decodeResource(getResources(), R.mipmap.mood);

        myBitmap = mBlackPiece;
        oppoBitmap = mWhitePiece;

        mPanelHeight = DisplayUtil.getScreenWidth(context);
        mPanelWidth = mPanelHeight * 0.95f;
        mSquareWidth = mPanelWidth / 7;

        halfPanelWidth = mPanelWidth * 0.5f;
        halfSquareWidth = mSquareWidth * 0.5f;

        width_1 = mPanelWidth * 1f / 14;
        width_3 = mPanelWidth * 3f / 14;
        width_5 = mPanelWidth * 5f / 14;
        width_6 = mPanelWidth * 6f / 7;
        width_9 = mPanelWidth * 9f / 14;
        width_11 = mPanelWidth * 11f / 14;
        width_13 = mPanelWidth * 13f / 14;
        width_15 = mPanelWidth * 15f / 56;
        width_41 = mPanelWidth * 41f / 56;

        chessWidth = (int) (mSquareWidth * 0.75f);
        halfChessWidth = mSquareWidth * 0.375f;
        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, chessWidth, chessWidth, false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, chessWidth, chessWidth, false);
        background = Bitmap.createScaledBitmap(background, (int) mPanelWidth, (int) mPanelWidth, false);

        initMoonPoints();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) mPanelWidth, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) mPanelHeight, MeasureSpec.EXACTLY);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void checkIsGameOver() {
        int myChessCount = 0, oppoChessCount = 0;
        for (int i = 0, len = mMoonPoints.length; i < len; i++) {
            if (mMoonPoints[i].color == NULL_COLOR) {
                continue;
            }
            if (mMoonPoints[i].color == myColor) {
                myChessCount += 1;
            } else {
                oppoChessCount += 1;
            }
            if (myChessCount > 2 && oppoChessCount > 2) {
                break;
            }
        }
        if (myChessCount < 3) {
            gameOver(mOnPlayListener.FLAG_OPPO_WIN);
        } else if (oppoChessCount < 3) {
            gameOver(mOnPlayListener.FLAG_I_WIN);
        }
    }

    public void undoOnce(boolean isMyUndo) {
        Object[] lastTwoMoveRecord = GameDbManager.getInstance().getLastTwoRecords(DBConstants.TABLE_MOON, new RecordParser());
        if (null == lastTwoMoveRecord) {
            return;
        }
        lastChessMove = (MoonRecord) lastTwoMoveRecord[0];
        if(lastChessMove==null){
            return;
        }
        mMoonPoints[lastChessMove.fromF].color = lastChessMove.fromC;
        mMoonPoints[lastChessMove.toF].color = NULL_COLOR;

        JSONArray killedFs = lastChessMove.killedFs;
        if (null != killedFs) {
            try {
                int killedColor = lastChessMove.fromC == myColor ? oppoColor : myColor;
                for (int i = 0, len = killedFs.length(); i < len; i++) {
                    mMoonPoints[killedFs.getInt(i)].color = killedColor;
                }
            } catch (JSONException e) {
                LogUtil.i(TAG, "killedFlagsArray,error=" + e.getMessage());
            }
        }
        GameDbManager.getInstance().deleteChessRecord(DBConstants.TABLE_MOON, lastChessMove.id);

        lastChessMove = (MoonRecord) lastTwoMoveRecord[1];
        isMyTurn = !isMyTurn;
        possiblyKilledPoints.clear();
        selectedPoint = null;
        canUndoTime--;

        //enableRender();
    }

    @Override
    public void initChessBoard(boolean isIFirst) {
        LogUtil.i(TAG, "aInitChessBoard");
        if (isIFirst) {
            myColor = BLACK_COLOR;
            oppoColor = WHITE_COLOR;
            myBitmap = mBlackPiece;
            oppoBitmap = mWhitePiece;
        } else {
            myColor = WHITE_COLOR;
            oppoColor = BLACK_COLOR;
            myBitmap = mWhitePiece;
            oppoBitmap = mBlackPiece;
        }

        mMoonPoints[0].color = oppoColor;
        mMoonPoints[1].color = oppoColor;
        mMoonPoints[2].color = oppoColor;
        mMoonPoints[3].color = oppoColor;
        mMoonPoints[5].color = oppoColor;
        mMoonPoints[13].color = oppoColor;

        mMoonPoints[7].color = myColor;
        mMoonPoints[8].color = myColor;
        mMoonPoints[9].color = myColor;
        mMoonPoints[10].color = myColor;
        mMoonPoints[11].color = myColor;
        mMoonPoints[15].color = myColor;

        mMoonPoints[4].color = NULL_COLOR;
        mMoonPoints[6].color = NULL_COLOR;
        mMoonPoints[12].color = NULL_COLOR;
        mMoonPoints[14].color = NULL_COLOR;
        mMoonPoints[16].color = NULL_COLOR;
        mMoonPoints[17].color = NULL_COLOR;
        mMoonPoints[18].color = NULL_COLOR;
        mMoonPoints[19].color = NULL_COLOR;
        mMoonPoints[20].color = NULL_COLOR;
        //enableRender();
    }

    @Override
    public void startGame(boolean isIFirst) {
        GameDbManager.getInstance().clearChessRecord(DBConstants.TABLE_MOON);
        possiblyKilledPoints.clear();
        lastChessMove = null;
        selectedPoint = null;
        isMyTurn = isIFirst;
        canUndoTime = 0;
        initChessBoard(isIFirst);
        isGameStarted = true;
    }

    @Override
    public void playChess(int[] oData) {
        int[] data;
        if (isMyTurn || gameMode != GameConstants.MODE_ONLINE) {
            data = oData;
        } else {
            //在线模式需要对对方的数据进行转换
            data = new int[2];
            data[0] = MoonChessUtil.getOppositePointFlag(oData[0]);
            data[1] = MoonChessUtil.getOppositePointFlag(oData[1]);
            //
//            LogUtil.i(TAG,"aPlayChess,oData0="+oData[0]+",oData1="+oData[1]+",data0="+data[0]+",data1="+data[1]);
        }

        int fromColor = mMoonPoints[data[0]].color;

        //移动
        mMoonPoints[data[1]].color = mMoonPoints[data[0]].color;
        mMoonPoints[data[0]].color = NULL_COLOR;

        selectedPoint = null;

        checkHasChessKilled();

        //enableRender();

        JSONArray killedFs = new JSONArray();
        if (possiblyKilledPoints.size() > 0) {
            for (MoonPoint moonPoint : possiblyKilledPoints) {
                killedFs.put(moonPoint.pointFlag);
            }
        }
        saveMoveToRecord(data[0], fromColor, data[1], killedFs);

        checkIsGameOver();

        if (isMyTurn) {
            isMyTurn = false;
            mOnPlayListener.onIPlayed(data);
        } else {
            isMyTurn = true;
            mOnPlayListener.onOppoPlayed(data);
        }
    }

    private void initMoonPoints() {
        LogUtil.i(TAG, "initMoonPoints");
        mMoonPoints[0] = new MoonPoint(width_15, mSquareWidth, MoonChessUtil.POINT_0, oppoColor);
        mMoonPoints[1] = new MoonPoint(halfPanelWidth, halfSquareWidth, MoonChessUtil.POINT_1, oppoColor);
        mMoonPoints[2] = new MoonPoint(width_41, mSquareWidth, MoonChessUtil.POINT_2, oppoColor);
        mMoonPoints[3] = new MoonPoint(halfPanelWidth, width_3, MoonChessUtil.POINT_3, oppoColor);
        mMoonPoints[5] = new MoonPoint(width_6, width_15, MoonChessUtil.POINT_5, oppoColor);
        mMoonPoints[13] = new MoonPoint(mSquareWidth, width_15, MoonChessUtil.POINT_13, oppoColor);

        mMoonPoints[7] = new MoonPoint(width_6, width_41, MoonChessUtil.POINT_7, myColor);
        mMoonPoints[8] = new MoonPoint(width_15, width_6, MoonChessUtil.POINT_8, myColor);
        mMoonPoints[9] = new MoonPoint(halfPanelWidth, width_11, MoonChessUtil.POINT_9, myColor);
        mMoonPoints[10] = new MoonPoint(width_41, width_6, MoonChessUtil.POINT_10, myColor);
        mMoonPoints[11] = new MoonPoint(halfPanelWidth, width_13, MoonChessUtil.POINT_11, myColor);
        mMoonPoints[15] = new MoonPoint(mSquareWidth, width_41, MoonChessUtil.POINT_15, myColor);

        mMoonPoints[4] = new MoonPoint(width_11, halfPanelWidth, MoonChessUtil.POINT_4, NULL_COLOR);
        mMoonPoints[6] = new MoonPoint(width_13, halfPanelWidth, MoonChessUtil.POINT_6, NULL_COLOR);
        mMoonPoints[12] = new MoonPoint(halfSquareWidth, halfPanelWidth, MoonChessUtil.POINT_12, NULL_COLOR);
        mMoonPoints[14] = new MoonPoint(width_3, halfPanelWidth, MoonChessUtil.POINT_14, NULL_COLOR);
        mMoonPoints[16] = new MoonPoint(width_5, halfPanelWidth, MoonChessUtil.POINT_16, NULL_COLOR);
        mMoonPoints[17] = new MoonPoint(halfPanelWidth, width_5, MoonChessUtil.POINT_17, NULL_COLOR);
        mMoonPoints[18] = new MoonPoint(width_9, halfPanelWidth, MoonChessUtil.POINT_18, NULL_COLOR);
        mMoonPoints[19] = new MoonPoint(halfPanelWidth, width_9, MoonChessUtil.POINT_19, NULL_COLOR);
        mMoonPoints[20] = new MoonPoint(halfPanelWidth, halfPanelWidth, MoonChessUtil.POINT_20, NULL_COLOR);
    }

    public void drawPieces(Canvas canvas) {
//        mPaint.setColor(Color.parseColor("#ffffff"));
//        mPaint.setTextSize(18);
        MoonPoint tempPoint;
        for (int i = 0, len = mMoonPoints.length; i < len; i++) {
            tempPoint = mMoonPoints[i];
            if (tempPoint.color == myColor) {
                canvas.drawBitmap(myBitmap, tempPoint.x - halfChessWidth, tempPoint.y - halfChessWidth, null);
            } else if (tempPoint.color == oppoColor) {
                canvas.drawBitmap(oppoBitmap, tempPoint.x - halfChessWidth, tempPoint.y - halfChessWidth, null);
            }
//            canvas.drawBitmap(myBitmap, tempPoint.x - halfChessWidth, tempPoint.y - halfChessWidth, null);
//            canvas.drawText(""+i,tempPoint.x,tempPoint.y,mPaint);
        }

        if (null != selectedPoint) {
            mPaint.setColor(getResources().getColor(R.color.green_66cc99));
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(selectedPoint.x, selectedPoint.y, 0.5f * halfChessWidth, mPaint);
        }

        if (null != lastChessMove) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(getResources().getColor(R.color.red_cd0b17));
            canvas.drawCircle(mMoonPoints[lastChessMove.fromF].x, mMoonPoints[lastChessMove.fromF].y, halfChessWidth, mPaint);
            mPaint.setColor(getResources().getColor(R.color.blue_0066ff));
            canvas.drawCircle(mMoonPoints[lastChessMove.toF].x, mMoonPoints[lastChessMove.toF].y, halfChessWidth, mPaint);
        }
        //死亡的棋子
        if (possiblyKilledPoints.size() > 0) {
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(getResources().getColor(R.color.purple_9b73e6));
            for (MoonPoint deadPoint : possiblyKilledPoints) {
                canvas.drawCircle(deadPoint.x, deadPoint.y, halfChessWidth, mPaint);
            }
        }
    }

    public void drawBoard(Canvas canvas) {
        canvas.drawBitmap(background, 0, 0, null);

        mPaint.setColor(0x88000000);
        mPaint.setStyle(Paint.Style.STROKE);//描边

//        canvas.drawLine(mSquareWidth*0.5f,mSquareWidth,mPanelWidth*13f/14,mSquareWidth,mPaint);//横
//        for(int i=0;i<7;i++){
//            canvas.drawLine(halfSquareWidth,mSquareWidth*i+halfSquareWidth,mPanelWidth-halfSquareWidth,mSquareWidth*i+halfSquareWidth,mPaint);//横
//            canvas.drawLine(mSquareWidth*i+halfSquareWidth,halfSquareWidth,mSquareWidth*i+halfSquareWidth,mPanelWidth-halfSquareWidth,mPaint);//竖
//        }

        canvas.drawLine(halfSquareWidth, halfPanelWidth, width_13, halfPanelWidth, mPaint);//横
        canvas.drawLine(halfPanelWidth, halfSquareWidth, halfPanelWidth, width_13, mPaint);//竖

        canvas.drawCircle(halfPanelWidth, halfPanelWidth, 3f / 7 * mPanelWidth, mPaint);//外圆
        canvas.drawCircle(halfPanelWidth, halfPanelWidth, mSquareWidth, mPaint);//内圆

        RectF rectF = new RectF(width_15, width_11, width_41, width_13);//下
        canvas.drawArc(rectF, 180, 180, false, mPaint);

        rectF = new RectF(width_15, width_1, width_41, width_3);//上
        canvas.drawArc(rectF, 0, 180, false, mPaint);

        rectF = new RectF(width_1, width_15, width_3, width_41);//左
        canvas.drawArc(rectF, -90, 180, false, mPaint);

        rectF = new RectF(width_11, width_15, width_13, width_41);//右
        canvas.drawArc(rectF, 90, 180, false, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (!isGameStarted) {
                return false;
            }
            if ((gameMode != GameConstants.MODE_DOUBLE) && !isMyTurn) {
                return true;
            }

            MoonPoint theMoonPoint = MoonChessUtil.getMoonPoint(mMoonPoints, event.getX(), event.getY(), halfChessWidth);
            if (null == theMoonPoint) {
                return true;
            }
            //如果点击的是自己的棋子，则标记为选中
            if ((isMyTurn && (theMoonPoint.color == myColor)) || (!isMyTurn && (theMoonPoint.color == oppoColor))) {
                selectedPoint = theMoonPoint;
                //enableRender();
            } else if (theMoonPoint.color == NULL_COLOR) {//点击的是空白处
                if (null != selectedPoint) {
                    MoonPoint[] aroundPoints = MoonChessUtil.getAroundPoints(mMoonPoints, selectedPoint.pointFlag);
                    MoonPoint tempPoint;
                    for (int i = 0, len = aroundPoints.length; i < len; i++) {
                        tempPoint = aroundPoints[i];
                        if (MoonChessUtil.isEqualPoints(theMoonPoint, tempPoint, halfChessWidth)) {
                            playChess(new int[]{selectedPoint.pointFlag, theMoonPoint.pointFlag});
                            break;
                        }
                    }
                }
            }
        }
        return true;
    }

    private void saveMoveToRecord(int fromFlag, int fromColor, int toFlag, JSONArray killedFlags) {
        lastChessMove = new MoonRecord();
        lastChessMove.fromF = fromFlag;
        lastChessMove.fromC = fromColor;
        lastChessMove.toF = toFlag;
        lastChessMove.killedFs = killedFlags;

        boolean insertResult = GameDbManager.getInstance().saveChessRecord(DBConstants.TABLE_MOON, lastChessMove.toString());
        if (insertResult) {
            canUndoTime++;
        } else {
            canUndoTime = -100;
        }
    }

    private void checkHasChessKilled() {
        possiblyKilledPoints.clear();
        for (int i = 0, len = mMoonPoints.length; i < len; i++) {
            if (mMoonPoints[i].color == NULL_COLOR) {
                continue;
            }
            //这里要对检查过了的棋子进行过滤，避免内存溢出
            if (checkedPoints.contains(mMoonPoints[i])) {
                continue;
            }
//            LogUtil.i(TAG, "checkHasChessKilled,flag=" + mMoonPoints[i].pointFlag);
            checkThisChessIsKilled(mMoonPoints[i]);
            //如果列表中有棋子，表示这些棋子要死亡
            if (possiblyKilledPoints.size() > 0) {
                for (MoonPoint moonPoint : possiblyKilledPoints) {
                    mMoonPoints[moonPoint.pointFlag].color = NULL_COLOR;
                }
                //检查到棋子死亡就停止检查
                break;
            }
        }
        checkedPoints.clear();
    }

    private List<MoonPoint> checkedPoints = new ArrayList<>();//检查过了的棋子
    //记录检查过的棋子以及其是否周边有空位，最后如果都没有空位的话，这个列表的点都会去掉
    private List<MoonPoint> possiblyKilledPoints = new ArrayList<>();

//    private int iteratorTime = 0;

    //关键：判断是否形成包围
    private void checkThisChessIsKilled(MoonPoint targetPoint) {
//        LogUtil.i(TAG, "*******************************checkThisChessIsKilled start*****************" + iteratorTime++);
//        LogUtil.i(TAG,"target point flag="+targetPoint.pointFlag);
        checkedPoints.add(targetPoint);//将该点加入已检查列表
        MoonPoint[] aroundPoints = MoonChessUtil.getAroundPoints(mMoonPoints, targetPoint.pointFlag);
        //第一次判断:棋子周边是否有空位，如果有，不死
        boolean hasNullAround = false;
        for (int i = 0, len = aroundPoints.length; i < len; i++) {
            if (aroundPoints[i].color == NULL_COLOR) {
                hasNullAround = true;
                break;
            }
        }
//        LogUtil.i(TAG, "checkThisChessIsKilled,hasNullAround=" + hasNullAround);
        //检查到棋子周边有空位的话，对于单棋子的情况该棋子不死亡，对于多棋子的情况，列表中的棋子都不死亡
        if (hasNullAround) {
            possiblyKilledPoints.clear();
            return;
        }
        //第一次判断结果是棋子周边没有空位的话，进行第二次判断
        //判断周围是否有自己的棋子，如果没有，该棋子死亡;这种判断是针对单棋子的，即该棋子周围都唯有自己的棋子，
        //  如果是有两个或多个棋子相连的话，这里的判断跳过
        boolean hasOwnChessAround = false;
        for (int i = 0, len = aroundPoints.length; i < len; i++) {
            if (aroundPoints[i].color == targetPoint.color) {
                hasOwnChessAround = true;
                break;
            }
        }
//        LogUtil.i(TAG, "checkThisChessIsKilled,hasOwnChessAround=" + hasOwnChessAround);
        if (!hasOwnChessAround) {
            possiblyKilledPoints.add(targetPoint);
//            mMoonPoints[targetPoint.pointFlag].color = NULL_COLOR;
            //检查到棋子死亡则停止检查，这种情况属于单棋子的情况；多棋子的话是在checkHasPointKilled中判断的
            return;
        }

        //第二次判断结果是棋子周围有自己的棋子的话，进行第三次判断
        //迭代判断连接的己方棋子[周边的棋子的周边]是否有空位，没有的话放进列表中，有的话列表被清空，最后判断列表是否有
        //  棋子，有的话，这些棋子都死亡了
        possiblyKilledPoints.add(targetPoint);
        for (int i = 0, len = aroundPoints.length; i < len; i++) {
            if (targetPoint.color != aroundPoints[i].color) {//这颗棋子不是己方的，跳过
                continue;
            }
            //
//            LogUtil.i(TAG, "checkThisChessIsKilled,flag=" + aroundPoints[i].pointFlag + ",checkedPoints contain=" + checkedPoints.contains(aroundPoints[i]) + ";possiblyKilledPoints contain=" + possiblyKilledPoints.contains(aroundPoints[i]));
            if (checkedPoints.contains(aroundPoints[i])) {
                //如果检查过了且死亡列表没有该点，说明死亡列表上的棋子死不了，可以停止该for循环
                if (!possiblyKilledPoints.contains(aroundPoints[i])) {
                    possiblyKilledPoints.clear();
                    break;
                }
                //如果检查过了且死亡列表有该点，跳到下一个继续判断
                continue;//!!!!!!如果去掉这个会死循环，checkHasChessKilled()死循环，target point flag的值不断在0和1切换
                //死循环描述：
                //检查POINT0，没有空点，有己方，将POINT0加入已查列表和死亡列表；检查POINT1确认POINT0是否死亡，POINT1，没有空点，有己方，将POINT0加入已查列表和死亡列表；检查POINT0确认POINT1是否死亡....
            }
            checkThisChessIsKilled(aroundPoints[i]);
//            LogUtil.i(TAG, "checkThisChessIsKilled,*******size=" + possiblyKilledPoints.size());
            //如果possiblyKilledPoints没有数据，说明检查到了空点，数据才被清除了，所以可以停止本轮判断，不必要进行多余的判断了
            if (possiblyKilledPoints.size() == 0) {
                break;
            }
        }
//        LogUtil.i(TAG, "*******************************checkThisChessIsKilled end*****************" + (--iteratorTime));
    }


    //    private int myColor = BLACK_COLOR;
//    private int oppoColor = WHITE_COLOR;
//    private MoonPoint[] mMoonPoints = new MoonPoint[21];
//    private MoonPoint selectedPoint;
//    private MoonRecord lastChessMove;
//    private int canUndoTime = 0;
    private static final String INSTANCE = "instance";
    private static final String INSTANCE_MY_COLOR = "myColor";
    private static final String INSTANCE_OPPO_COLOR = "oppoColor";
    private static final String INSTANCE_GAME_OVER = "aIsGameStarted";
    private static final String INSTANCE_IS_MY_TURN = "aIsMyTurn";
    private static final String INSTANCE_CAN_UNDO_TIME = "canUndoTime";
    private static final String INSTANCE_GAME_MODE = "gameMode";
    private static final String INSTANCE_LAST_MOVE = "lastChessMove";
    private static final String INSTANCE_SELECTED_POINT = "selectedPoint";
    private static final String INSTANCE_POINTS_COLOR = "mMoonPointsColor";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE, super.onSaveInstanceState());
        bundle.putInt(INSTANCE_MY_COLOR, myColor);
        bundle.putInt(INSTANCE_OPPO_COLOR, oppoColor);
        bundle.putBoolean(INSTANCE_GAME_OVER, isGameStarted);
        bundle.putBoolean(INSTANCE_IS_MY_TURN, isMyTurn);
        bundle.putInt(INSTANCE_CAN_UNDO_TIME, canUndoTime);
        bundle.putInt(INSTANCE_GAME_MODE, gameMode);
        if (null != selectedPoint) {
            bundle.putString(INSTANCE_SELECTED_POINT, selectedPoint.toString());
        }
        bundle.putString(INSTANCE_LAST_MOVE, lastChessMove.toString());
        bundle.putString(INSTANCE_POINTS_COLOR, translateMoonPointsColorToJson(mMoonPoints));
        return bundle;
    }

    private String translateMoonPointsColorToJson(MoonPoint[] moonPoints) {
        List<Integer> flags = new ArrayList<>(moonPoints.length);
        for (int i = 0, len = moonPoints.length; i < len; i++) {
            flags.add(moonPoints[i].color);
        }
        LogUtil.i(TAG, "translateMoonPointsColorToJson,flags=" + flags.toString());
        return flags.toString();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            myColor = bundle.getInt(INSTANCE_MY_COLOR);
            oppoColor = bundle.getInt(INSTANCE_OPPO_COLOR);
            if (myColor == BLACK_COLOR) {
                myBitmap = mBlackPiece;
                oppoBitmap = mWhitePiece;
            } else {
                myBitmap = mWhitePiece;
                oppoBitmap = mBlackPiece;
            }
            isGameStarted = bundle.getBoolean(INSTANCE_GAME_OVER);
            isMyTurn = bundle.getBoolean(INSTANCE_IS_MY_TURN);
            canUndoTime = bundle.getInt(INSTANCE_CAN_UNDO_TIME);

            if (bundle.containsKey(INSTANCE_SELECTED_POINT)) {

                String selectedPointJson = bundle.getString(INSTANCE_SELECTED_POINT);
                try {
                    JSONObject selectedPointObj = new JSONObject(selectedPointJson);
                    String xStr = selectedPointObj.getString("x");
                    String yStr = selectedPointObj.getString("y");
                    selectedPoint = new MoonPoint(Float.valueOf(xStr), Float.valueOf(yStr), selectedPointObj.getInt("pointFlag"), selectedPointObj.getInt("color"));
                } catch (JSONException e) {
                    LogUtil.i(TAG, "parse selectedPointJson error:" + e.getMessage());
                }
            }

            String lastChessMoveJson = bundle.getString(INSTANCE_LAST_MOVE);
            try {
                JSONObject lastChessMoveObj = new JSONObject(lastChessMoveJson);
                lastChessMove = new MoonRecord();
                lastChessMove.fromF = lastChessMoveObj.getInt("fromF");
                lastChessMove.toF = lastChessMoveObj.getInt("toF");
            } catch (JSONException e) {
                LogUtil.i(TAG, "parse lastChessMoveJson error:" + e.getMessage());
            }

            String moonPointsColorJson = bundle.getString(INSTANCE_POINTS_COLOR);
            try {
                JSONArray moonPointsColorArray = new JSONArray(moonPointsColorJson);
                for (int i = 0, len = moonPointsColorArray.length(); i < len; i++) {
                    mMoonPoints[i].color = moonPointsColorArray.getInt(i);
                }
            } catch (JSONException e) {
                LogUtil.i(TAG, "parse moonPointsColorJson error:" + e.getMessage());
            }

            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
}
