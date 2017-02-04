package com.ue.gobang.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.hyphenate.easeui.game.GameConstants;
import com.hyphenate.easeui.game.base.BaseGameView;
import com.hyphenate.easeui.game.iterface.OnPlayListener;
import com.ue.common.util.DisplayUtil;
import com.ue.gobang.R;
import com.ue.gobang.util.GobangUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Reference:
 * Author:
 * Date:2016/8/5.
 * 棋盘view
 */
public class GobangView extends BaseGameView {
    private Paint mPaint;
    private float mPanelWidth;
    private float mSquareWidth;//棋盘格子宽高
    private Bitmap background;
    private Bitmap mWhitePiece;
    private Bitmap mBlackPiece;

    private Bitmap mBlackMovingPiece;
    private ArrayList<Point> oppoChesses;
    private ArrayList<Point> myChesses;
    private boolean isMyChessBlack = false;
    private Point movingChessPoint;
    private boolean isMoving;//根据这个标识判断是否是滑动下子，点击不能下子

    public GobangView(Context context) {
        this(context, null, 0);
    }

    public GobangView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GobangView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);//抖动处理，平滑处理
        mPaint.setStrokeWidth(2f);

        oppoChesses = new ArrayList<>();
        myChesses = new ArrayList<>();

        mPanelWidth = DisplayUtil.getScreenWidth(context) * 0.95f;
