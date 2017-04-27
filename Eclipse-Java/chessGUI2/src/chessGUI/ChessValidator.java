package chessGUI;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Vector;
import chessGUI.JChessBoard.JChessTile;
import chessGUI.JChessBoard.JChessPiece;
import chessGUI.Chess.ChessPieceType;

public class ChessValidator {
	/**
	 * @author argon
	 * @param piece Current active chesspiece
	 * @param board Current board with piece
	 * @return Vector<Point> legal moves (without en passant, rochade)
	 */
	
	private static boolean isEmpty(final JChessTile target) {
		return (target.getPiece() == null);
	}
	
	private static boolean isAlly(final JChessPiece me, final JChessTile target) {
		// no piece on tile
		if (isEmpty(target))
			return false;
		 
		return (me.getType().isWhite() == target.getPiece().getType().isWhite());
	}

	private static boolean isEnemy(final JChessPiece me, final JChessTile target) {
		if (isEmpty(target))
			return false;
		
		return (me.getType().isWhite() != target.getPiece().getType().isWhite());
	}
		
	public static boolean isThreatened(final JChessPiece piece, final JChessTile[][] board) {
		ChessPieceType type = piece.getType();
		JChessTile tile = (JChessTile)piece.getParent(); 

		boolean isWhite = type.isWhite();
		Point tPt = new Point(tile.getCol(), tile.getRow());
		
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board.length; x++) {
				// get enemy pieces
				JChessPiece enemy = board[y][x].getPiece(); 
				if (enemy != null && enemy.getType().isWhite() != isWhite) {
					// is it's valid move my piece?
					if (getValidMoves(enemy, board).contains(tPt))
						return true;
				}
			}
		}
		
		return false;
	}
	
	public static Vector<Point> getValidMoves(final JChessPiece piece, final JChessTile[][] board) {
		Vector<Point> result = new Vector<Point>();
		
		JChessTile current = (JChessTile)piece.getParent();

		final Rectangle bounds = new Rectangle(board.length, board[0].length);
		final int x = current.getCol();
		final int y = current.getRow();
		
		Point at = new Point();
		
//		@FunctionalInterface
//		interface checkAndAdd<ChessPiece, ChessTile[][], RetVal> {
//		    public boolean apply(ChessPiece one, ChessTile[][] two);
//		}
		
		switch (piece.getType()) {
			case WHITE_PAWN:
				// double move
				at.x = x; at.y = (bounds.height - 1) - 3;
				if ((y == (bounds.height - 1) - 1) && (isEmpty(board[at.y][at.x])))
					result.add(new Point(at.x, at.y));
				
				// single move
				at.x = x; at.y = y - 1;
				if (isEmpty(board[at.y][at.x]))
					result.add(new Point(at.x, at.y));

				// enemies
				at.y = y - 1;
				for (int i : new int[]{x - 1, x + 1}) {
					at.x = i;
					if (bounds.contains(at) && isEnemy(piece, board[at.y][at.x]))
						result.add(new Point(at));
				}
				break;
			case BLACK_PAWN:
				// double move
				at.x = x; at.y = 3;
				if ((y == 1) && (isEmpty(board[at.y][at.x])))
					result.add(new Point(at.x, at.y));
				
				// single move
				at.x = x; at.y = y + 1;
				if (isEmpty(board[at.y][at.x]))
					result.add(new Point(at.x, at.y));

				// enemies
				at.y = y + 1;
				for (int i : new int[]{x - 1, x + 1}) {
					at.x = i;
					if (bounds.contains(at) && isEnemy(piece, board[at.y][at.x]))
						result.add(new Point(at));
				}
				break;

			case WHITE_KNIGHT:
			case BLACK_KNIGHT:
				for (int atx : new int[] {x - 2, x + 2}) {
					for (int aty : new int[] {y - 1, y + 1}) {
						at.x = atx; at.y = aty;
						if (bounds.contains(at) && ! isAlly(piece, board[at.y][at.x]))
							result.add(new Point(at));
					}
				}
				for (int atx : new int[] {x - 1, x + 1}) {
					for (int aty : new int[] {y - 2, y + 2}) {
						at.x = atx; at.y = aty;
						if (bounds.contains(at) && ! isAlly(piece, board[at.y][at.x]))
							result.add(new Point(at));
					}
				}
				break;

			case WHITE_QUEEN:
			case BLACK_QUEEN:
			case WHITE_ROOK:
			case BLACK_ROOK:

				at.y = y;
				for (at.x = x + 1; at.x < bounds.width; at.x++) {
					if (isAlly(piece, board[at.y][at.x]))
						break;
					if (isEnemy(piece, board[at.y][at.x])) {
						result.add(new Point(at));
						break;
					}
					result.add(new Point(at));
				}
				for (at.x = x - 1; at.x >= 0; at.x--) {
					if (isAlly(piece, board[at.y][at.x]))
						break;
					if (isEnemy(piece, board[at.y][at.x])) {
						result.add(new Point(at));
						break;
					}
					result.add(new Point(at));
				}
		
				at.x = x;
				for (at.y = y + 1; at.y < bounds.height; at.y++) {
					if (isAlly(piece, board[at.y][at.x]))
						break;
					if (isEnemy(piece, board[at.y][at.x])) {
						result.add(new Point(at));
						break;
					}
					result.add(new Point(at));
				}

				for (at.y = y - 1; at.y >= 0; at.y--) {
					if (isAlly(piece, board[at.y][at.x]))
						break;
					if (isEnemy(piece, board[at.y][at.x])) {
						result.add(new Point(at));
						break;
					}
					result.add(new Point(at));
				}

				// fallthrough for queens (i.e. not rooks)
				if (piece.getType() == ChessPieceType.BLACK_ROOK || piece.getType()== ChessPieceType.WHITE_ROOK)
					break;
				
			case WHITE_BISHOP:
			case BLACK_BISHOP:
				
				at.x = x - 1;
				at.y = y - 1;
				while (bounds.contains(at)) {
					if (isAlly(piece, board[at.y][at.x]))
						break;
					if (isEnemy(piece, board[at.y][at.x])) {
						result.add(new Point(at));
						break;
					}
					result.add(new Point(at));
					--at.x;	--at.y;
				}
				
				at.x = x + 1;
				at.y = y + 1;
				while (bounds.contains(at)) {
					if (isAlly(piece, board[at.y][at.x]))
						break;
					if (isEnemy(piece, board[at.y][at.x])) {
						result.add(new Point(at));
						break;
					}
					result.add(new Point(at));
					++at.x;	++at.y;
				}

				at.x = x + 1;
				at.y = y - 1;
				while (bounds.contains(at)) {
					if (isAlly(piece, board[at.y][at.x]))
						break;
					if (isEnemy(piece, board[at.y][at.x])) {
						result.add(new Point(at));
						break;
					}
					result.add(new Point(at));
					++at.x;	--at.y;
				}

				at.x = x - 1;
				at.y = y + 1;
				while (bounds.contains(at)) {
					if (isAlly(piece, board[at.y][at.x]))
						break;
					if (isEnemy(piece, board[at.y][at.x])) {
						result.add(new Point(at));
						break;
					}
					result.add(new Point(at));
					--at.x;	++at.y;
				}
				break;
				
			case WHITE_KING:
			case BLACK_KING:
				for (at.x = x - 1; at.x <= x + 1; at.x++)
					for (at.y = y - 1; at.y <= y + 1; at.y++)
						if (!(at.x == x && at.y == y) && bounds.contains(at))
							if (!isAlly(piece, board[at.y][at.x]))
								result.add(new Point(at));
				break;
		}
		return result;
		
	}
}
