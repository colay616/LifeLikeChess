package com.hyphenate.easeui.game.base;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.hyphenate.easeui.game.iterface.OnPlayListener;
import com.hyphenate.easeui.game.iterface.OnUndoListener;

/**
 * Created by hawk on 2017/1/15.
 */

public abstract class BaseGameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    protected static final String TAG = BaseGameView.class.getSimpleName();
    protected boolean isGameStarted = false;
    protected boolean isMyTurn;
    protected int gameMode;
    protected int canUndoTime;
    protected OnPlayListener mOnPlayListener;
    protected OnUndoListener mOnUndoListener;//人机模式

    protected Thread renderThread;
    protected SurfaceHolder renderSHolder;
    protected boolean isToRender;//是否继续渲染

    public BaseGameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);
    }

    public void setOnUndoListener(OnUndoListener onUndoListener) {
        mOnUndoListener = onUndoListener;
    }

    public boolean isMyTurn() {
        return isMyTurn;
    }

    public boolean isAllowedToUndo() {
        return canUndoTime > 0;
    }

    public boolean isGameStarted() {
        return isGameStarted;
    }

    public void undoChess(boolean isMyUndo) {
        if ((isMyUndo && isMyTurn) || (!isMyUndo && !isMyTurn)) {
            undoOnce(true);//我退一步
            undoOnce(false);//对方退一步
        } else if (isMyUndo && !isMyTurn) {
            undoOnce(!isMyTurn);
        } else if (!isMyUndo && isMyTurn) {
            undoOnce(isMyTurn);
        }
    }

    public void surrender(boolean isMySurrender) {
        gameOver(isMySurrender ? OnPlayListener.FLAG_OPPO_WIN : OnPlayListener.FLAG_I_WIN);
    }

    public void escape() {
        isGameStarted = false;
    }

    public void drawChess(boolean isMyDraw) {
        gameOver(OnPlayListener.FLAG_DRAW);
    }

    public void gameOver(int resultFlag) {
        isGameStarted = false;
        mOnPlayListener.onGameOver(resultFlag);
    }

    public void setGameMode(int mode) {
        this.gameMode = mode;
    }

    public void setPlayListener(OnPlayListener playListener) {
        mOnPlayListener = playListener;
    }

    @Override
    public void run() {
        Canvas canvas = null;
        while (isToRender) {
            try {
                canvas = renderSHolder.lockCanvas();
                synchronized (renderSHolder) {
                    drawBoard(canvas);
                    drawPieces(canvas);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != canvas) {
                    renderSHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        renderSHolder = getHolder();
        renderThread = new Thread(this);
        isToRender = true;
        renderThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isToRender = false;
        try {
            renderThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public abstract void initChessBoard(boolean isIFirst);

    public abstract void startGame(boolean isIFirst);

    public abstract void playChess(int[] data);

    public abstract void drawBoard(Canvas canvas);

    public abstract void drawPieces(Canvas canvas);

    public abstract void undoOnce(boolean isMyUndo);
}
