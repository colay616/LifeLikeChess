package com.ue.army_chess.entity;

import com.ue.army_chess.util.ArmyChessUtil;

/**
 *
 */
public class FMArmyRecord {
    public int id;
	public int fromF;
	public int fromX;
	public int fromY;
	public int fromC;
	public int toF;
	public int toX;
	public int toY;
	public int toC;

	public int fromBY(){
		return fromY>5?fromY+1:fromY;
	}
	public int toBY(){
		return toY>5?toY+1:toY;
	}

	public FMArmyRecord(FMArmyChess from, FMArmyChess to){
		this.fromF=from.getF();
		this.fromX=from.x;
		this.fromY=from.y;
		this.fromC=from.c;
		this.toF=to.getF();
		this.toX=to.x;
		this.toY=to.y;
		this.toC=to.c;
	}

    public FMArmyChess getFrom(){
        return new FMArmyChess(fromF,fromX,fromY,fromC,ArmyChessUtil.STAT_NORMAL);
    }

    public FMArmyChess getTo(){
        return new FMArmyChess(toF,toX,toY,toC,ArmyChessUtil.STAT_NORMAL);
    }

	@Override
	public String toString() {
		return String.format("{'fromF':%d,'fromX':%d,'fromY':%d,'fromC':%d,'toF':%d,'toX':%d,'toY':%d,'toC':%d}",
				fromF,fromX,fromY,fromC,toF,toX,toY,toC);
	}
}