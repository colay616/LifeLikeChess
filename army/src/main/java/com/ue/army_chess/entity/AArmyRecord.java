package com.ue.army_chess.entity;

/**
 * 移动棋子的一步走法类
 *
 * 暗棋和明棋可以悔棋
 */
public class AArmyRecord {
    public int id;
	public int fromF;
	public int fromX;
	public int fromY;
	public int fromC;
	public int fromS;
	public int fromOldS;
	public String fromSignedTxt;
	public int toF;
	public int toX;
	public int toY;
	public int toC;
	public int toS;
	public int toOldS;
	public String toSignedTxt;

	public int fromBY(){
		return fromY>5?fromY+1:fromY;
	}
	public int toBY(){
		return toY>5?toY+1:toY;
	}

	public AArmyRecord(AArmyChess fromArmyChess,AArmyChess toArmyChess){
		this.fromF=fromArmyChess.getF();
		this.fromX=fromArmyChess.x;
		this.fromY=fromArmyChess.y;
		this.fromC=fromArmyChess.c;
		this.fromS=fromArmyChess.s;
		this.fromOldS=fromArmyChess.oldS;
		this.fromSignedTxt=fromArmyChess.signedTxt();
		//
		this.toF=toArmyChess.getF();
		this.toX=toArmyChess.x;
		this.toY=toArmyChess.y;
		this.toC=toArmyChess.c;
		this.toS=toArmyChess.s;
		this.toOldS=toArmyChess.oldS;
		this.toSignedTxt=toArmyChess.signedTxt();
	}

    public AArmyChess getFrom(){
        AArmyChess armyChess=new AArmyChess(fromF,fromX,fromY,fromC,fromS);
        armyChess.oldS=fromOldS;
        armyChess.setSign(fromSignedTxt);
        return armyChess;
    }

    public AArmyChess getTo(){
        AArmyChess armyChess=new AArmyChess(toF,toX,toY,toC,toS);
        armyChess.oldS=toOldS;
        armyChess.setSign(toSignedTxt);
        return armyChess;
    }

	@Override
	public String toString() {
		return String.format(
				"{'fromF':%d,'fromX':%d,'fromY':%d,'fromC':%d,'fromS':%d,'fromOldS':%d,'fromSignedTxt':%s,'toF':%d,'toX':%d,'toY':%d,'toC':%d,'toS':%d,'toOldS':%d,'toSignedTxt':%s}",
				fromF,fromX,fromY,fromC,fromS,fromOldS,fromSignedTxt,toF,toX,toY,toC,toS,toOldS,toSignedTxt);
	}
}