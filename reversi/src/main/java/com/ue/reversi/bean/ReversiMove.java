package com.ue.reversi.bean;

/**
 * 标识棋盘中的一个位置
 */
public class ReversiMove {
    public int x;
    public int y;

    public ReversiMove(int row, int col) {
        this.x = row;
        this.y = col;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + y;
        result = prime * result + x;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ReversiMove other = (ReversiMove) obj;
        if (y != other.y)
            return false;
        if (x != other.x)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("{'x':%d,'y':%d}", x, y);//JSONObject格式
    }
}
