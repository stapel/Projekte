package chessGUI;

import java.awt.Point;

import chessGUI.JChessBoard.JChessPiece;

public class Chess {
	
	
	private ChessPiece[][] board;
	private int TILES = 8;
	private boolean whiteMove = true;
	
	/************************************************************/
	/**
	 *  Enum for types of Chess-pieces
	 *  
	 *  incorperates "name" as unicode-characters of chess figures
	 *  and "notation" as the english standard short-notation  
	 */
	public enum ChessPieceType {
		WHITE_PAWN("\u2659", "", true),
		BLACK_PAWN("\u265f", "", false),
		WHITE_KNIGHT("\u2658", "N", true),
		BLACK_KNIGHT("\u265e", "N", false),
		WHITE_BISHOP("\u2657", "B", true),
		BLACK_BISHOP("\u265d", "B", false),
		WHITE_ROOK("\u2656", "R", true),
		BLACK_ROOK("\u265c", "R", false),
		WHITE_QUEEN("\u2655", "Q", true),
		BLACK_QUEEN("\u265b", "Q", false),
		WHITE_KING("\u2654", "K", true),
		BLACK_KING("\u265a", "K", false);
		
		private final String  name;
		private final String  notation;
		private final boolean white;
		
		private ChessPieceType(String name, String notation, boolean isWhite) {
			this.name = name;
			this.notation = notation;
			this.white = isWhite;
		}

		// Getter for unicode-repesentation of the specific figure
		public String getName() {
			return this.name;
		}

		// Getter for notation of the specific figure
		public String getNotation() {
			return this.notation;
		}
		
		public boolean isWhite() {
			return white;
		}
		
		public boolean isBlack() {
			return ! white;
		}
	}
	
	public class ChessPiece {
		private ChessPieceType type = null;
		private int moves = 0;
		
		public ChessPiece(ChessPieceType type) {
			this.type = type;
		}
		
		public ChessPieceType getType() {
			return this.type;
		}
		
		public boolean hasMoved() {
			return (moves > 0);
		}
		
		public void moved() {
			++this.moves;
		}
		
		public void promote(ChessPieceType type) {
			this.type = type;
		}
	}
	
	private ChessPiece[][] getBoard() {
		if (this.board == null) {
			this.board = new ChessPiece[TILES][TILES];
		}
		return this.board;
	}
	
	private void setPiece(int y, int x, ChessPiece piece) {
		getBoard()[y][x] = piece;
	}

	private void clearBoard() {
		this.board = null;
	}
	
	public ChessPiece at(int y, int x) {
		return getBoard()[y][x];
	}
	
	// move a piece on the board via notation (e.g. "e2e4")
	public boolean move(String str) {
		if (str.equals("O-O") || str.equals("0-0-0")) {
			return false;
		} else {
			int x =  (str.charAt(0) - 'a');
			int y =  TILES - (str.charAt(1) - '1') - 1;
			int xTo =  (str.charAt(2) - 'a');
			int yTo = TILES - (str.charAt(3) - '1') - 1;
			return move(y, x, yTo, xTo);
		}
	}
	
	// move a piece on the board
	private boolean move(int y, int x, int yTo, int xTo) {
		ChessPiece piece = getBoard()[y][x];
		if (piece == null)
			return false;
		getBoard()[yTo][xTo] = piece; 
		piece.moved();
		return true;
	}

	public void setupBoard() {
		clearBoard();
		
		for (int col = 0; col < TILES; col++) {
			setPiece(1, col, new ChessPiece(ChessPieceType.BLACK_PAWN));
			setPiece(TILES - 2, col, new ChessPiece(ChessPieceType.WHITE_PAWN));
		}
		setPiece(0, 0, new ChessPiece(ChessPieceType.BLACK_ROOK));
		setPiece(0, 7, new ChessPiece(ChessPieceType.BLACK_ROOK));
		setPiece(7, 0, new ChessPiece(ChessPieceType.WHITE_ROOK));
		setPiece(7, 7, new ChessPiece(ChessPieceType.WHITE_ROOK));
	
		setPiece(0, 1, new ChessPiece(ChessPieceType.BLACK_KNIGHT));
		setPiece(0, 6, new ChessPiece(ChessPieceType.BLACK_KNIGHT));
		setPiece(7, 1, new ChessPiece(ChessPieceType.WHITE_KNIGHT));
		setPiece(7, 6, new ChessPiece(ChessPieceType.WHITE_KNIGHT));
	
		setPiece(0, 2, new ChessPiece(ChessPieceType.BLACK_BISHOP));
		setPiece(0, 5, new ChessPiece(ChessPieceType.BLACK_BISHOP));
		setPiece(7, 2, new ChessPiece(ChessPieceType.WHITE_BISHOP));
		setPiece(7, 5, new ChessPiece(ChessPieceType.WHITE_BISHOP));
	
		setPiece(0, 3, new ChessPiece(ChessPieceType.BLACK_QUEEN));
		setPiece(0, 4, new ChessPiece(ChessPieceType.BLACK_KING));
		setPiece(7, 3, new ChessPiece(ChessPieceType.WHITE_QUEEN));
		setPiece(7, 4, new ChessPiece(ChessPieceType.WHITE_KING));
	}
}