//        mPanelHeight=mPanelWidth;
//        LogUtil.i(TAG,"mPanelWidth="+mPanelWidth);
//        mPanelWidth=729.6
        mSquareWidth = mPanelWidth / 15;

        int pieceWidth = (int) (mSquareWidth * 0.75f);
        background = BitmapFactory.decodeResource(getResources(), R.drawable.game_bg_mood);
        background = Bitmap.createScaledBitmap(background, (int) mPanelWidth, (int) mPanelWidth, false);
        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.game_black1);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, pieceWidth, pieceWidth, false);
        mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.game_white1);
        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, pieceWidth, pieceWidth, false);
        mBlackMovingPiece = BitmapFactory.decodeResource(getResources(), R.drawable.game_black7);
        mBlackMovingPiece = Bitmap.createScaledBitmap(mBlackMovingPiece, pieceWidth, pieceWidth, false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) mPanelWidth, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) mPanelWidth, MeasureSpec.EXACTLY);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public ArrayList<Point> getMyChesses() {
        return myChesses;
    }

    public ArrayList<Point> getOppoChesses() {
        return oppoChesses;
    }

    public void drawPieces(Canvas canvas) {
        if (null != movingChessPoint) {
            canvas.drawBitmap(mBlackMovingPiece, (movingChessPoint.x + 0.125f) * mSquareWidth, (movingChessPoint.y + 0.125f) * mSquareWidth, null);
        }
        Point tempPoint;
        Bitmap pieceBitmap;
        pieceBitmap = isMyChessBlack ? mWhitePiece : mBlackPiece;
        for (int i = 0, num = oppoChesses.size(); i < num; i++) {
            tempPoint = oppoChesses.get(i);
            canvas.drawBitmap(pieceBitmap, (tempPoint.x + 0.125f) * mSquareWidth, (tempPoint.y + 0.125f) * mSquareWidth, null);
        }
        pieceBitmap = isMyChessBlack ? mBlackPiece : mWhitePiece;
        for (int i = 0, num = myChesses.size(); i < num; i++) {
            tempPoint = myChesses.get(i);
            canvas.drawBitmap(pieceBitmap, (tempPoint.x + 0.125f) * mSquareWidth, (tempPoint.y + 0.125f) * mSquareWidth, null);
        }
        //最后下的一只棋要特殊标注
        mPaint.setStyle(Paint.Style.FILL);
        if (isMyTurn && oppoChesses.size() > 0) {
            Point lastPoint = oppoChesses.get(oppoChesses.size() - 1);
            mPaint.setColor(Color.BLUE);
            canvas.drawCircle((lastPoint.x + 0.5f) * mSquareWidth, (lastPoint.y + 0.5f) * mSquareWidth, 3 * mSquareWidth / 16, mPaint);
        } else if (!isMyTurn && myChesses.size() > 0) {
            Point lastPoint = myChesses.get(myChesses.size() - 1);
            mPaint.setColor(Color.BLUE);
            canvas.drawCircle((lastPoint.x + 0.5f) * mSquareWidth, (lastPoint.y + 0.5f) * mSquareWidth, 3 * mSquareWidth / 16, mPaint);
        }
    }

    @Override
    public void undoOnce(boolean isMyUndo) {
        List<Point> targetChess = isMyUndo ? myChesses : oppoChesses;
        int chessSize = targetChess.size();
        if (chessSize == 0) {
            return;
        }
        if (null != mOnUndoListener) {
            Point undoPoint = targetChess.get(chessSize - 1);
            mOnUndoListener.onUndo(new int[]{undoPoint.x, undoPoint.y});
        }
        targetChess.remove(chessSize - 1);
        isMyTurn = !isMyTurn;
        canUndoTime--;
    }

    public void drawBoard(Canvas canvas) {
        canvas.drawBitmap(background, 0, 0, null);

        mPaint.setColor(0x88000000);
        mPaint.setStyle(Paint.Style.STROKE);//描边

        float halfSquareWidth = mSquareWidth / 2;
        float endWidth = mPanelWidth - halfSquareWidth;
        float tempFloat;
        for (int i = 0; i < 15; i++) {
            tempFloat = (i + 0.5f) * mSquareWidth;
            //横线
            canvas.drawLine(halfSquareWidth, tempFloat, endWidth, tempFloat, mPaint);
            //纵线
            canvas.drawLine(tempFloat, halfSquareWidth, tempFloat, endWidth, mPaint);
        }
        //中间和四周的小黑点
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLACK);
        canvas.drawCircle(7.5f * mSquareWidth, 7.5f * mSquareWidth, 6, mPaint);//中心点
        canvas.drawCircle(3.5f * mSquareWidth, 3.5f * mSquareWidth, 6, mPaint);//上左
        canvas.drawCircle(11.5f * mSquareWidth, 3.5f * mSquareWidth, 6, mPaint);//上右
        canvas.drawCircle(3.5f * mSquareWidth, 11.5f * mSquareWidth, 6, mPaint);//下左
        canvas.drawCircle(11.5f * mSquareWidth, 11.5f * mSquareWidth, 6, mPaint);//下右
    }

    //落子方式：滑动落子
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isGameStarted) {
            return true;
        }
        if (gameMode != GameConstants.MODE_DOUBLE && !isMyTurn) {
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            isMoving = false;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (isMoving) {
                Point newPoint = new Point(movingChessPoint.x, movingChessPoint.y);
                isMoving = false;
                movingChessPoint = null;
                if (oppoChesses.contains(newPoint) || myChesses.contains(newPoint)) {
                    //enableRender();
                    return true;
                }
                playChess(new int[]{newPoint.x, newPoint.y});
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            isMoving = true;
            //显示移动中的棋子的图片
            int pointX = (int) (event.getX() / mSquareWidth);
            int pointY = (int) (event.getY() / mSquareWidth) - 3;
            if (pointY >= 0 && pointY <= (15 - 1)) {
                movingChessPoint = new Point(pointX, pointY);
                //enableRender();
            } else {
                isMoving = false;
                movingChessPoint = null;
                //enableRender();
            }
        }
        return true;//返回true表示处理该事件
    }

    public void playChess(int[] data) {
        if (isMyTurn) {
            isMyTurn = false;
            myChesses.add(new Point(data[0], data[1]));
            canUndoTime++;
            //enableRender();

            if (GobangUtil.checkFiveInLine(myChesses)) {
                isGameStarted = false;
                mOnPlayListener.onGameOver(OnPlayListener.FLAG_I_WIN);
            }

            if (myChesses.size() + oppoChesses.size() == 225) {//棋子下满了棋盘
                isGameStarted = false;
                mOnPlayListener.onGameOver(OnPlayListener.FLAG_DRAW);
            }
            mOnPlayListener.onIPlayed(data);
        } else {
            isMyTurn = true;
            oppoChesses.add(new Point(data[0], data[1]));
            canUndoTime++;
            //enableRender();

            if (GobangUtil.checkFiveInLine(oppoChesses)) {
                isGameStarted = false;
                mOnPlayListener.onGameOver(OnPlayListener.FLAG_OPPO_WIN);
            }
            if (myChesses.size() + oppoChesses.size() == 225) {//棋子下满了棋盘
                isGameStarted = false;
                mOnPlayListener.onGameOver(OnPlayListener.FLAG_DRAW);
            }
            mOnPlayListener.onOppoPlayed(data);
        }
    }

    public void initChessBoard(boolean isIFirst) {
        myChesses.clear();
        oppoChesses.clear();
        //enableRender();
    }

    public void startGame(boolean isIRunFirst) {
        initChessBoard(isIRunFirst);
        isMyChessBlack = isIRunFirst;
        isMyTurn = isIRunFirst;
        isGameStarted = true;
        canUndoTime = 0;
    }

    private static final String INSTANCE = "instance";
    private static final String INSTANCE_GAME_OVER = "aIsGameStarted";
    private static final String INSTANCE_OPPO_ARRAY = "oppoChesses";
    private static final String INSTANCE_MY_ARRAY = "myChesses";
    private static final String INSTANCE_IS_MY_TURN = "isMyChessWhite";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE, super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER, isGameStarted);
        bundle.putParcelableArrayList(INSTANCE_OPPO_ARRAY, oppoChesses);
        bundle.putParcelableArrayList(INSTANCE_MY_ARRAY, myChesses);
        bundle.putBoolean(INSTANCE_IS_MY_TURN, isMyTurn);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            isGameStarted = bundle.getBoolean(INSTANCE_GAME_OVER);
            oppoChesses = bundle.getParcelableArrayList(INSTANCE_OPPO_ARRAY);
            myChesses = bundle.getParcelableArrayList(INSTANCE_MY_ARRAY);
            isMyTurn = bundle.getBoolean(INSTANCE_IS_MY_TURN);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
}
