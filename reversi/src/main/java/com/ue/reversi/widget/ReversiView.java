package com.ue.reversi.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.hyphenate.easeui.game.GameConstants;
import com.hyphenate.easeui.game.base.BaseGameView;
import com.hyphenate.easeui.game.db.DBConstants;
import com.hyphenate.easeui.game.db.GameDbManager;
import com.hyphenate.easeui.game.iterface.OnPlayListener;
import com.ue.common.util.DisplayUtil;
import com.ue.common.util.LogUtil;
import com.ue.reversi.R;
import com.ue.reversi.bean.ReversiMove;
import com.ue.reversi.bean.ReversiRecord;
import com.ue.reversi.bean.Statistic;
import com.ue.reversi.util.Rule;
import com.ue.reversi.util.Util;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import static android.os.Build.VERSION_CODES.M;


/**
 * 棋盘界面
 */
public class ReversiView extends BaseGameView {
    public static final byte BLACK = -1;//标识黑子
    public static final byte NULL = 0;//标识空位
    public static final byte WHITE = 1;//标识白子

    private Paint mPaint = new Paint();
    private float mPanelWidth;
    private float squareWidth;//棋格边长

    private byte[][] chessBoard;
    private int[][] index;

    private Bitmap[] images;
    private Bitmap background;
    private byte myColor;
    private byte oppoColor;

    private ReversiRecord lastMove;//记录上一次的位置和当前位置，描绘焦点

    public ReversiView(Context context) {
        this(context, null, 0);
    }

    public ReversiView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReversiView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mPanelWidth = DisplayUtil.getScreenWidth(context) * 0.9f;
        squareWidth = mPanelWidth / 9;
        images = new Bitmap[22];
        loadChesses(context);

