package com.ue.cnchess.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.hyphenate.easeui.game.GameConstants;
import com.hyphenate.easeui.game.base.BaseGameView;
import com.hyphenate.easeui.game.db.DBConstants;
import com.hyphenate.easeui.game.db.GameDbManager;
import com.hyphenate.easeui.game.iterface.OnPlayListener;
import com.ue.cnchess.R;
import com.ue.cnchess.entity.CNChessRecord;
import com.ue.cnchess.entity.CnChessPoint;
import com.ue.cnchess.entity.RecordParser;
import com.ue.cnchess.util.CNChessUtil;
import com.ue.common.util.DisplayUtil;
import com.ue.common.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CNChessView extends BaseGameView {
    private Paint paint = new Paint();
    private float mPanelWidth;
    private float mPanelHeight;
    private float mLineHeight;
    private float pieceSize;
    private Bitmap backgroundBitmap;

    //棋盘现有棋子的位子记录
    public CnChessPoint[][] qzs;
    private int myColor;
    private int oppoColor;
    private CNChessRecord lastMove;
    private CnChessPoint selectedChess;

    public CnChessPoint[][] getChessArray() {
        return qzs;
    }

    public CNChessView(Context context) {
        this(context, null, 0);
    }

    public CNChessView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CNChessView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getHolder().addCallback(this);

        mPanelWidth = DisplayUtil.getScreenWidth(context) * 0.95f;
        mLineHeight = mPanelWidth / 9;
        int addLineHeight = (int) (mLineHeight + 1);
        mPanelHeight = mPanelWidth + addLineHeight;
        backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.game_bg_mood);
        backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, (int) mPanelWidth, (int) mPanelHeight, false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) mPanelWidth, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) mPanelHeight, MeasureSpec.EXACTLY);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void initChessBoard() {
        qzs = new CnChessPoint[9][10];
        //初始化炮
        qzs[1][2] = new CnChessPoint(CNChessUtil.CANNON, 1, 2, oppoColor);
        qzs[7][2] = new CnChessPoint(CNChessUtil.CANNON, 7, 2, oppoColor);
        qzs[1][7] = new CnChessPoint(CNChessUtil.CANNON, 1, 7, myColor);
        qzs[7][7] = new CnChessPoint(CNChessUtil.CANNON, 7, 7, myColor);
        //初始化车
        qzs[0][0] = new CnChessPoint(CNChessUtil.CHARIOT, 0, 0, oppoColor);
        qzs[8][0] = new CnChessPoint(CNChessUtil.CHARIOT, 8, 0, oppoColor);
        qzs[0][9] = new CnChessPoint(CNChessUtil.CHARIOT, 0, 9, myColor);
        qzs[8][9] = new CnChessPoint(CNChessUtil.CHARIOT, 8, 9, myColor);
        //初始化马
        qzs[1][0] = new CnChessPoint(CNChessUtil.HORSE, 1, 0, oppoColor);
        qzs[7][0] = new CnChessPoint(CNChessUtil.HORSE, 7, 0, oppoColor);
        qzs[1][9] = new CnChessPoint(CNChessUtil.HORSE, 1, 9, myColor);
        qzs[7][9] = new CnChessPoint(CNChessUtil.HORSE, 7, 9, myColor);
        //初始化象
        qzs[2][0] = new CnChessPoint(CNChessUtil.ELEPHANT, 2, 0, oppoColor);
        qzs[6][0] = new CnChessPoint(CNChessUtil.ELEPHANT, 6, 0, oppoColor);
        qzs[2][9] = new CnChessPoint(CNChessUtil.ELEPHANT, 2, 9, myColor);
        qzs[6][9] = new CnChessPoint(CNChessUtil.ELEPHANT, 6, 9, myColor);
        //初始化士
        qzs[3][0] = new CnChessPoint(CNChessUtil.GUARD, 3, 0, oppoColor);
        qzs[5][0] = new CnChessPoint(CNChessUtil.GUARD, 5, 0, oppoColor);
        qzs[3][9] = new CnChessPoint(CNChessUtil.GUARD, 3, 9, myColor);
        qzs[5][9] = new CnChessPoint(CNChessUtil.GUARD, 5, 9, myColor);
        //将帅
        qzs[4][0] = new CnChessPoint(CNChessUtil.O_GENERAL, 4, 0, oppoColor);
        qzs[4][9] = new CnChessPoint(CNChessUtil.M_GENERAL, 4, 9, myColor);
        //初始化兵
        for (int i = 0; i < 5; i++) {
            qzs[i * 2][3] = new CnChessPoint(CNChessUtil.O_SOLDIER, i * 2, 3, oppoColor);
            qzs[i * 2][6] = new CnChessPoint(CNChessUtil.M_SOLDIER, i * 2, 6, myColor);
        }
        //enableRender();
    }

    //绘制棋盘
    public void drawBoard(Canvas canvas) {
        canvas.drawBitmap(backgroundBitmap, 0, 0, null);

        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);//设置去锯齿
        paint.setStrokeWidth(2);//线条宽度
        //横线
        for (int i = 0, start = (int) (mLineHeight / 2), end = (int)(mPanelWidth - start); i < 10; i++) {
            float y = (0.5f + i) * mLineHeight;
            canvas.drawLine(start, y, end, y, paint);
        }
        //上-纵线
        for (int i = 1, j = 9 - 1, start = (int) (mLineHeight / 2), end = (int) (4.5f * mLineHeight); i < j; i++) {
            int x = (int) ((0.5f + i) * mLineHeight);
            canvas.drawLine(x, start, x, end, paint);
        }
        //下-纵线
        for (int i = 1, j = 9 - 1, start = (int) (5.5f * mLineHeight), end = (int) (mPanelHeight - mLineHeight / 2); i < j; i++) {
            float x = (0.5f + i) * mLineHeight;
            canvas.drawLine(x, start, x, end, paint);
        }
        int x = (int) (mLineHeight / 2);
        int y = (int) (mPanelHeight - mLineHeight / 2);
        canvas.drawLine(x, x, x, y, paint);//第一条纵线
        x = (int) (mPanelWidth - mLineHeight / 2);
        canvas.drawLine(x, mLineHeight / 2, x, y, paint);//最后一条纵线

        //交叉线
        int startX = (int) (3.5f * mLineHeight);
        int startY = (int) (mLineHeight / 2);
        int stopX = (int) (5.5f * mLineHeight);
        int stopY = (int) (2.5f * mLineHeight);
        canvas.drawLine(startX, startY, stopX, stopY, paint);
        canvas.drawLine(startX, stopY, stopX, startY, paint);

        startY = (int) (7.5f * mLineHeight);
        stopY = (int) (9.5f * mLineHeight);
        canvas.drawLine(startX, startY, stopX, stopY, paint);
        canvas.drawLine(startX, stopY, stopX, startY, paint);

        //绘制折线
        drawItemKH(canvas, 1, 2);
        drawItemKH(canvas, 7, 2);
        drawItemKH(canvas, 1, 7);
        drawItemKH(canvas, 7, 7);
        for (int i = 0, j = 0; i < 5; i++, j += 2) {
            drawItemKH(canvas, j, 3);
            drawItemKH(canvas, j, 6);
        }

        paint.setTextSize(mLineHeight / 2);
        float centy = 5.2f * mLineHeight;
        float centx = 2.5f * mLineHeight;
        float centx2 = 5.5f * mLineHeight;
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText("楚河", centx, centy, paint); // 画出文字
        canvas.drawText("汉界", centx2, centy, paint); // 画出文字
    }

    // 绘制折线
    private void drawItemKH(Canvas c, int pointX, int pointY) {
        // 获取中心坐标
        float x = (pointX + 0.5f) * mLineHeight;
        float y = (pointY + 0.5f) * mLineHeight;
        // 线长
        float len = mLineHeight / 4;
        // 距离中心
        float dc = mLineHeight / 10;

        // left，如果x是0的话，不绘制左侧
        if (pointX != 0) {
            c.drawLine(x - dc, y + dc, x - dc - len, y + dc, paint);
            c.drawLine(x - dc, y - dc, x - dc - len, y - dc, paint);
            c.drawLine(x - dc, y + dc, x - dc, y + dc + len, paint);
            c.drawLine(x - dc, y - dc, x - dc, y - dc - len, paint);
        }
        // right
        if (pointX != 8) {
            c.drawLine(x + dc, y + dc, x + dc + len, y + dc, paint);
            c.drawLine(x + dc, y - dc, x + dc + len, y - dc, paint);
            c.drawLine(x + dc, y + dc, x + dc, y + dc + len, paint);
            c.drawLine(x + dc, y - dc, x + dc, y - dc - len, paint);
        }
    }

    //绘制棋子
    public void drawPieces(Canvas canvas) {
        if (null == qzs) {
            return;
        }
        pieceSize = 0.4f * mLineHeight;
        paint.setTextSize(mLineHeight / 2);
        paint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 10; j++) {
                if (null == qzs[i][j]) {
                    continue;
                }
                drawPiece(canvas, qzs[i][j]);
            }
        }
        if (null != selectedChess) {
            paint.setColor(getResources().getColor(R.color.green_66cc99));
            canvas.drawCircle((selectedChess.x + 0.5f) * mLineHeight, (selectedChess.y + 0.5f) * mLineHeight, pieceSize, paint);
            //绘制棋子上面的字
            paint.setColor(getResources().getColor(R.color.white_ffffff));
            canvas.drawText(selectedChess.txt(), (selectedChess.x + 0.25f) * mLineHeight, (selectedChess.y + 0.7f) * mLineHeight, paint);
        }
        if (lastMove != null) {
            paint.setStyle(Paint.Style.STROKE);// 变成空心的圆

            paint.setColor(Color.GREEN);
            int xx = lastMove.fromX;
            int yy = lastMove.fromY;
            canvas.drawCircle((0.5f + xx) * mLineHeight, (0.5f + yy) * mLineHeight, pieceSize, paint);

            paint.setColor(Color.BLUE);
            xx = lastMove.toX;
            yy = lastMove.toY;
            canvas.drawCircle((0.5f + xx) * mLineHeight, (0.5f + yy) * mLineHeight, pieceSize, paint);
        }
    }

    //画棋子
    private void drawPiece(Canvas canvas, CnChessPoint point) {
        if (point.s == CNChessUtil.STATUS_CAN_GO || point.s == CNChessUtil.STATUS_CAN_EAT) {
            paint.setColor(Color.parseColor("#bababa"));
            canvas.drawCircle((point.x + 0.5f) * mLineHeight, (point.y + 0.5f) * mLineHeight, pieceSize, paint);
            //绘制棋子上面的字
            paint.setColor(Color.parseColor("#ffffff"));
            canvas.drawText(point.txt(), (point.x + 0.25f) * mLineHeight, (point.y + 0.7f) * mLineHeight, paint);
            return;
        }
        //偏移量：1/2*mLineHeight-ratioPieceOfLineHeight*mLineHight/2=((1-ratioPieceOfLineHeight)/2)*mLineHeight;(0,0)距离panel最左边的距离
        if (point.c == CNChessUtil.BLACK) {
            paint.setColor(Color.parseColor("#000000"));
            canvas.drawCircle((point.x + 0.5f) * mLineHeight, (point.y + 0.5f) * mLineHeight, pieceSize, paint);
            //绘制棋子上面的字
            paint.setColor(Color.parseColor("#ffffff"));
            canvas.drawText(point.txt(), (point.x + 0.25f) * mLineHeight, (point.y + 0.7f) * mLineHeight, paint);
            return;
        }
        if (point.c == CNChessUtil.RED) {
            paint.setColor(Color.parseColor("#ffff4444"));
            canvas.drawCircle((point.x + 0.5f) * mLineHeight, (point.y + 0.5f) * mLineHeight, pieceSize, paint);
            //绘制棋子上面的字
            paint.setColor(Color.parseColor("#ffffff"));
            canvas.drawText(point.txt(), (point.x + 0.25f) * mLineHeight, (point.y + 0.7f) * mLineHeight, paint);
        }
    }

    //这里面的可以进行方法的提取
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (!isGameStarted) {
                return false;
            }
            if (gameMode != GameConstants.MODE_DOUBLE && !isMyTurn) {
                return true;
            }

            int i = (int) (event.getX() / mLineHeight);
            int j = (int) (event.getY() / mLineHeight);

            playChess(new int[]{i, j});
        }
        return true;
    }

    //online/single
    private int[] translateData(int[] data) {
        int fromX = data[0], fromY = data[1], toX = data[2], toY = data[3];
        //在线模式的话需要对数据进行一下转换,左右对称上下对称
        if (gameMode == GameConstants.MODE_ONLINE) {
            fromX = 8 - fromX;
            fromY = 9 - fromY;//6->3,5->4
            toX = 8 - toX;
            toY = 9 - toY;
        }
        selectedChess = qzs[fromX][fromY];
        LogUtil.i(TAG, "translateData,selectedChess=" + selectedChess);//null
        if (null == qzs[toX][toY]) {
            qzs[toX][toY] = new CnChessPoint(selectedChess.f(), toX, toY, selectedChess.c);
            qzs[toX][toY].s = CNChessUtil.STATUS_CAN_GO;
        } else {
            qzs[toX][toY].s = CNChessUtil.STATUS_CAN_EAT;
        }
        return new int[]{toX, toY};
    }

    private void resetStatus(boolean isInvalidate) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 10; j++) {
                if (qzs[i][j] == null) {
                    continue;
                }
                if (qzs[i][j].s == CNChessUtil.STATUS_CAN_GO) {
                    qzs[i][j] = null;
                } else {
                    qzs[i][j].s = CNChessUtil.STATUS_NORMAL;  //正常
                }
            }
        }
        if (isInvalidate) {
            //enableRender();
        }
    }

    public void undoOnce(boolean isMyUndo) {
        Object[] lastTwoRecords = GameDbManager.getInstance().getLastTwoRecords(DBConstants.TABLE_CNCHESS, new RecordParser());
        if (null == lastTwoRecords) {
            return;
        }
        lastMove = (CNChessRecord) lastTwoRecords[0];
        if(lastMove==null){
            return;
        }
        if (null != mOnUndoListener) {
            int[] data = new int[]{
                    lastMove.fromF, lastMove.fromX, lastMove.fromY, lastMove.fromC,
                    lastMove.toF, lastMove.toX, lastMove.toY, lastMove.toC};
            mOnUndoListener.onUndo(data);
        }
        qzs[lastMove.fromX][lastMove.fromY] = new CnChessPoint(lastMove.fromF, lastMove.fromX, lastMove.fromY, lastMove.fromC);
        qzs[lastMove.toX][lastMove.toY] = lastMove.toF == -1 ? null : new CnChessPoint(lastMove.toF, lastMove.toX, lastMove.toY, lastMove.toC);

        GameDbManager.getInstance().deleteChessRecord(DBConstants.TABLE_CNCHESS, lastMove.id);

        lastMove = (CNChessRecord) lastTwoRecords[1];
        canUndoTime--;
        isMyTurn = !isMyTurn;
        resetStatus(true);
    }

    @Override
    public void playChess(int[] oData) {
        if (!isGameStarted) {
            return;
        }
        int[] data;
        if (isMyTurn || gameMode == GameConstants.MODE_DOUBLE) {
            data = oData;
        } else {
            data = translateData(oData);
        }

        int toX = data[0];
        int toY = data[1];

        if (null == qzs[toX][toY]) {
            selectedChess = null;
            resetStatus(true);
            return;
        }
        if (null == selectedChess) {
            if (isMyTurn && qzs[toX][toY].c != myColor) {
                return;
            }
            if (!isMyTurn && qzs[toX][toY].c == myColor) {
                return;
            }
            selectedChess = qzs[toX][toY];
            selectedChess.showValidMoves(qzs);
            //enableRender();
            return;
        }
        if (qzs[toX][toY].s == CNChessUtil.STATUS_NORMAL) {
            resetStatus(false);
            if (qzs[toX][toY].c == selectedChess.c) {
                selectedChess = qzs[toX][toY];
                selectedChess.showValidMoves(qzs);
            } else {
                selectedChess = null;
            }
            //enableRender();
            return;
        }
        //save record
        lastMove = new CNChessRecord(selectedChess, qzs[toX][toY]);
        boolean result = GameDbManager.getInstance().saveChessRecord(DBConstants.TABLE_CNCHESS, lastMove.toString());
        if (result) {
            canUndoTime++;
        }
        //is game over
        int flag = -1;
        if (qzs[toX][toY].s == CNChessUtil.STATUS_CAN_EAT) {
            if (qzs[toX][toY].f() == CNChessUtil.O_GENERAL) {
                flag = OnPlayListener.FLAG_I_WIN;
            } else if (qzs[toX][toY].f() == CNChessUtil.M_GENERAL) {
                flag = OnPlayListener.FLAG_OPPO_WIN;
            }
        }
        //move
        int fromX = selectedChess.x;
        int fromY = selectedChess.y;
        qzs[toX][toY].copyFrom(selectedChess);
        qzs[fromX][fromY] = null;
        selectedChess = null;
        resetStatus(true);
        //on play
        if (isMyTurn) {
            isMyTurn = false;
            mOnPlayListener.onIPlayed(new int[]{fromX, fromY, toX, toY});
        } else {
            isMyTurn = true;
            mOnPlayListener.onOppoPlayed(new int[]{fromX, fromY, toX, toY});
        }
        if (flag != -1) {
            gameOver(flag);
        }
    }

    @Override
    public void startGame(boolean isIRunFirst) {
        GameDbManager.getInstance().clearChessRecord(DBConstants.TABLE_CNCHESS);
        lastMove = null;
        selectedChess = null;
        isMyTurn = isIRunFirst;
        canUndoTime = 0;
        if (isIRunFirst) {
            myColor = CNChessUtil.RED;
            oppoColor = CNChessUtil.BLACK;
        } else {
            myColor = CNChessUtil.BLACK;
            oppoColor = CNChessUtil.RED;
        }
        initChessBoard();
        isGameStarted = true;
    }

    @Override
    public void initChessBoard(boolean isIFirst) {
        lastMove = null;
        initChessBoard();
    }

    private static final String INSTANCE = "instance";
    private static final String INSTANCE_MY_COLOR = "myColor";
    private static final String INSTANCE_OPPO_COLOR = "oppoColor";
    private static final String INSTANCE_GAME_OVER = "aIsGameStarted";
    private static final String INSTANCE_IS_MY_TURN = "aIsMyTurn";
    private static final String INSTANCE_CAN_UNDO_TIME = "canUndoTime";
    private static final String INSTANCE_GAME_MODE = "gameMode";
    private static final String INSTANCE_LAST_MOVE = "lastMove";
    private static final String INSTANCE_CHESS_ARRAY = "qzs";

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
        bundle.putString(INSTANCE_LAST_MOVE, lastMove.toString());
        bundle.putString(INSTANCE_CHESS_ARRAY, translateChessArrayToJson(qzs));
        return bundle;
    }

    //    qzs = new CnChessPoint[9][10];
    private String translateChessArrayToJson(CnChessPoint[][] qzs) {
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 10; j++) {
                //有的位置是没有棋子的
                if (qzs[i][j] == null) {
                    stringList.add("{}");
                    continue;
                }
                stringList.add(qzs[i][j].toString());
            }
        }
        LogUtil.i(TAG, "translateChessArrayToJson,stringList=" + stringList.toString());
        return stringList.toString();
    }

    //    qzs = new CnChessPoint[9][10];
    private void translateJsonToChessArray(String jsonChessArray) {
        try {
            JSONArray jsonArray = new JSONArray(jsonChessArray);
            int index = 0;
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 10; j++) {
                    try {
                        JSONObject chessPoint = jsonArray.getJSONObject(index++);
//                    String.format("{'f':%d,'x':%d,'y':%d,'c':%d,'s':%d}",f,x,y,c,s);
                        int x = chessPoint.getInt("x");
                        int y = chessPoint.getInt("y");
                        int c = chessPoint.getInt("c");
//                        int s = chessPoint.getInt("s");
                        int f = chessPoint.getInt("f");
//                        qzs[i][j]=new CnChessPoint(f,x,y,c,s);
                        qzs[i][j] = new CnChessPoint(f, x, y, c);
                    } catch (JSONException jExp) {
                        LogUtil.i(TAG, "translateJsonToChessArray,inside error:" + jExp.getMessage());
                        continue;
                    }
                }
            }
        } catch (JSONException e) {
            LogUtil.i(TAG, "translateJsonToChessArray,error:" + e.getMessage());
        }
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            myColor = bundle.getInt(INSTANCE_MY_COLOR);
            oppoColor = bundle.getInt(INSTANCE_OPPO_COLOR);
            isGameStarted = bundle.getBoolean(INSTANCE_GAME_OVER);
            isMyTurn = bundle.getBoolean(INSTANCE_IS_MY_TURN);
            canUndoTime = bundle.getInt(INSTANCE_CAN_UNDO_TIME);
            gameMode = bundle.getInt(INSTANCE_GAME_MODE);

            String lastChessMoveJson = bundle.getString(INSTANCE_LAST_MOVE);
            try {
                JSONObject lastChessMoveObj = new JSONObject(lastChessMoveJson);
//                String.format("{'fromF':%d,'fromX':%d,'fromY':%d,'fromC':%d,'toF':%d,'toX':%d,'toY':%d,'toC':%d}",
//                        fromF,fromX,fromY,fromC,toF,toX,toY,toC);
                lastMove = new CNChessRecord();
                lastMove.fromF = lastChessMoveObj.getInt("fromF");
                lastMove.fromX = lastChessMoveObj.getInt("fromX");
                lastMove.fromY = lastChessMoveObj.getInt("fromY");
                lastMove.fromC = lastChessMoveObj.getInt("fromC");
                lastMove.toF = lastChessMoveObj.getInt("toF");
                lastMove.toX = lastChessMoveObj.getInt("toX");
                lastMove.toY = lastChessMoveObj.getInt("toY");
                lastMove.toC = lastChessMoveObj.getInt("toC");
            } catch (JSONException e) {
                LogUtil.i(TAG, "parse lastChessMoveObj error:" + e.getMessage());
            }

            translateJsonToChessArray(bundle.getString(INSTANCE_CHESS_ARRAY));

            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
}