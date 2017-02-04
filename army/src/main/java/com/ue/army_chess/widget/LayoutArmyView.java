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
import android.view.View;

import com.ue.army_chess.R;
import com.ue.army_chess.entity.FMArmyChess;
import com.ue.army_chess.util.ArmyChessUtil;
import com.ue.army_chess.util.VectorDrawableUtil;
import com.ue.common.util.DisplayUtil;
import com.ue.common.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hawk on 2016/12/17.
 */

/**
 * 军旗、地雷不能移动
 * 炸弹可以和棋子同归于尽，其它棋子亦可和炸弹同归于尽
 * <p>
 * 暗棋:若司令被吃，被吃的一方亮出军旗
 * 当地雷被工兵，炸弹以外的单位触发时，触发者阵亡，地雷翻面（对所有人展示身份）；若翻面地雷再次被工兵外的单位触发，则同归于尽。
 * 杀光对方所有能移动的棋子则获得胜利；或者用工兵挖掉对方地雷后再用本方的棋子吃掉对方的军旗，也能获得胜利。
 * <p>
 * 胜负：工兵扛旗/杀光可移动的棋子为赢
 * <p>
 * 参考其它游戏规则，腾讯天天军棋
 * <p>
 * 棋子标记:
 * 长按对方棋子显示标记面板
 */
public class LayoutArmyView extends View {
    private static final String TAG = LayoutArmyView.class.getSimpleName();
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
    private int myColor=Color.parseColor("#9b73e6");

    public LayoutArmyView(Context context) {
        this(context, null, 0);
    }

    public LayoutArmyView(Context context, AttributeSet attrs) {
        this(context, null, 0);
    }

    public LayoutArmyView(Context context, AttributeSet attrs, int defStyle) {
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

        mPanelWidth = DisplayUtil.getScreenWidth(context) * 0.95f;
        mPanelHeight = DisplayUtil.getScreenHeight(context)*0.7f;

        mSquareWidth = mPanelWidth * 0.2f;
        mSquareHeight = mPanelHeight * 1f / 13;
        halfSquareWidth = 0.5f * mSquareWidth;
        halfSquareHeight = 0.5f * mSquareHeight;

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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        tempPaint.setStyle(Paint.Style.FILL);
        drawBoard(canvas);
        drawPieces(canvas);
    }

    private void drawPieces(Canvas canvas) {
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
                tempPaint.setStyle(Paint.Style.STROKE);
                tempPaint.setColor(Color.GREEN);
                canvas.drawRect((selectedArmyChess.x + 0.1f) * mSquareWidth, (selectedArmyChess.getBoardY() + 0.1f) * mSquareHeight, (selectedArmyChess.x + 0.9f) * mSquareWidth, (selectedArmyChess.getBoardY() + 0.9f) * mSquareHeight, tempPaint);
            }
        }
    }

    private void drawPiece(Canvas canvas, FMArmyChess armyChess) {
        if (armyChess.s == ArmyChessUtil.STAT_UNKNOWN) {
            tempPaint.setColor(Color.parseColor("#719F02"));
            canvas.drawRect((armyChess.x + 0.1f) * mSquareWidth, (armyChess.getBoardY() + 0.1f) * mSquareHeight, (armyChess.x + 0.9f) * mSquareWidth, (armyChess.getBoardY() + 0.9f) * mSquareHeight, tempPaint);
            return;
        }
        if (armyChess.s == ArmyChessUtil.STAT_NORMAL) {
            tempPaint.setColor(armyChess.c);
            canvas.drawRect((armyChess.x + 0.1f) * mSquareWidth, (armyChess.getBoardY() + 0.1f) * mSquareHeight, (armyChess.x + 0.9f) * mSquareWidth, (armyChess.getBoardY() + 0.9f) * mSquareHeight, tempPaint);
            canvas.drawText(armyChess.txt(), (armyChess.x + 0.22f) * mSquareWidth, (armyChess.getBoardY() + 0.66f) * mSquareHeight, tempTxtPaint);
        }
    }

    private void drawBoard(Canvas canvas) {
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
            int x = (int) (event.getX() / mSquareWidth);
            int y = (int) (event.getY() / mSquareHeight);
            //点击对方棋子无反应
            if (y < 7) {
                return true;
            }
            //将board坐标转为array下标
            y -= 1;
            //兵营里不能放棋子
            if (isInCamps(x, y)) {
                return true;
            }
            //点击的地方都是有棋子的
            if (null == selectedArmyChess) {
                selectedArmyChess = mArmyChesses[x][y];
                invalidate();
                return true;
            }
            //地雷易位
            if (selectedArmyChess.getF() == 9 || mArmyChesses[x][y].getF() == 9) {
                if (selectedArmyChess.y < 10 || mArmyChesses[x][y].y < 10) {
                    ToastUtil.toast("地雷只能放在最后两行");
                    return true;
                }
            }
            //炸弹易位
            if (selectedArmyChess.getF() == 10 || mArmyChesses[x][y].getF() == 10) {
                if (selectedArmyChess.y == 6 || mArmyChesses[x][y].y == 6) {
                    ToastUtil.toast("炸弹不能放在第一行");
                    return true;
                }
            }
            //军旗易位
            if (selectedArmyChess.getF() == 11 || mArmyChesses[x][y].getF() == 11) {
                if (!isInBaseCamps(selectedArmyChess.x, selectedArmyChess.y) || !isInBaseCamps(x, y)) {
                    ToastUtil.toast("军旗只能放在大本营中");
                    return true;
                }
            }
            int fromX = selectedArmyChess.x;
            int fromY = selectedArmyChess.y;
            int fromF = selectedArmyChess.getF();
            mArmyChesses[fromX][fromY].setF(mArmyChesses[x][y].getF());
            mArmyChesses[x][y].setF(fromF);
            selectedArmyChess=null;
            invalidate();
        }
        return true;
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

    public void initChessBoard(List<Integer> myChessList) {
        List<Integer> chessList = new ArrayList<>(60);
        if (null == myChessList) {
            myChessList = ArmyChessUtil.genHalfChessList();
        }
        for (int i = 0; i < 25; i++) {
            chessList.add(0);
        }
        chessList.addAll(myChessList);

        mArmyChesses = new FMArmyChess[5][12];
        //添加10个空位置,方便遍历绘制棋子
        int[] indexes = new int[]{11, 13, 17, 21, 23, 36, 38, 42, 46, 48};
        for (int i = 0, len = indexes.length; i < len; i++) {
            chessList.add(indexes[i], -1);
        }
        int flagIndex = 0;
        for (int j = 0; j < 12; j++) {
            for (int i = 0; i < 5; i++) {
                int flag = chessList.get(flagIndex);
                if (flag < 0) {
                    flagIndex++;
                    continue;
                }
                mArmyChesses[i][j] = new FMArmyChess(flag, i, j, myColor, j > 5 ? ArmyChessUtil.STAT_NORMAL : ArmyChessUtil.STAT_UNKNOWN);
                flagIndex++;
            }
        }
        invalidate();
    }

    public List<Integer>getMyChessList(){
        List<Integer>myChessList=new ArrayList<>(25);
        for(int j=6;j<12;j++){
            for(int i=0;i<5;i++){
                if(null==mArmyChesses[i][j]){
                    continue;
                }
                myChessList.add(mArmyChesses[i][j].getF());
            }
        }
        return myChessList;
    }
}