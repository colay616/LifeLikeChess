package com.ue.chess.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.LruCache;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.hyphenate.easeui.game.GameConstants;
import com.hyphenate.easeui.game.base.BaseGameView;
import com.hyphenate.easeui.game.db.DBConstants;
import com.hyphenate.easeui.game.db.GameDbManager;
import com.ue.chess.entity.ChessPoint;
import com.ue.chess.entity.ChessRecord;
import com.ue.chess.entity.RecordParser;
import com.ue.chess.util.ChessUtil;
import com.ue.common.util.DisplayUtil;
import com.ue.common.util.LogUtil;
import com.ue.common.util.ToastUtil;

/**
 * Reference:
 * Author:
 * Date:2016/9/13.
 */
public class ChessView extends BaseGameView {
    private static final String TAG = ChessView.class.getSimpleName();
    private float mPanelWidth;
    private float mSquareWidth;//棋盘格子宽高
    private Paint mPaint;
    private int pieceWidth;

    private PromotionDialog mPromotionDialog;

    private LruCache<Integer, Bitmap> mMemoryCache;
    private Bitmap piece;
    private ChessPoint[][] qzs;
    private int myColor;
    private int oppoColor;
    private ChessRecord lastMove;
    private ChessPoint selectedChess;

    public ChessView(Context context) {
        this(context, null, 0);
    }

