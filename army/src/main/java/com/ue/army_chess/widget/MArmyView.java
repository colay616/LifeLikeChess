package com.ue.army_chess.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.hyphenate.easeui.game.GameConstants;
import com.hyphenate.easeui.game.base.BaseGameView;
import com.hyphenate.easeui.game.db.DBConstants;
import com.hyphenate.easeui.game.db.GameDbManager;
import com.hyphenate.easeui.game.iterface.OnPlayListener;
import com.ue.army_chess.R;
import com.ue.army_chess.entity.FMArmyChess;
import com.ue.army_chess.entity.FMArmyRecord;
import com.ue.army_chess.entity.FMRecordParser;
import com.ue.army_chess.util.ArmyChessUtil;
import com.ue.army_chess.util.VectorDrawableUtil;
import com.ue.common.util.DisplayUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by hawk on 2016/12/17.
 */

/**
 * 明棋
 */
public class MArmyView extends BaseGameView{
    private float mPanelWidth;
    private float mPanelHeight;
    private float mSquareWidth;
    private float mSquareHeight;
    //    private float mChessWidth;
//    private float mChessHeight;
    private float halfSquareWidth;
    private float halfSquareHeight;
    private Paint tempPaint;
    private Paint tempTxtPaint;
    private Bitmap railwayBitmapH;
    private Bitmap railwayBitmapVM;
    private Bitmap railwayBitmapV;
    private Bitmap background;
    private FMArmyChess[][] mArmyChesses;
    private FMArmyChess selectedArmyChess;
    private int myColor;
    private int oppoColor;
    private FMArmyRecord lastChessMove;
    private List<Integer> chessList = new ArrayList<>();

    public MArmyView(Context context) {
        super(context,null,0);
    }

    public MArmyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MArmyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        tempPaint = new Paint();
        tempPaint.setAntiAlias(true);
        tempPaint.setDither(true);//抖动处理，平滑处理
        tempPaint.setStrokeWidth(4f);

        tempTxtPaint = new Paint();
        tempTxtPaint.setAntiAlias(true);
        tempTxtPaint.setDither(true);//抖动处理，平滑处理
        tempTxtPaint.setTextSize(DisplayUtil.sp2px(getContext(), 19f));
        tempTxtPaint.setColor(Color.parseColor("#ffffff"));
        Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        tempTxtPaint.setTypeface(font);

        mPanelWidth=DisplayUtil.getScreenWidth(context)*0.95f;
        mPanelHeight = DisplayUtil.getScreenHeight(context)*0.7f;

        mSquareWidth = mPanelWidth * 0.2f;
        mSquareHeight = mPanelHeight * 1f / 13;
        halfSquareWidth = 0.5f * mSquareWidth;
        halfSquareHeight = 0.5f * mSquareHeight;
//        mChessWidth=0.8f*mSquareWidth;
//        mChessHeight=0.8f*mSquareHeight;