        background = BitmapFactory.decodeResource(getResources(), R.drawable.game_bg_mood);
        background = Bitmap.createScaledBitmap(background, (int) mPanelWidth, (int) mPanelWidth, false);
        initChessBoard(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) mPanelWidth, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) mPanelWidth, MeasureSpec.EXACTLY);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void run() {
        Canvas canvas = null;
        while (isToRender) {
            long startTime = System.currentTimeMillis();
            update();
            long endTime = System.currentTimeMillis();

            try {
                canvas = renderSHolder.lockCanvas();
                synchronized (renderSHolder) {
                    drawBoard(canvas);
                    drawPieces(canvas);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    renderSHolder.unlockCanvasAndPost(canvas);
                }
            }
            try {
                if ((endTime - startTime) <= 100) {
                    Thread.sleep(100 - (endTime - startTime));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public byte[][] getChessBoard() {
        return chessBoard;
    }

    public byte getMyColor() {
        return myColor;
    }

    public byte getOppoColor() {
        return oppoColor;
    }

    private int updateIndex(int index, int color) {

        if (index == 0 || index == 11) {
            return index;
        } else if (index >= 1 && index <= 10 || index >= 12 && index <= 21) {
            return (index + 1) % 22;
        } else {
            return defaultIndex(color);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isGameStarted) {
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (gameMode != GameConstants.MODE_DOUBLE && !isMyTurn) {
                return false;
            }
            float x = event.getX();
            float y = event.getY();

            int row = (int) (y / squareWidth - 0.5f);
            int col = (int) (x / squareWidth - 0.5f);

            playChess(isMyTurn, row, col);
        }
        return true;
    }

    /**
     * 保存行棋记录
     *
     * @param x
     * @param y
     * @param color
     * @param moves
     */
    private void saveReversiRecord(int x, int y, int color, List<ReversiMove> moves) {
        lastMove = new ReversiRecord(x, y, color, moves);

        boolean result = GameDbManager.getInstance().saveChessRecord(DBConstants.TABLE_REVERSI, lastMove.toString());
        if (result) {//保存数据不成功，不能悔棋了
            canUndoTime++;
        } else {
            canUndoTime = -100;
        }
    }

    /**
     * 返回上一步
     *
     * 有时候是连下的，这种情况也要考虑进去
     */
    public void undoOnce(boolean isMyUndo) {
//        Object[] lastTwoRecords = GameDbManager.getInstance().getLastTwoRecords(DBConstants.TABLE_REVERSI, new RecordParser());
//        if (null == lastTwoRecords) {
//            return;
//        }
////            LogUtil.i(TAG,"changedChess="+lastChessMove.changedChess);
//        //changedChess=[{'x':3,'y':3}]
//        lastMove = (ReversiRecord) lastTwoRecords[0];
//        //1、反转
//        byte originalColor = lastMove.c == BLACK ? WHITE : BLACK;
//        List<ReversiMove> changedChess = lastMove.changedChess;
//        ReversiMove reversiMove;
//        for (int i = 0, len = changedChess.size(); i < len; i++) {
//            reversiMove = changedChess.get(i);
//            int row = reversiMove.x;
//            int col = reversiMove.y;
//            chessBoard[row][col] = originalColor;
//            if (originalColor == WHITE) {
//                index[row][col] = 11;
//            } else if (originalColor == BLACK) {
//                index[row][col] = 0;
//            }
//        }
//        //2、界面移除
//        chessBoard[lastMove.x][lastMove.y] = NULL;
//        //3、数据库移除
//        GameDbManager.getInstance().deleteChessRecord(DBConstants.TABLE_REVERSI, lastMove.id);
//        lastMove = (ReversiRecord) lastTwoRecords[1];
//
//        isMyTurn = !isMyTurn;
//        canUndoTime--;
//
//        if (null != mOnUndoListener) {
//            mOnUndoListener.onUndo(null);
//        }
    }

    private void playChess(boolean isIPlay, int row, int col) {
        byte ownerColor = isIPlay ? myColor : oppoColor;
        if (!Rule.isLegalMove(chessBoard, new ReversiMove(row, col), ownerColor)) {
            return;
        }
        /**
         * 玩家走步
         */
        ReversiMove move = new ReversiMove(row, col);
        List<ReversiMove> moves = Rule.move(chessBoard, move, ownerColor);
        move(chessBoard, moves, move);

        //保存一个记录
        saveReversiRecord(row, col, ownerColor, moves);

        isMyTurn = !isIPlay;

        int legalMovesOfAI = Rule.getLegalMoves(chessBoard, oppoColor).size();
        int legalMovesOfPlayer = Rule.getLegalMoves(chessBoard, myColor).size();

        if (legalMovesOfAI == 0 && legalMovesOfPlayer > 0) {
            isMyTurn = true;
        } else if (legalMovesOfAI == 0 && legalMovesOfPlayer == 0) {
            Statistic statistic = Rule.analyse(chessBoard, myColor);
            int winOrLoseOrDraw = statistic.PLAYER - statistic.AI;
            int resultFlag = OnPlayListener.FLAG_I_WIN;
            if (winOrLoseOrDraw == 0) {
                resultFlag = OnPlayListener.FLAG_DRAW;
            } else if (winOrLoseOrDraw < 0) {
                resultFlag = OnPlayListener.FLAG_OPPO_WIN;
            }
            mOnPlayListener.onIPlayed(new int[]{row, col});
            gameOver(resultFlag);
            return;
        } else if (legalMovesOfAI > 0 && legalMovesOfPlayer == 0) {
            isMyTurn = false;
        }

        if (isIPlay) {
            mOnPlayListener.onIPlayed(new int[]{row, col});
        } else {
            mOnPlayListener.onOppoPlayed(new int[]{row, col});
        }
    }

    public void move(byte[][] chessBoard, List<ReversiMove> reversed, ReversiMove move) {
        Util.copyBinaryArray(chessBoard, this.chessBoard);
        for (int i = 0; i < reversed.size(); i++) {
            int reverseRow = reversed.get(i).x;
            int reverseCol = reversed.get(i).y;
            if (chessBoard[reverseRow][reverseCol] == WHITE) {
                index[reverseRow][reverseCol] = 1;
            } else if (chessBoard[reverseRow][reverseCol] == BLACK) {
                index[reverseRow][reverseCol] = 12;
            }
        }
        int row = move.x, col = move.y;
        if (chessBoard[row][col] == WHITE) {
            index[row][col] = 11;
        } else if (chessBoard[row][col] == BLACK) {
            index[row][col] = 0;
        }
        canUndoTime++;
    }

    public void update() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (chessBoard[i][j] == NULL)
                    continue;
                index[i][j] = updateIndex(index[i][j], chessBoard[i][j]);
            }
        }

    }

    public int defaultIndex(int color) {
        if (color == WHITE)
            return 11;
        else if (color == BLACK)
            return 0;
        return -1;
    }

    @Override
    public void drawBoard(Canvas canvas) {
        canvas.drawBitmap(background, 0, 0, mPaint);

        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(3);
        for (int i = 0; i < 9; i++) {
//            canvas.drawLine(chessBoardLeft, chessBoardTop + i * squareWidth, chessBoardRight, chessBoardTop + i * squareWidth, mPaint);
//            canvas.drawLine(chessBoardLeft + i * squareWidth, chessBoardTop, chessBoardLeft + i * squareWidth, chessBoardBottom, mPaint);
            canvas.drawLine(0.5f * squareWidth, (i + 0.5f) * squareWidth, mPanelWidth - 0.5f * squareWidth, (i + 0.5f) * squareWidth, mPaint);
            canvas.drawLine((i + 0.5f) * squareWidth, 0.5f * squareWidth, (i + 0.5f) * squareWidth, mPanelWidth - 0.5f * squareWidth, mPaint);
        }
    }

    @Override
    public void drawPieces(Canvas canvas) {
        for (int col = 0; col < M; col++) {
            for (int row = 0; row < M; row++) {
                if (chessBoard[row][col] != NULL) {
                    canvas.drawBitmap(images[index[row][col]], (col + 0.5f) * squareWidth, (row + 0.5f) * squareWidth, mPaint);
                }
            }
        }

        if (null != lastMove) {
            //最后下的一只棋要特殊标注
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Color.BLUE);
            canvas.drawCircle((lastMove.y + 1) * squareWidth, (lastMove.x + 1) * squareWidth, 3 * squareWidth / 16, mPaint);
        }
    }


    public Bitmap loadBitmap(float width, float height, Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, (int) width, (int) height);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 加载棋子图片
     */
    private void loadChesses(Context context) {

        images[0] = loadBitmap(squareWidth, squareWidth, context.getResources().getDrawable(R.drawable.game_black1));
        images[1] = loadBitmap(squareWidth, squareWidth, context.getResources().getDrawable(R.drawable.game_black2));
        images[2] = loadBitmap(squareWidth, squareWidth, context.getResources().getDrawable(R.drawable.game_black3));
        images[3] = loadBitmap(squareWidth, squareWidth, context.getResources().getDrawable(R.drawable.game_black4));
        images[4] = loadBitmap(squareWidth, squareWidth, context.getResources().getDrawable(R.drawable.game_black5));
        images[5] = loadBitmap(squareWidth, squareWidth, context.getResources().getDrawable(R.drawable.game_black6));
        images[6] = loadBitmap(squareWidth, squareWidth, context.getResources().getDrawable(R.drawable.game_black7));
        images[7] = loadBitmap(squareWidth, squareWidth, context.getResources().getDrawable(R.drawable.game_black8));
        images[8] = loadBitmap(squareWidth, squareWidth, context.getResources().getDrawable(R.drawable.game_black9));
        images[9] = loadBitmap(squareWidth, squareWidth, context.getResources().getDrawable(R.drawable.game_black10));
        images[10] = loadBitmap(squareWidth, squareWidth, context.getResources().getDrawable(R.drawable.game_black11));
        images[11] = loadBitmap(squareWidth, squareWidth, context.getResources().getDrawable(R.drawable.game_white1));
        images[12] = loadBitmap(squareWidth, squareWidth, context.getResources().getDrawable(R.drawable.game_white2));
        images[13] = loadBitmap(squareWidth, squareWidth, context.getResources().getDrawable(R.drawable.game_white3));
        images[14] = loadBitmap(squareWidth, squareWidth, context.getResources().getDrawable(R.drawable.game_white4));
        images[15] = loadBitmap(squareWidth, squareWidth, context.getResources().getDrawable(R.drawable.game_white5));
        images[16] = loadBitmap(squareWidth, squareWidth, context.getResources().getDrawable(R.drawable.game_white6));
        images[17] = loadBitmap(squareWidth, squareWidth, context.getResources().getDrawable(R.drawable.game_white7));
        images[18] = loadBitmap(squareWidth, squareWidth, context.getResources().getDrawable(R.drawable.game_white8));
        images[19] = loadBitmap(squareWidth, squareWidth, context.getResources().getDrawable(R.drawable.game_white9));
        images[20] = loadBitmap(squareWidth, squareWidth, context.getResources().getDrawable(R.drawable.game_white10));
        images[21] = loadBitmap(squareWidth, squareWidth, context.getResources().getDrawable(R.drawable.game_white11));
    }

    private static final String INSTANCE = "INSTANCE";
    private static final String INSTANCE_IS_GAME_STARTED = "INSTANCE_IS_GAME_STARTED";
    private static final String INSTANCE_IS_MY_TURN = "INSTANCE_IS_MY_TURN";
    private static final String INSTANCE_MY_COLOR = "INSTANCE_MY_COLOR";
    private static final String INSTANCE_OPPO_COLOR = "INSTANCE_OPPO_COLOR";
    private static final String INSTANCE_GAME_MODE = "INSTANCE_GAME_MODE";
    private static final String INSTANCE_CAN_UNDO_TIME = "INSTANCE_CAN_UNDO_TIME";
    private static final String INSTANCE_CHESSBOARD = "INSTANCE_CHESSBOARD";
    private static final String INSTANCE_INDEX = "INSTANCE_INDEX";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE, super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_IS_GAME_STARTED, isGameStarted);
        bundle.putBoolean(INSTANCE_IS_MY_TURN, isMyTurn);
        bundle.putByte(INSTANCE_MY_COLOR, myColor);
        bundle.putByte(INSTANCE_OPPO_COLOR, oppoColor);
        bundle.putInt(INSTANCE_GAME_MODE, gameMode);
        bundle.putInt(INSTANCE_CAN_UNDO_TIME, canUndoTime);
        bundle.putString(INSTANCE_CHESSBOARD, convertByteArrayToJsonStr(chessBoard));
        bundle.putString(INSTANCE_INDEX, convertIntArrayToJsonStr(index));
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            isGameStarted = bundle.getBoolean(INSTANCE_IS_GAME_STARTED);
            isMyTurn = bundle.getBoolean(INSTANCE_IS_MY_TURN);
            myColor = bundle.getByte(INSTANCE_MY_COLOR);
            oppoColor = bundle.getByte(INSTANCE_OPPO_COLOR);
            gameMode = bundle.getInt(INSTANCE_GAME_MODE);
            canUndoTime = bundle.getInt(INSTANCE_CAN_UNDO_TIME);
            byte[][] chessboardArr = convertJsonStrToByteArray(bundle.getString(INSTANCE_CHESSBOARD));
            if (null != chessboardArr) {
                chessBoard = chessboardArr;
            }
            int[][] indexArr = convertJsonStrToIntArray(bundle.getString(INSTANCE_INDEX));
            if (null != indexArr) {
                index = indexArr;
            }
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    //[[1,2,3,4],[1,2,3,4]]
    public String convertByteArrayToJsonStr(byte[][] chessBoard) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[");
        for (int i = 0; i < 8; i++) {
            buffer.append("[");
            for (int j = 0; j < 8; j++) {
                buffer.append(chessBoard[i][j]).append(",");
            }
            buffer.deleteCharAt(buffer.length() - 1);
            buffer.append("],");
        }
        buffer.deleteCharAt(buffer.length() - 1);
        buffer.append("]");
        return buffer.toString();
    }

    public String convertIntArrayToJsonStr(int[][] index) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[");
        for (int i = 0; i < 8; i++) {
            buffer.append("[");
            for (int j = 0; j < 8; j++) {
                buffer.append(index[i][j]).append(",");
            }
            buffer.deleteCharAt(buffer.length() - 1);
            buffer.append("],");
        }
        buffer.deleteCharAt(buffer.length() - 1);
        buffer.append("]");
        return buffer.toString();
    }

    public byte[][] convertJsonStrToByteArray(String jsonStr) {
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            byte[][] array = new byte[8][8];
            for (int i = 0, iLen = jsonArray.length(); i < iLen; i++) {
                JSONArray insideArray = jsonArray.getJSONArray(i);
                for (int j = 0, jLen = insideArray.length(); j < jLen; j++) {
                    array[i][j] = (byte) insideArray.getInt(j);
//                    LogUtil.i(TAG,"byteij="+array[i][j]);
                }
//                LogUtil.i(TAG,"byteij***********************");
            }
            return array;
        } catch (JSONException e) {
            LogUtil.i(TAG, "convertJsonStrToByteArray,failed:" + e.getMessage());
        }
        return null;
    }

    public int[][] convertJsonStrToIntArray(String jsonStr) {
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            int[][] array = new int[8][8];
            for (int i = 0, iLen = jsonArray.length(); i < iLen; i++) {
                JSONArray insideArray = jsonArray.getJSONArray(i);
                for (int j = 0, jLen = insideArray.length(); j < jLen; j++) {
                    array[i][j] = insideArray.getInt(j);
//                    LogUtil.i(TAG,"intij="+array[i][j]);
                }
//                LogUtil.i(TAG,"byteij***********************");
            }
            return array;
        } catch (JSONException e) {
            LogUtil.i(TAG, "convertJsonStrToIntArray,failed:" + e.getMessage());
        }
        return null;
    }

    public void playChess(int[] data) {
        playChess(false, data[0], data[1]);
    }

    public void startGame(boolean isIFirst) {
        GameDbManager.getInstance().clearChessRecord(DBConstants.TABLE_REVERSI);//清空记录
        lastMove = null;
        isGameStarted = true;
        isMyTurn = isIFirst;
        canUndoTime = 0;
        if (isIFirst) {
            myColor = BLACK;
            oppoColor = WHITE;
        } else {
            oppoColor = BLACK;
            myColor = WHITE;
        }
        initChessBoard(false);
    }

    public void initChessBoard(boolean isIFirst) {
        lastMove = null;
        chessBoard = new byte[M][M];
        index = new int[M][M];

        for (int i = 0; i < M; i++) {
            for (int j = 0; j < M; j++) {
                chessBoard[i][j] = NULL;
            }
        }
        chessBoard[3][3] = WHITE;
        chessBoard[3][4] = BLACK;
        chessBoard[4][3] = BLACK;
        chessBoard[4][4] = WHITE;

        index[3][3] = 11;
        index[3][4] = 0;
        index[4][3] = 0;
        index[4][4] = 11;
    }
}
