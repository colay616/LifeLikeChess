package com.ue.reversi.bean;


/**
 * 记录极小极大算法过程中的数据
 */
public class MinimaxResult {

	public int mark;
	
	public ReversiMove move;
	
	public MinimaxResult(int mark, ReversiMove move) {
		this.mark = mark;
		this.move = move;
	}
	
}