        railwayBitmapH = VectorDrawableUtil.getBitmapFromVectorDrawable(getContext(), R.drawable.svg_railway_h, (int) (4f * mSquareWidth), (int) (0.2f * mSquareHeight));
        railwayBitmapVM = VectorDrawableUtil.getBitmapFromVectorDrawable(getContext(), R.drawable.svg_railway_v_m, (int) (0.12f * mSquareWidth), (int) (2f * mSquareHeight));
        railwayBitmapV = VectorDrawableUtil.getBitmapFromVectorDrawable(getContext(), R.drawable.svg_railway_v, (int) (0.12f * mSquareWidth), (int) (10f * mSquareHeight + 0.1f * mSquareWidth));
        background = BitmapFactory.decodeResource(getResources(), R.drawable.game_bg_mood);
        background = Bitmap.createScaledBitmap(background, (int)mPanelWidth, (int)mPanelHeight, false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) mPanelWidth, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) mPanelHeight, MeasureSpec.EXACTLY);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    
    public void drawPieces(Canvas canvas) {
        if (null != mArmyChesses) {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 12; j++) {
                    if (null == mArmyChesses[i][j]) {
                        continue;
                    }
                    drawPiece(canvas, mArmyChesses[i][j]);
                }
            }
            if (null != selectedArmyChess) {
                tempPaint.setColor(Color.parseColor("#66CC99"));
                canvas.drawRect((selectedArmyChess.x + 0.1f) * mSquareWidth, (selectedArmyChess.getBoardY() + 0.1f) * mSquareHeight, (selectedArmyChess.x + 0.9f) * mSquareWidth, (selectedArmyChess.getBoardY() + 0.9f) * mSquareHeight, tempPaint);
                canvas.drawText(selectedArmyChess.txt(), (selectedArmyChess.x + 0.22f) * mSquareWidth, (selectedArmyChess.getBoardY() + 0.66f) * mSquareHeight, tempTxtPaint);
            }
            if (null != lastChessMove) {
                tempPaint.setStyle(Paint.Style.STROKE);
                tempPaint.setColor(Color.parseColor("#9b73e6"));
                int xx = lastChessMove.fromX;
                int yy = lastChessMove.fromBY();
                canvas.drawRect((xx + 0.1f) * mSquareWidth, (yy + 0.1f) * mSquareHeight, (xx + 0.9f) * mSquareWidth, (yy + 0.9f) * mSquareHeight, tempPaint);

                tempPaint.setColor(Color.GREEN);
                xx = lastChessMove.toX;
                yy = lastChessMove.toBY();
                canvas.drawRect((xx + 0.1f) * mSquareWidth, (yy + 0.1f) * mSquareHeight, (xx + 0.9f) * mSquareWidth, (yy + 0.9f) * mSquareHeight, tempPaint);
            }
        }
    }

    private void drawPiece(Canvas canvas, FMArmyChess armyChess) {
        //以下一部分后面省略
        if (ArmyChessUtil.canGo(armyChess.s)) {
            tempPaint.setColor(Color.parseColor("#BABABA"));
            canvas.drawRect((armyChess.x + 0.1f) * mSquareWidth, (armyChess.getBoardY() + 0.1f) * mSquareHeight, (armyChess.x + 0.9f) * mSquareWidth, (armyChess.getBoardY() + 0.9f) * mSquareHeight, tempPaint);
            canvas.drawText(armyChess.txt(), (armyChess.x + 0.22f) * mSquareWidth, (armyChess.getBoardY() + 0.66f) * mSquareHeight, tempTxtPaint);
            return;
        }
        if (armyChess.s == ArmyChessUtil.STAT_NORMAL) {
            tempPaint.setColor(armyChess.c);
            canvas.drawRect((armyChess.x + 0.1f) * mSquareWidth, (armyChess.getBoardY() + 0.1f) * mSquareHeight, (armyChess.x + 0.9f) * mSquareWidth, (armyChess.getBoardY() + 0.9f) * mSquareHeight, tempPaint);
            canvas.drawText(armyChess.txt(), (armyChess.x + 0.22f) * mSquareWidth, (armyChess.getBoardY() + 0.66f) * mSquareHeight, tempTxtPaint);
        }
    }

    public void drawBoard(Canvas canvas) {
        tempPaint.setStyle(Paint.Style.FILL);
        canvas.drawBitmap(background, 0, 0, null);

        tempPaint.setColor(Color.parseColor("#2F6CA5"));
        //横线
        int[] lines = new int[]{0, 2, 3, 4, 8, 9, 10, 12};
        for (int i = 0, len = lines.length; i < len; i++) {
            canvas.drawLine(halfSquareWidth, halfSquareHeight + mSquareHeight * lines[i], mPanelWidth - halfSquareWidth, halfSquareHeight + mSquareHeight * lines[i], tempPaint);
        }
        //竖线
        for (int i = 0; i < 5; i++) {
            canvas.drawLine(halfSquareWidth + mSquareWidth * i, halfSquareHeight, halfSquareWidth + mSquareWidth * i, halfSquareHeight + 5 * mSquareHeight, tempPaint);
            canvas.drawLine(halfSquareWidth + mSquareWidth * i, halfSquareHeight + mSquareHeight * 7, halfSquareWidth + mSquareWidth * i, mPanelHeight - halfSquareHeight, tempPaint);
        }
        //斜线,above
        canvas.drawLine(halfSquareWidth, 1.5f * mSquareHeight, mPanelWidth - halfSquareWidth, 5.5f * mSquareHeight, tempPaint);
        canvas.drawLine(halfSquareWidth, 5.5f * mSquareHeight, mPanelWidth - halfSquareWidth, 1.5f * mSquareHeight, tempPaint);
        canvas.drawLine(halfSquareWidth, 3.5f * mSquareHeight, 2.5f * mSquareWidth, 1.5f * mSquareHeight, tempPaint);
        canvas.drawLine(halfSquareWidth, 3.5f * mSquareHeight, 2.5f * mSquareWidth, 5.5f * mSquareHeight, tempPaint);
        canvas.drawLine(2.5f * mSquareWidth, 1.5f * mSquareHeight, mPanelWidth - halfSquareWidth, 3.5f * mSquareHeight, tempPaint);
        canvas.drawLine(2.5f * mSquareWidth, 5.5f * mSquareHeight, mPanelWidth - halfSquareWidth, 3.5f * mSquareHeight, tempPaint);
        //斜线,below
        canvas.drawLine(halfSquareWidth, 7.5f * mSquareHeight, mPanelWidth - halfSquareWidth, 11.5f * mSquareHeight, tempPaint);
        canvas.drawLine(halfSquareWidth, 11.5f * mSquareHeight, mPanelWidth - halfSquareWidth, 7.5f * mSquareHeight, tempPaint);
        canvas.drawLine(halfSquareWidth, 9.5f * mSquareHeight, 2.5f * mSquareWidth, 7.5f * mSquareHeight, tempPaint);
        canvas.drawLine(halfSquareWidth, 9.5f * mSquareHeight, 2.5f * mSquareWidth, 11.5f * mSquareHeight, tempPaint);
        canvas.drawLine(2.5f * mSquareWidth, 7.5f * mSquareHeight, mPanelWidth - halfSquareWidth, 9.5f * mSquareHeight, tempPaint);
        canvas.drawLine(2.5f * mSquareWidth, 11.5f * mSquareHeight, mPanelWidth - halfSquareWidth, 9.5f * mSquareHeight, tempPaint);

        //railway horizontal
        canvas.drawBitmap(railwayBitmapH, halfSquareWidth, 1.5f * mSquareHeight, null);
        canvas.drawBitmap(railwayBitmapH, halfSquareWidth, 5.5f * mSquareHeight, null);
        canvas.drawBitmap(railwayBitmapH, halfSquareWidth, 7.5f * mSquareHeight, null);
        canvas.drawBitmap(railwayBitmapH, halfSquareWidth, 11.5f * mSquareHeight, null);
        //railway vertical middle
        canvas.drawBitmap(railwayBitmapVM, 2.44f * mSquareWidth, 5.5f * mSquareHeight, null);
        //railway vertical sides
        canvas.drawBitmap(railwayBitmapV, 0.44f * mSquareWidth, 1.5f * mSquareHeight, null);
        canvas.drawBitmap(railwayBitmapV, 4.44f * mSquareWidth, 1.5f * mSquareHeight, null);

        //camp above
        Bitmap soldierCampBitmap = VectorDrawableUtil.getBitmapFromVectorDrawable(getContext(), R.drawable.svg_camp, (int) mSquareHeight, (int) mSquareHeight);
        canvas.drawBitmap(soldierCampBitmap, 1.5f * mSquareWidth - halfSquareHeight, 2f * mSquareHeight, null);
        canvas.drawBitmap(soldierCampBitmap, 3.5f * mSquareWidth - halfSquareHeight, 2f * mSquareHeight, null);
        canvas.drawBitmap(soldierCampBitmap, 2.5f * mSquareWidth - halfSquareHeight, 3f * mSquareHeight, null);
        canvas.drawBitmap(soldierCampBitmap, 1.5f * mSquareWidth - halfSquareHeight, 4f * mSquareHeight, null);
        canvas.drawBitmap(soldierCampBitmap, 3.5f * mSquareWidth - halfSquareHeight, 4f * mSquareHeight, null);
        //camp below
        canvas.drawBitmap(soldierCampBitmap, 1.5f * mSquareWidth - halfSquareHeight, 8f * mSquareHeight, null);
        canvas.drawBitmap(soldierCampBitmap, 3.5f * mSquareWidth - halfSquareHeight, 8f * mSquareHeight, null);
        canvas.drawBitmap(soldierCampBitmap, 2.5f * mSquareWidth - halfSquareHeight, 9f * mSquareHeight, null);
        canvas.drawBitmap(soldierCampBitmap, 1.5f * mSquareWidth - halfSquareHeight, 10f * mSquareHeight, null);
        canvas.drawBitmap(soldierCampBitmap, 3.5f * mSquareWidth - halfSquareHeight, 10f * mSquareHeight, null);

        //base camp,above
        Bitmap baseCampBitmap = VectorDrawableUtil.getBitmapFromVectorDrawable(getContext(), R.drawable.svg_base_camp_oppo, (int) mSquareWidth, (int) (mSquareHeight + 0.4f * mSquareHeight));
        canvas.drawBitmap(baseCampBitmap, mSquareWidth, halfSquareHeight - 0.4f * mSquareHeight, null);
        canvas.drawText("大本营", 1.1f * mSquareWidth, halfSquareHeight + 0.16f * mSquareHeight, tempTxtPaint);
        canvas.drawBitmap(baseCampBitmap, 3f * mSquareWidth, halfSquareHeight - 0.4f * mSquareHeight, null);
        canvas.drawText("大本营", 3.1f * mSquareWidth, halfSquareHeight + 0.16f * mSquareHeight, tempTxtPaint);
        //base camp,below
        baseCampBitmap = VectorDrawableUtil.getBitmapFromVectorDrawable(getContext(), R.drawable.svg_base_camp_mine, (int) mSquareWidth, (int) (mSquareHeight + 0.4f * mSquareHeight));
        canvas.drawBitmap(baseCampBitmap, mSquareWidth, mPanelHeight - 1.5f * mSquareHeight, null);
        canvas.drawText("大本营", 1.1f * mSquareWidth, mPanelHeight - 0.25f * mSquareHeight, tempTxtPaint);
        canvas.drawBitmap(baseCampBitmap, 3f * mSquareWidth, mPanelHeight - 1.5f * mSquareHeight, null);
        canvas.drawText("大本营", 3.1f * mSquareWidth, mPanelHeight - 0.25f * mSquareHeight, tempTxtPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            if (!isGameStarted) {
                return true;
            }
            if (gameMode!=GameConstants.MODE_DOUBLE&&!isMyTurn) {
                return true;
            }
            int x = (int) (event.getX() / mSquareWidth);
            int y = (int) (event.getY() / mSquareHeight);
            if (y == 6) {//纵坐标为6时忽略
                return true;
            }
            y = y > 6 ? y - 1 : y;//将board坐标转为array下标
            if (null == mArmyChesses[x][y]) {
                selectedArmyChess = null;
                resetStatus(true);
                return true;
            }
            if (null == selectedArmyChess) {
                //地雷和军旗不能被选中
                if (mArmyChesses[x][y].getF() == 9 || mArmyChesses[x][y].getF() == 11) {
                    return true;
                }
                //不能走对方的棋子
                if(isMyTurn){
                    if(myColor!=mArmyChesses[x][y].c){
                        return true;
                    }
                }else{
                    if(oppoColor!=mArmyChesses[x][y].c){
                        return true;
                    }
                }
                selectedArmyChess = mArmyChesses[x][y];
                processValidMoves(selectedArmyChess);
                //enableRender();
            } else {
                if (!selectedArmyChess.equals(mArmyChesses[x][y])) {
                    if (ArmyChessUtil.canGo(mArmyChesses[x][y].s)) {
                        playChess(new int[]{x, y});
                    } else {
                        //选中的不是己方的棋子
                        if ((isMyTurn && mArmyChesses[x][y].c != myColor) || (!isMyTurn && mArmyChesses[x][y].c != oppoColor)) {
                            selectedArmyChess = null;
                            resetStatus(true);
                            return true;
                        }
                        //地雷和军旗不能被选中
                        if (mArmyChesses[x][y].c != myColor || mArmyChesses[x][y].getF() == 9 || mArmyChesses[x][y].getF() == 11) {
                            selectedArmyChess = null;
                            resetStatus(true);
                            return true;
                        }
                        selectedArmyChess = mArmyChesses[x][y];
                        resetStatus(false);
                        processValidMoves(selectedArmyChess);
                        //enableRender();
                    }
                }
            }
        }
        return true;
    }

    private void processValidMoves(FMArmyChess armyChess) {
        boolean isHorizontallyOnRailway = isHorizontallyOnRailway(armyChess);
        boolean isVerticallyOnRailway = isVerticallyOnRailway(armyChess);
        //on railway
        if (isHorizontallyOnRailway || isVerticallyOnRailway) {
            if (armyChess.getF() == 0) {//soldier
                changeLeftSideStatus(armyChess, true);
                changeRightSideStatus(armyChess, true);
                changeAboveSideStatus(armyChess, true);
                changeBelowSideStatus(armyChess, true);
                changeSubSidesStatus(armyChess);
                //extra
                if (armyChess.y != 5) {
                    if (null != mArmyChesses[0][5] && mArmyChesses[0][5].s == ArmyChessUtil.STAT_CAN_GO) {
                        changeRightSideStatus(mArmyChesses[0][5], true);
                    }
                    if (null != mArmyChesses[4][5] && mArmyChesses[4][5].s == ArmyChessUtil.STAT_CAN_GO) {
                        changeLeftSideStatus(mArmyChesses[4][5], true);
                    }
                }
                if (armyChess.y != 6) {
                    if (null != mArmyChesses[0][6] && mArmyChesses[0][6].s == ArmyChessUtil.STAT_CAN_GO) {
                        changeRightSideStatus(mArmyChesses[0][6], true);
                    }
                    if (null != mArmyChesses[4][6] && mArmyChesses[4][6].s == ArmyChessUtil.STAT_CAN_GO) {
                        changeLeftSideStatus(mArmyChesses[4][6], true);
                    }
                }
                if (null != mArmyChesses[2][5] && (armyChess == mArmyChesses[2][5] || mArmyChesses[2][5].s == ArmyChessUtil.STAT_CAN_GO)) {
                    if (changeStatus(armyChess, 2, 6)) {
                        changeLeftSideStatus(mArmyChesses[2][6], false);
                        changeRightSideStatus(mArmyChesses[2][6], false);
                    }
                }
                if (null != mArmyChesses[2][6] && (armyChess == mArmyChesses[2][6] || mArmyChesses[2][6].s == ArmyChessUtil.STAT_CAN_GO)) {
                    if (changeStatus(armyChess, 2, 5)) {
                        changeLeftSideStatus(mArmyChesses[2][5], false);
                        changeRightSideStatus(mArmyChesses[2][5], false);
                    }
                }
                //four corners
                changeCornersStatus(armyChess, 0, 0);
                changeCornersStatus(armyChess, 4, 0);
                changeCornersStatus(armyChess, 0, 11);
                changeCornersStatus(armyChess, 4, 11);
            } else {//not soldier
                changeLeftSideStatus(armyChess, false);
                changeRightSideStatus(armyChess, false);
                changeAboveSideStatus(armyChess, false);
                changeBelowSideStatus(armyChess, false);
                changeSubSidesStatus(armyChess);
            }
        } else {//not on railway
//            LogUtil.i(TAG,"not on railway");
            //simple process
            changeAboveStatus(armyChess);
            changeBelowStatus(armyChess);
            //left
            if (armyChess.x > 0) {
                changeStatus(armyChess, armyChess.x - 1, armyChess.y);
            }
            //right
            if (armyChess.x < 4) {
                changeStatus(armyChess, armyChess.x + 1, armyChess.y);
            }
            changeSubSidesStatus(armyChess);
        }
    }

    private void changeCornersStatus(FMArmyChess armyChess, int x, int y) {
        if (null != mArmyChesses[x][y] && (mArmyChesses[x][y].s == ArmyChessUtil.STAT_CAN_GO || mArmyChesses[x][y].s == ArmyChessUtil.STAT_CAN_EAT)) {
            if (armyChess != mArmyChesses[x][y > 5 ? y - 1 : y + 1]) {
                if (mArmyChesses[x][y].s == ArmyChessUtil.STAT_CAN_GO) {
                    mArmyChesses[x][y] = null;
                } else {
                    mArmyChesses[x][y].s = ArmyChessUtil.STAT_NORMAL;
                }
            }
        }
    }

    private void changeAboveStatus(FMArmyChess armyChess) {
//        LogUtil.i(TAG,"changeAboveStatus");
        if (armyChess.y != 6 || armyChess.x == 2) {
            if (armyChess.y > 0) {
                int aboveY = armyChess.y - 1;
                changeStatus(armyChess, armyChess.x, aboveY);
            }
        }
    }

    private void changeBelowStatus(FMArmyChess armyChess) {
//        LogUtil.i(TAG,"changeBelowStatus");
        if (armyChess.y != 5 || armyChess.x == 2) {
            if (armyChess.y < 11) {
                int belowY = armyChess.y + 1;
                changeStatus(armyChess, armyChess.x, belowY);
            }
        }
    }

    /**
     * @param armyChess
     * @return false:meet obstacles on left side
     */
    private boolean changeLeftSideStatus(FMArmyChess armyChess, boolean isSoldier) {
//        LogUtil.i(TAG,"changeLeftSideStatus");
        int y = armyChess.y;
        if (!isHorizontallyOnRailway(armyChess) && isVerticallyOnRailway(armyChess)) {
            if (armyChess.x == 0) {
                return true;
            }
            changeStatus(armyChess, armyChess.x - 1, y);
            return false;
        }
        for (int i = armyChess.x - 1; i >= 0; i--) {
            if (!changeStatus(armyChess, i, y)) {
                //armyChess.c == mArmyChesses[i][armyChess.y].c || armyChess.f < mArmyChesses[i][armyChess.y].f
                return false;
            }
        }
        if (isSoldier) {
            changeAboveSideStatus(mArmyChesses[0][y], isSoldier);
            changeBelowSideStatus(mArmyChesses[0][y], isSoldier);
        }
        return true;
    }

    private boolean changeRightSideStatus(FMArmyChess armyChess, boolean isSoldier) {
//        LogUtil.i(TAG,"changeRightSideStatus");
        int y = armyChess.y;
        if (!isHorizontallyOnRailway(armyChess) && isVerticallyOnRailway(armyChess)) {
            if (armyChess.x == 4) {
                return true;
            }
            changeStatus(armyChess, armyChess.x + 1, y);
            return false;
        }
        for (int i = armyChess.x + 1; i < 5; i++) {
            if (!changeStatus(armyChess, i, y)) {
                return false;
            }
        }
        if (isSoldier) {
            changeAboveSideStatus(mArmyChesses[4][y], isSoldier);
            changeBelowSideStatus(mArmyChesses[4][y], isSoldier);
        }
        return true;
    }

    //for chess on railway
    private boolean changeAboveSideStatus(FMArmyChess armyChess, boolean isSoldier) {
        if (isHorizontallyOnRailway(armyChess) && !isVerticallyOnRailway(armyChess)) {
            changeAboveStatus(armyChess);
            return false;
        }
        if (armyChess.y == 1) {
            changeStatus(armyChess, armyChess.x, 0);
            return true;
        }
        for (int i = armyChess.y - 1; i > 0; i--) {
            if (!changeStatus(armyChess, armyChess.x, i)) {
                return false;
            }
        }
        if (isSoldier) {
            changeLeftSideStatus(mArmyChesses[armyChess.x][1], isSoldier);
            changeRightSideStatus(mArmyChesses[armyChess.x][1], isSoldier);
        }
        return true;
    }

    private boolean changeBelowSideStatus(FMArmyChess armyChess, boolean isSoldier) {
        if (isHorizontallyOnRailway(armyChess) && !isVerticallyOnRailway(armyChess)) {
            changeBelowStatus(armyChess);
            return false;
        }
        if (armyChess.y == 10) {
            changeStatus(armyChess, armyChess.x, 11);
            return true;
        }
        for (int i = armyChess.y + 1; i < 11; i++) {
            if (!changeStatus(armyChess, armyChess.x, i)) {
                return false;
            }
        }
        if (isSoldier) {
            changeLeftSideStatus(mArmyChesses[armyChess.x][10], isSoldier);
            changeRightSideStatus(mArmyChesses[armyChess.x][10], isSoldier);
        }
        return true;
    }

    private void changeSubSidesStatus(FMArmyChess armyChess) {
//        LogUtil.i(TAG, "changeSubSidesStatus");
        //no sub sides
        if (armyChess.y == 0 || armyChess.y == 11) {
            return;
        }
        if (armyChess.x % 2 == 0) {//偶数列
            if (armyChess.getBoardY() % 2 == 0) {//偶数行
                return;
            }
        } else {//基数列
            if (armyChess.getBoardY() % 2 != 0) {//基数行
                return;
            }
        }
        //has sub sides
//        String logTxt = String.format("x=%d,y=%d,is contained in campChess=%b", armyChess.x, armyChess.y, (isInCamps(armyChess.x, armyChess.y)));
//        LogUtil.i(TAG, logTxt);
        if (isInCamps(armyChess.x, armyChess.y)) {
            //left up
            changeStatus(armyChess, armyChess.x - 1, armyChess.y - 1);
            //left down
            changeStatus(armyChess, armyChess.x - 1, armyChess.y + 1);
            //right up
            changeStatus(armyChess, armyChess.x + 1, armyChess.y - 1);
            //right down
            changeStatus(armyChess, armyChess.x + 1, armyChess.y + 1);
        } else {
            //left up
            if (isInCamps(armyChess.x - 1, armyChess.y - 1)) {
                changeStatus(armyChess, armyChess.x - 1, armyChess.y - 1);
            }
            //left down
            if (isInCamps(armyChess.x - 1, armyChess.y + 1)) {
                changeStatus(armyChess, armyChess.x - 1, armyChess.y + 1);
            }
            //right up
            if (isInCamps(armyChess.x + 1, armyChess.y - 1)) {
                changeStatus(armyChess, armyChess.x + 1, armyChess.y - 1);
            }
            //right down
            if (isInCamps(armyChess.x + 1, armyChess.y + 1)) {
                changeStatus(armyChess, armyChess.x + 1, armyChess.y + 1);
            }
        }
    }

    //y以ArrayY为标准
    private boolean isInBaseCamps(int x, int y) {
        if (y != 0 && y != 11) {
            return false;
        }
        return (x == 1 || x == 3);
    }

    //y以ArrayY为标准
    private boolean isInCamps(int x, int y) {
        if (x == 1) {
            if (y == 2 || y == 4 || y == 7 || y == 9) {
                return true;
            }
            return false;
        }
        if (x == 2) {
            if (y == 3 || y == 8) {
                return true;
            }
            return false;
        }
        if (x == 3) {
            if (y == 2 || y == 4 || y == 7 || y == 9) {
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * @param armyChess
     * @param x         for mArmyChessArray
     * @param y         for mArmyChessArray
     * @return false:meet obstacles
     */
    //y以ArrayY为标准
    private boolean changeStatus(FMArmyChess armyChess, int x, int y) {
        if (null == mArmyChesses[x][y]) {
            mArmyChesses[x][y] = new FMArmyChess(armyChess.getF(), x, y, armyChess.c, ArmyChessUtil.STAT_CAN_GO);
        } else {
            if (ArmyChessUtil.canGo(mArmyChesses[x][y].s)) {
                return false;
            }
            //工兵扛旗
            if (armyChess.getF() == 0 && mArmyChesses[x][y].getF() == 11) {
                if (armyChess.c != mArmyChesses[x][y].c) {
                    mArmyChesses[x][y].s = ArmyChessUtil.STAT_CAN_EAT;
                }
                return false;
            }
            //不能吃大本营内的棋子
            if (isInBaseCamps(mArmyChesses[x][y].x, mArmyChesses[x][y].y)) {
                return false;
            }
            //不能吃兵营、大本营内的棋子
            if (isInCamps(mArmyChesses[x][y].x, mArmyChesses[x][y].y)) {
                return false;
            }
            if (armyChess.c != mArmyChesses[x][y].c) {
                //同归于尽:一方为炸弹/双方棋子相等/对方是暴露的地雷
                if ((armyChess.getF() == 10 || mArmyChesses[x][y].getF() == 10) ||
                        armyChess.getF() == mArmyChesses[x][y].getF() ||
                        (mArmyChesses[x][y].getF() == 9 && mArmyChesses[x][y].s == ArmyChessUtil.STAT_NORMAL)) {
                    if (mArmyChesses[x][y].getF() != 11) {
                        mArmyChesses[x][y].s = ArmyChessUtil.STAT_EQUAL;
                    }
                    return false;
                }
                if (armyChess.getF() == 0 && mArmyChesses[x][y].getF() == 9) {
                    //工兵-地雷
                    mArmyChesses[x][y].s = ArmyChessUtil.STAT_CAN_EAT;
                    return false;
                }
                if (armyChess.getF() > mArmyChesses[x][y].getF()) {
                    mArmyChesses[x][y].s = ArmyChessUtil.STAT_CAN_EAT;
                }
            }
            return false;
        }
        return true;
    }

    private boolean isHorizontallyOnRailway(FMArmyChess armyChess) {
        return (armyChess.y == 1 || armyChess.y == 5 || armyChess.y == 6 || armyChess.y == 10);
    }

    private boolean isVerticallyOnRailway(FMArmyChess armyChess) {
        return ((armyChess.x == 0 || armyChess.x == 4) && armyChess.y > 0 && armyChess.y < 11);
    }

    private void resetStatus(boolean isInvalidate) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 12; j++) {
                if (null == mArmyChesses[i][j]) {
                    continue;
                }
                if (mArmyChesses[i][j].s == ArmyChessUtil.STAT_CAN_GO) {
                    mArmyChesses[i][j] = null;
                    continue;
                }
                mArmyChesses[i][j].s = ArmyChessUtil.STAT_NORMAL;
            }
        }
        if (isInvalidate) {
            //enableRender();
        }
    }

    private int[] translateData(int[] data) {
        if (gameMode == GameConstants.MODE_ONLINE) {
            data[0] = 4 - data[0];
            data[1] = 11 - data[1];
            data[2] = 4 - data[2];
            data[3] = 11 - data[3];
        }
        selectedArmyChess = mArmyChesses[data[0]][data[1]];
        processValidMoves(selectedArmyChess);
        return new int[]{data[2], data[3]};
    }

    @Override
    public void playChess(int[] oData) {
        if (!isGameStarted) {
            return;
        }
        int winFlag = ArmyChessUtil.FLAG_NOT_OVER;
        int[] data;
        if (isMyTurn || gameMode == GameConstants.MODE_DOUBLE) {
            data = oData;
        } else {
            data = translateData(oData);
        }
        int fromX = selectedArmyChess.x;
        int fromY = selectedArmyChess.y;
        int toX = data[0];
        int toY = data[1];
        //save move record to db
        lastChessMove = new FMArmyRecord(selectedArmyChess, mArmyChesses[toX][toY]);
        boolean saveResult = GameDbManager.getInstance().saveChessRecord(DBConstants.TABLE_ARMY_M, lastChessMove.toString());
        if (saveResult) {
            canUndoTime++;
        }
        //save over
        if (mArmyChesses[toX][toY].s == ArmyChessUtil.STAT_EQUAL) {
            mArmyChesses[toX][toY] = null;
        }else if (mArmyChesses[toX][toY].s == ArmyChessUtil.STAT_CAN_GO) {
            mArmyChesses[toX][toY].setF(selectedArmyChess.getF());
            mArmyChesses[toX][toY].c = selectedArmyChess.c;
            mArmyChesses[toX][toY].s = selectedArmyChess.s;
        } else if (mArmyChesses[toX][toY].s == ArmyChessUtil.STAT_CAN_EAT) {
            //工兵扛旗，游戏结束
            if (mArmyChesses[fromX][fromY].getF() == 0 && mArmyChesses[toX][toY].getF() == 11) {
                winFlag = mArmyChesses[toX][toY].c == myColor ? OnPlayListener.FLAG_OPPO_WIN : OnPlayListener.FLAG_I_WIN;
            }
            mArmyChesses[toX][toY].setF(selectedArmyChess.getF());
            mArmyChesses[toX][toY].c = selectedArmyChess.c;
            mArmyChesses[toX][toY].s = selectedArmyChess.s;
        }

        mArmyChesses[fromX][fromY] = null;
        selectedArmyChess = null;
        resetStatus(true);

        if (isMyTurn) {
            isMyTurn = false;
            mOnPlayListener.onIPlayed(new int[]{fromX, fromY, toX, toY});
        } else {
            isMyTurn = true;
            mOnPlayListener.onOppoPlayed(new int[]{fromX, fromY, toX, toY});
        }
        if (winFlag != ArmyChessUtil.FLAG_NOT_OVER) {
            gameOver(winFlag);
            return;
        }
        winFlag = ArmyChessUtil.checkGameOver(mArmyChesses, myColor);
        if (winFlag != ArmyChessUtil.FLAG_NOT_OVER) {
            gameOver(winFlag);
        }
    }

    public void undoOnce(boolean isMyUndo) {
        Object[] lastTwoRecords = GameDbManager.getInstance().getLastTwoRecords(DBConstants.TABLE_ARMY_M, new FMRecordParser());
        if (null == lastTwoRecords) {
            return;
        }
        FMArmyRecord lastRecord = (FMArmyRecord) lastTwoRecords[0];
        if(lastRecord==null){
            return;
        }
        mArmyChesses[lastRecord.fromX][lastRecord.fromY] = lastRecord.getFrom();
        mArmyChesses[lastRecord.toX][lastRecord.toY] = lastRecord.getTo();
        GameDbManager.getInstance().deleteChessRecord(DBConstants.TABLE_ARMY_M, lastRecord.id);

        lastChessMove = (FMArmyRecord) lastTwoRecords[1];
        isMyTurn = !isMyTurn;
        canUndoTime--;
        resetStatus(true);
    }

    @Override
    public void initChessBoard(boolean isIFirst) {
        if (isIFirst) {
            myColor = Color.parseColor("#3264A1");
            oppoColor = Color.parseColor("#DB6F02");
        } else {
            myColor = Color.parseColor("#DB6F02");
            oppoColor = Color.parseColor("#3264A1");
        }
        if (gameMode == GameConstants.MODE_ONLINE) {
            mArmyChesses = null;
            //enableRender();
        }
    }

    public void setChessList(List<Integer> paramChessList, boolean isMyChessList) {
        if (isMyChessList) {
            chessList.addAll(paramChessList);
        } else {
            Collections.reverse(paramChessList);
            chessList.addAll(0, paramChessList);
        }
    }

    public void prepareChessBoard(boolean isIFirst) {
        mArmyChesses = new FMArmyChess[5][12];
        //添加10个空位置,方便遍历绘制棋子
        int[] indexes = new int[]{11, 13, 17, 21, 23, 36, 38, 42, 46, 48};
        for (int i = 0, len = indexes.length; i < len; i++) {
            chessList.add(indexes[i], -1);
        }
        int flagIndex = 0;
        int flag;
        for (int j = 0; j < 12; j++) {
            for (int i = 0; i < 5; i++) {
                flag = chessList.get(flagIndex);
                if (flag < 0) {
                    flagIndex++;
                    continue;
                }
                mArmyChesses[i][j] = new FMArmyChess(flag, i, j, j > 5 ? myColor : oppoColor, ArmyChessUtil.STAT_NORMAL);
                flagIndex++;
            }
        }
        //enableRender();
    }

    @Override
    public void startGame(boolean isIFirst) {
        GameDbManager.getInstance().clearChessRecord(DBConstants.TABLE_ARMY_M);
        lastChessMove = null;
        selectedArmyChess = null;
        initChessBoard(isIFirst);
        prepareChessBoard(isIFirst);
        isMyTurn = isIFirst;
        isGameStarted = true;
        canUndoTime = 0;
        chessList.clear();
    }
}
