package com.ue.reversi.util;

import com.ue.reversi.bean.MinimaxResult;
import com.ue.reversi.bean.ReversiMove;
import com.ue.reversi.widget.ReversiView;

import java.util.List;

/**
 * 算法
 */
public class Algorithm {

	private static final byte WHITE = ReversiView.WHITE;
	private static final byte BLACK = ReversiView.BLACK;

	public static ReversiMove getGoodMove(byte[][] chessBoard, int depth, byte chessColor, int difficulty) {

		if (chessColor == ReversiView.BLACK)
			return max(chessBoard, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, chessColor, difficulty).move;
		else
			return min(chessBoard, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, chessColor, difficulty).move;
	}

	private static MinimaxResult max(byte[][] chessBoard, int depth, int alpha, int beta, byte chessColor, int difficulty) {
		if (depth == 0) {
			return new MinimaxResult(evaluate(chessBoard, difficulty), null);
		}

		List<ReversiMove> legalMovesMe = Rule.getLegalMoves(chessBoard, chessColor);
		if (legalMovesMe.size() == 0) {
			if (Rule.getLegalMoves(chessBoard, (byte)-chessColor).size() == 0) {
				return new MinimaxResult(evaluate(chessBoard, difficulty), null);
			}
			return min(chessBoard, depth, alpha, beta, (byte)-chessColor, difficulty);
		}

		byte[][] tmp = new byte[8][8];
		Util.copyBinaryArray(chessBoard, tmp);
		int best = Integer.MIN_VALUE;
		ReversiMove move = null;

		for (int i = 0; i < legalMovesMe.size(); i++) {
            alpha = Math.max(best, alpha);
            if(alpha >= beta){
                break;
            }
			Rule.move(chessBoard, legalMovesMe.get(i), chessColor);
			int value = min(chessBoard, depth - 1, Math.max(best, alpha), beta, (byte)-chessColor, difficulty).mark;
			if (value > best) {
				best = value;
				move = legalMovesMe.get(i);
			}
			Util.copyBinaryArray(tmp, chessBoard);
		}
		return new MinimaxResult(best, move);
	}

	private static MinimaxResult min(byte[][] chessBoard, int depth, int alpha, int beta, byte chessColor, int difficulty) {
		if (depth == 0) {
			return new MinimaxResult(evaluate(chessBoard, difficulty), null);
		}

		List<ReversiMove> legalMovesMe = Rule.getLegalMoves(chessBoard, chessColor);
		if (legalMovesMe.size() == 0) {
			if (Rule.getLegalMoves(chessBoard, (byte)-chessColor).size() == 0) {
				return new MinimaxResult(evaluate(chessBoard, difficulty), null);
			}
			return max(chessBoard, depth, alpha, beta, (byte)-chessColor, difficulty);
		}

		byte[][] tmp = new byte[8][8];
		Util.copyBinaryArray(chessBoard, tmp);
		int best = Integer.MAX_VALUE;
		ReversiMove move = null;

		for (int i = 0; i < legalMovesMe.size(); i++) {
            beta = Math.min(best, beta);
            if(alpha >= beta){
                break;
            }
			Rule.move(chessBoard, legalMovesMe.get(i), chessColor);
			int value = max(chessBoard, depth - 1, alpha, Math.min(best, beta), (byte)-chessColor, difficulty).mark;
			if (value < best) {
				best = value;
				move = legalMovesMe.get(i);
			}
			Util.copyBinaryArray(tmp, chessBoard);
		}
		return new MinimaxResult(best, move);
	}