    public ChessView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChessView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);//抖动处理，平滑处理

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Use 1/16th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 16;
        mMemoryCache = new LruCache<Integer, Bitmap>(cacheSize);

        mPanelWidth = DisplayUtil.getScreenWidth(context) * 0.9f;
        mSquareWidth = mPanelWidth / 8;

        pieceWidth = (int) mSquareWidth - 10;

        initChessArray(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) mPanelWidth, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) mPanelWidth, MeasureSpec.EXACTLY);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void addBitmapToMemoryCache(int key, Bitmap bitmap) {
        if (mMemoryCache.get(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private void initChessArray(boolean isReversed) {
        qzs = new ChessPoint[8][8];

        if (isReversed) {
            qzs[3][0] = new ChessPoint(ChessUtil.KING, 3, 0, oppoColor);
            qzs[4][0] = new ChessPoint(ChessUtil.QUEEN, 4, 0, oppoColor);
            qzs[3][7] = new ChessPoint(ChessUtil.KING, 3, 7, myColor);
            qzs[4][7] = new ChessPoint(ChessUtil.QUEEN, 4, 7, myColor);
        } else {
            qzs[3][0] = new ChessPoint(ChessUtil.QUEEN, 3, 0, oppoColor);
            qzs[4][0] = new ChessPoint(ChessUtil.KING, 4, 0, oppoColor);
            qzs[3][7] = new ChessPoint(ChessUtil.QUEEN, 3, 7, myColor);
            qzs[4][7] = new ChessPoint(ChessUtil.KING, 4, 7, myColor);
        }

        qzs[0][0] = new ChessPoint(ChessUtil.ROOK, 0, 0, oppoColor);
        qzs[1][0] = new ChessPoint(ChessUtil.KNIGHT, 1, 0, oppoColor);
        qzs[2][0] = new ChessPoint(ChessUtil.BISHOP, 2, 0, oppoColor);
        qzs[5][0] = new ChessPoint(ChessUtil.BISHOP, 5, 0, oppoColor);
        qzs[6][0] = new ChessPoint(ChessUtil.KNIGHT, 6, 0, oppoColor);
        qzs[7][0] = new ChessPoint(ChessUtil.ROOK, 7, 0, oppoColor);

        qzs[0][7] = new ChessPoint(ChessUtil.ROOK, 0, 7, myColor);
        qzs[1][7] = new ChessPoint(ChessUtil.KNIGHT, 1, 7, myColor);
        qzs[2][7] = new ChessPoint(ChessUtil.BISHOP, 2, 7, myColor);
        qzs[5][7] = new ChessPoint(ChessUtil.BISHOP, 5, 7, myColor);
        qzs[6][7] = new ChessPoint(ChessUtil.KNIGHT, 6, 7, myColor);
        qzs[7][7] = new ChessPoint(ChessUtil.ROOK, 7, 7, myColor);

        for (int i = 0; i < 8; i++) {
            qzs[i][1] = new ChessPoint(ChessUtil.PAWN, i, 1, oppoColor);
        }

        for (int i = 0; i < 8; i++) {
            qzs[i][6] = new ChessPoint(ChessUtil.PAWN, i, 6, myColor);
        }
    }

    public void drawPieces(Canvas canvas) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (null != qzs[i][j]) {
                    drawPiece(canvas, i, j);
                }
            }
        }
        if (lastMove != null) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(3);
            mPaint.setColor(Color.BLUE);
            canvas.drawRect(lastMove.fromX * mSquareWidth, lastMove.fromY * mSquareWidth, (lastMove.fromX + 1) * mSquareWidth, (lastMove.fromY + 1) * mSquareWidth, mPaint);

            mPaint.setColor(Color.GREEN);
            canvas.drawRect(lastMove.toX * mSquareWidth, lastMove.toY * mSquareWidth, (lastMove.toX + 1) * mSquareWidth, (lastMove.toY + 1) * mSquareWidth, mPaint);
        }
    }

    private void drawPiece(Canvas canvas, int i, int j) {
        switch (qzs[i][j].s){
            case ChessUtil.STATUS_NORMAL:
                piece = mMemoryCache.get(qzs[i][j].img());
                if (null == piece) {
                    piece = BitmapFactory.decodeResource(getResources(), qzs[i][j].img());
                    piece = Bitmap.createScaledBitmap(piece, pieceWidth, pieceWidth, false);
                    addBitmapToMemoryCache(qzs[i][j].img(), piece);
                }
                canvas.drawBitmap(piece, i * mSquareWidth + 5, j * mSquareWidth + 5, mPaint);
                break;
            case ChessUtil.STATUS_CHOSEN:
                piece = mMemoryCache.get(qzs[i][j].img());
                if (null == piece) {
                    piece = BitmapFactory.decodeResource(getResources(), qzs[i][j].img());
                    piece = Bitmap.createScaledBitmap(piece, pieceWidth, pieceWidth, false);
                    addBitmapToMemoryCache(qzs[i][j].img(), piece);
                }
                canvas.drawBitmap(piece, i * mSquareWidth + 5, j * mSquareWidth + 5, mPaint);

                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(3);
                mPaint.setColor(Color.BLACK);
                canvas.drawRect(i * mSquareWidth, j * mSquareWidth, (i + 1) * mSquareWidth, (j + 1) * mSquareWidth, mPaint);
                break;
            case ChessUtil.STATUS_CAN_GO:
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(Color.GREEN);
                canvas.drawCircle((i + 0.5f) * mSquareWidth, (j + 0.5f) * mSquareWidth, mSquareWidth / 4, mPaint);
                break;
            case ChessUtil.STATUS_CAN_EAT:
                piece = mMemoryCache.get(qzs[i][j].img());
                if (null == piece) {
                    piece = BitmapFactory.decodeResource(getResources(), qzs[i][j].img());
                    piece = Bitmap.createScaledBitmap(piece, pieceWidth, pieceWidth, false);
                    addBitmapToMemoryCache(qzs[i][j].img(), piece);
                }
                canvas.drawBitmap(piece, i * mSquareWidth + 5, j * mSquareWidth + 5, mPaint);

                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(Color.GREEN);
                canvas.drawCircle((i + 0.5f) * mSquareWidth, (j + 0.5f) * mSquareWidth, mSquareWidth / 4, mPaint);
                break;
            case ChessUtil.STATUS_EXCHANGE:
                piece = mMemoryCache.get(qzs[i][j].img());
                if (null == piece) {
                    piece = BitmapFactory.decodeResource(getResources(), qzs[i][j].img());
                    piece = Bitmap.createScaledBitmap(piece, pieceWidth, pieceWidth, false);
                    addBitmapToMemoryCache(qzs[i][j].img(), piece);
                }
                canvas.drawBitmap(piece, i * mSquareWidth + 5, j * mSquareWidth + 5, mPaint);

                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(Color.BLUE);
                canvas.drawCircle((i + 0.5f) * mSquareWidth, (j + 0.5f) * mSquareWidth, mSquareWidth / 4, mPaint);
                break;
        }
    }

    public void drawBoard(Canvas canvas) {
        mPaint.setColor(0x88000000);
        mPaint.setStyle(Paint.Style.STROKE);//描边

        float endWidth = mPanelWidth;
        float tempFloat;
        for (int i = 0; i < 9; i++) {
            tempFloat = i * mSquareWidth;
            //橫线
            canvas.drawLine(0, tempFloat, endWidth, tempFloat, mPaint);
            //纵线
            canvas.drawLine(tempFloat, 0, tempFloat, endWidth, mPaint);
        }
        mPaint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 0) {
                    mPaint.setColor(Color.parseColor("#ffffff"));
                } else {
                    mPaint.setColor(Color.parseColor("#9AAE9B"));
                }
                canvas.drawRect(i * mSquareWidth, j * mSquareWidth, (i + 1) * mSquareWidth, (j + 1) * mSquareWidth, mPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (!isGameStarted) {
                return true;
            }
            if (gameMode != GameConstants.MODE_DOUBLE && !isMyTurn) {
                return true;
            }
            int x = (int) (event.getX() / mSquareWidth);
            int y = (int) (event.getY() / mSquareWidth);

            if (null == qzs[x][y]) {
                selectedChess = null;
                resetStatus();
                return true;
            }
            if (null == selectedChess) {
                if ((isMyTurn && qzs[x][y].c == myColor) || (!isMyTurn && qzs[x][y].c == oppoColor)) {
                    selectedChess = qzs[x][y];
                    LogUtil.e(TAG,"isFirstMove=" + selectedChess.isFirstMove());
                    selectedChess.showValidMoves(isMyTurn, qzs);
                    return true;
                }
                return true;
            }
            if (qzs[x][y].s == ChessUtil.STATUS_NORMAL) {
                resetStatus();
                if (qzs[x][y].c == selectedChess.c) {
                    selectedChess = qzs[x][y];
                    selectedChess.showValidMoves(isMyTurn, qzs);
                } else {
                    selectedChess = null;
                }
                return true;
            }
            LogUtil.i(TAG, "before playChess,isMyTurn=" + isMyTurn);
            playChess(new int[]{x, y});
            LogUtil.i(TAG, "after playChess,isMyTurn=" + isMyTurn);
        }
        return true;
    }

    private boolean isPawnPromoted(int roleFlag) {
        return roleFlag == ChessUtil.BISHOP || roleFlag == ChessUtil.KNIGHT || roleFlag == ChessUtil.QUEEN || roleFlag == ChessUtil.ROOK;
    }

    private void completeOneStep(int x, int y, int extraFlag) {
        boolean isPawnPromoted = isPawnPromoted(extraFlag);
        LogUtil.i(TAG, "completeOneStep,isPawnPromoted=" + isPawnPromoted + ",x=" + x + ",y=" + y);

        if (extraFlag == ChessUtil.FLAG_EXCHANGE) {
            //王车易位：王向车方向移动两格，车绕过王停在王的旁边
            if(selectedChess.x>x){//车在王的左边
                qzs[selectedChess.x-2][y]=new ChessPoint(selectedChess.x-2,y);
                qzs[selectedChess.x-2][y].copyFrom(selectedChess);//王易位

                qzs[selectedChess.x-1][y]=new ChessPoint(selectedChess.x-1,y);
                qzs[selectedChess.x-1][y].copyFrom(qzs[x][y]);//车易位
            }else{//车在王的右边
                qzs[selectedChess.x+2][y]=new ChessPoint(selectedChess.x+2,y);
                qzs[selectedChess.x+2][y].copyFrom(selectedChess);

                qzs[selectedChess.x+1][y]=new ChessPoint(selectedChess.x+1,y);
                qzs[selectedChess.x+1][y].copyFrom(qzs[x][y]);
            }
            saveRecord(new ChessRecord(selectedChess, qzs[x][y],true));
            qzs[x][y]=null;
        } else {
            saveRecord(new ChessRecord(selectedChess, qzs[x][y],false));
            qzs[x][y].copyFrom(selectedChess);
            if (isPawnPromoted) {//如果升兵了的话需要更新flag
                qzs[x][y].setF(extraFlag);
            }
        }
        qzs[lastMove.fromX][lastMove.fromY] = null;//清除走前的位置
        selectedChess = null;//清除选中
        resetStatus();

        if (isMyTurn) {
            isMyTurn = false;
            int[] data = (isPawnPromoted||extraFlag==ChessUtil.FLAG_EXCHANGE) ? new int[]{lastMove.fromX, lastMove.fromY, lastMove.toX, lastMove.toY, extraFlag} : new int[]{lastMove.fromX, lastMove.fromY, lastMove.toX, lastMove.toY};
            mOnPlayListener.onIPlayed(data);
        } else {
            isMyTurn = true;
            mOnPlayListener.onOppoPlayed(null);
        }
    }

    private void saveRecord(ChessRecord record){
        //<-------save record--------
        lastMove=record;
        boolean saveResult = GameDbManager.getInstance().saveChessRecord(DBConstants.TABLE_CHESS, lastMove.toString());
        if (saveResult) {
            canUndoTime++;
        }
        //------save record end--------->
    }

    public int[] translateData(int[] data) {
        int fromX = data[0], fromY = data[1], toX = data[2], toY = data[3];
        if (gameMode == GameConstants.MODE_ONLINE) {
            LogUtil.i(TAG, "before translation,data0=" + data[0] + ",data1=" + data[1] + ",data2=" + data[2] + ",data3=" + data[3]);
//            before translation,data0=4,data1=7,data2=7,data3=7
            fromX = 7 - fromX;
            fromY = 7 - fromY;
            toX = 7 - toX;
            toY = 7 - toY;
            LogUtil.i(TAG,"after translation,fromX="+fromX+",fromY="+fromY+",toX="+toX+",toY="+toY);
//            after translation,fromX=3,fromY=0,toX=0,toY=0
        }
        selectedChess = qzs[fromX][fromY];
        if (null == qzs[toX][toY]) {
            qzs[toX][toY] = new ChessPoint(selectedChess.f(), toX, toY, selectedChess.c);
            qzs[toX][toY].s = ChessUtil.STATUS_CAN_GO;
        } else {
            //王车易位
            if(data.length==5&&data[4]==ChessUtil.FLAG_EXCHANGE){
                qzs[toX][toY].s = ChessUtil.STATUS_EXCHANGE;
            }else{
                qzs[toX][toY].s = ChessUtil.STATUS_CAN_EAT;
            }
        }
        return data.length == 5 ? new int[]{toX, toY, data[4]} : new int[]{toX, toY};//data[4]升兵角色/王车易位标识
    }

    private void resetStatus() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (null == qzs[i][j]) {
                    continue;
                }
                if (qzs[i][j].s == ChessUtil.STATUS_CAN_GO) {
                    qzs[i][j] = null;
                } else {
                    qzs[i][j].s = ChessUtil.STATUS_NORMAL;
                }
            }
        }
    }

    private void showPromotionWin(final int x, final int y, boolean isWhitePromote) {
        if (null == mPromotionDialog) {
            mPromotionDialog = new PromotionDialog();
        }
        //这里要注意一下，不要把以下设置监听器放在以上的判断中
        mPromotionDialog.setOnRoleSelectListener(new PromotionDialog.OnRoleSelectListener() {
            @Override
            public void onRoleSelected(int roleFlag) {
                completeOneStep(x, y, roleFlag);
            }
        });
        mPromotionDialog.show(((FragmentActivity) getContext()).getSupportFragmentManager(), isWhitePromote);
    }

    @Override
    public void startGame(boolean isIRunFirst) {
        GameDbManager.getInstance().clearChessRecord(DBConstants.TABLE_CHESS);
        lastMove = null;
        isMyTurn = isIRunFirst;
        isGameStarted = true;
        initChessBoard(isIRunFirst);
        canUndoTime = 0;
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
//        before translation,data0=3,data1=6,data2=3,data3=4
//      aPlayChess,oData0=4,oData1=1;data0=4,data1=3
        int fromX = selectedChess.x;
        int fromY = selectedChess.y;
        int toX = data[0];
        int toY = data[1];
        LogUtil.i(TAG, "aPlayChess,fromX=" + fromX + ",fromY=" + fromY + ";toX=" + toX + ",toY=" + toY);

        if (qzs[toX][toY].s != ChessUtil.STATUS_CAN_GO && qzs[toX][toY].s != ChessUtil.STATUS_CAN_EAT && qzs[toX][toY].s != ChessUtil.STATUS_EXCHANGE) {
            return;
        }

        //<-------判断是否王车易位------
        if (qzs[toX][toY].s == ChessUtil.STATUS_EXCHANGE) {
            completeOneStep(toX, toY, ChessUtil.FLAG_EXCHANGE);
            return;
        }
        //------------end-------->
        //游戏结束
        if (qzs[toX][toY].s == ChessUtil.STATUS_CAN_EAT && qzs[toX][toY].f() == ChessUtil.KING) {
            gameOver(qzs[toX][toY].c == myColor ? mOnPlayListener.FLAG_OPPO_WIN : mOnPlayListener.FLAG_I_WIN);
            completeOneStep(toX, toY, -1);
            return;
        }
        //游戏没结束的话判断是否是兵
        if (qzs[fromX][fromY].f() == ChessUtil.PAWN) {
            if (toY == 0) {
                //我方升兵
                showPromotionWin(toX, toY, myColor == ChessUtil.WHITE_CHESS);
                return;
            } else if (toY == 7) {
                //对方升兵
                if (gameMode != GameConstants.MODE_ONLINE) {
                    showPromotionWin(toX, toY, oppoColor == ChessUtil.WHITE_CHESS);
                    return;
                } else {
                    //对方升兵
                    completeOneStep(toX, toY, data[2]);
                    ToastUtil.toast("对方升兵!");
                    return;
                }
            }
        }
        //游戏没结束或者没升兵的话就会执行到这里
        completeOneStep(toX, toY, -1);
    }

    public void initChessBoard(boolean isIFirst) {
        if (isIFirst) {
            myColor = ChessUtil.WHITE_CHESS;
            oppoColor = ChessUtil.BLACK_CHESS;
        } else {
            myColor = ChessUtil.BLACK_CHESS;
            oppoColor = ChessUtil.WHITE_CHESS;
        }
        //如果是在线模式并且我是后手的话要更改棋局
        initChessArray((gameMode == GameConstants.MODE_ONLINE && !isIFirst));
    }

    public void undoOnce(boolean isMyUndo) {
        Object[] lastTwoRecords = GameDbManager.getInstance().getLastTwoRecords(DBConstants.TABLE_CHESS, new RecordParser());
        if (null == lastTwoRecords) {
            LogUtil.i(TAG, "null = lastTwoRecords");
            return;
        }
        lastMove = (ChessRecord) lastTwoRecords[0];
        LogUtil.e(TAG, "lastMove=" + lastMove);
        if (lastMove == null) {
            return;
        }

        qzs[lastMove.fromX][lastMove.fromY] = new ChessPoint(lastMove.fromF, lastMove.fromX, lastMove.fromY, lastMove.fromC);
        qzs[lastMove.fromX][lastMove.fromY].setFirstMove(lastMove.fromFirstMove);
        if (lastMove.toF == -1) {
            qzs[lastMove.toX][lastMove.toY] = null;
        } else {
            qzs[lastMove.toX][lastMove.toY] = new ChessPoint(lastMove.toF, lastMove.toX, lastMove.toY, lastMove.toC);
            qzs[lastMove.toX][lastMove.toY].setFirstMove(lastMove.toFirstMove);
        }

        if(lastMove.isExchange){//如果是王车易位的话要移除王车之间的棋子
            int fromX,toX;
            if(lastMove.fromX>lastMove.toX){//车在王的左侧
                fromX=lastMove.toX;
                toX=lastMove.fromX;
            }else{//车在王的右侧
                fromX=lastMove.fromX;
                toX=lastMove.toX;
            }
            for(int i=fromX+1;i<toX;i++){
                if(qzs[i][lastMove.fromY]==null){
                    continue;
                }
                qzs[i][lastMove.fromY]=null;
            }
        }
        GameDbManager.getInstance().deleteChessRecord(DBConstants.TABLE_CHESS, lastMove.id);

        lastMove = (ChessRecord) lastTwoRecords[1];
        isMyTurn = !isMyTurn;
        canUndoTime--;
        resetStatus();
    }
}