	private static int evaluate(byte[][] chessBoard, int difficulty) {
		int whiteEvaluate = 0;
		int blackEvaluate = 0;
		switch (difficulty) {
			case 1:
				for (int i = 0; i < 8; i++) {
					for (int j = 0; j < 8; j++) {
						if (chessBoard[i][j] == WHITE) {
							whiteEvaluate += 1;
						} else if (chessBoard[i][j] == BLACK) {
							blackEvaluate += 1;
						}
					}
				}
				break;
			case 2:
			case 3:
			case 4:
				for (int i = 0; i < 8; i++) {
					for (int j = 0; j < 8; j++) {
						if ((i == 0 || i == 7) && (j == 0 || j == 7)) {
							if (chessBoard[i][j] == WHITE) {
								whiteEvaluate += 5;
							} else if (chessBoard[i][j] == BLACK) {
								blackEvaluate += 5;
							}
						} else if (i == 0 || i == 7 || j == 0 || j == 7) {
							if (chessBoard[i][j] == WHITE) {
								whiteEvaluate += 2;
							} else if (chessBoard[i][j] == BLACK) {
								blackEvaluate += 2;
							}
						} else {
							if (chessBoard[i][j] == WHITE) {
								whiteEvaluate += 1;
							} else if (chessBoard[i][j] == BLACK) {
								blackEvaluate += 1;
							}
						}
					}
				}
				break;
			case 5:
			case 6:
				for (int i = 0; i < 8; i++) {
					for (int j = 0; j < 8; j++) {
						if ((i == 0 || i == 7) && (j == 0 || j == 7)) {
							if (chessBoard[i][j] == WHITE) {
								whiteEvaluate += 5;
							} else if (chessBoard[i][j] == BLACK) {
								blackEvaluate += 5;
							}
						} else if (i == 0 || i == 7 || j == 0 || j == 7) {
							if (chessBoard[i][j] == WHITE) {
								whiteEvaluate += 2;
							} else if (chessBoard[i][j] == BLACK) {
								blackEvaluate += 2;
							}
						} else {
							if (chessBoard[i][j] == WHITE) {
								whiteEvaluate += 1;
							} else if (chessBoard[i][j] == BLACK) {
								blackEvaluate += 1;
							}
						}
					}
				}
				blackEvaluate = blackEvaluate * 2 + Rule.getLegalMoves(chessBoard, BLACK).size();
				whiteEvaluate = whiteEvaluate * 2 + Rule.getLegalMoves(chessBoard, WHITE).size();
				break;
			case 7:
			case 8:
				/**
				 * 稳定度
				 */
				for (int i = 0; i < 8; i++) {
					for (int j = 0; j < 8; j++) {
						int weight[] = new int[] { 2, 4, 6, 10, 15 };
						if (chessBoard[i][j] == WHITE) {
							whiteEvaluate += weight[getStabilizationDegree(chessBoard, new ReversiMove(i, j))];
						} else if (chessBoard[i][j] == BLACK) {
							blackEvaluate += weight[getStabilizationDegree(chessBoard, new ReversiMove(i, j))];
						}
					}
				}
				/**
				 * 行动力
				 */
				blackEvaluate += Rule.getLegalMoves(chessBoard, BLACK).size();
				whiteEvaluate += Rule.getLegalMoves(chessBoard, WHITE).size();
				break;
		}
		return blackEvaluate - whiteEvaluate;
	}

	private static int getStabilizationDegree(byte[][] chessBoard, ReversiMove move) {
		int chessColor = chessBoard[move.x][move.y];
		int drow[][], dcol[][];
		int row[] = new int[2], col[] = new int[2];
		int degree = 0;

		drow = new int[][] { { 0, 0 }, { -1, 1 }, { -1, 1 }, { 1, -1 } };
		dcol = new int[][] { { -1, 1 }, { 0, 0 }, { -1, 1 }, { -1, 1 } };

		for (int k = 0; k < 4; k++) {
			row[0] = row[1] = move.x;
			col[0] = col[1] = move.y;
			for (int i = 0; i < 2; i++) {
				while (Rule.isLegal(row[i] + drow[k][i], col[i] + dcol[k][i])
						&& chessBoard[row[i] + drow[k][i]][col[i] + dcol[k][i]] == chessColor) {
					row[i] += drow[k][i];
					col[i] += dcol[k][i];
				}
			}
			if (!Rule.isLegal(row[0] + drow[k][0], col[0] + dcol[k][0])
					|| !Rule.isLegal(row[1] + drow[k][1], col[1] + dcol[k][1])) {
				degree += 1;
			} else if (chessBoard[row[0] + drow[k][0]][col[0] + dcol[k][0]] == (-chessColor)
					&& chessBoard[row[1] + drow[k][1]][col[1] + dcol[k][1]] == (-chessColor)) {
				degree += 1;
			}
		}
		return degree;
	}

}
