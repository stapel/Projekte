package chessGUI;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import chessGUI.Chess.ChessPiece;
import chessGUI.Chess.ChessPieceType;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.Vector;


/* TODO
 * Rochade
 	* Beförderung des Bauerns
 	* Schach!
 	* XBoardConnect
 	* en passant
 */

/**
 *  Graphical Chessboard class
 *  
 *  Includes board, tiles and pieces and pieces-type-enum.
 *  @author argon
 */

@SuppressWarnings("serial")
public class JChessBoard extends JPanel implements MouseListener, MouseMotionListener {

	/* Default user-option values */ 
	protected boolean showAnnotation = true;
	protected boolean showMoves = true;
	protected boolean checkLegality = true;
	
	/************************************************************/
	// 8x8 normal chess board
	protected final int TILES_X = 8;
	protected final int TILES_Y = 8;

	// default tile colors
	protected final Color COLOR_BLACK = Color.LIGHT_GRAY;
	protected final Color COLOR_WHITE = Color.WHITE;
	
	// highlighting color (will be darkened/brightened accordingly)
	protected final Color COLOR_HIGHLIGHT = Color.GREEN;

	// Container with annotations
	protected Vector<JLabel> annotations;
	
	// array with board-tiles
	protected JChessTile[][] board;
	
	XBoardConnect gnuchess = new XBoardConnect();
	
	// current game mode
	boolean whiteMoves = true; 
	
	// currently moved piece for drag and drop
	protected JChessPiece movingPiece = null;
	protected Point movingPos = null;
	protected JChessTile movingTile = null;
	
	// highlighted tiles for drag and drop
	protected Vector<Point> highlightedTiles = null;
	
	// pane that contains the chessboard-pane and moved piece
	protected JLayeredPane parentPane;
	// panel where the board is written to
	protected JPanel boardPanel;
	
	/************************************************************/
	/**
	 * Chess Pieces Class
	 */
	class JChessPiece extends JLabel {

		// type
		private ChessPieceType type;
		// has it been moved
		private boolean moved = false;
		
		public JChessPiece(ChessPieceType type) {
			this.type = type;
			setText(type.getName());
			setHorizontalAlignment(CENTER);
		}
		
		public void hasMoved(boolean moved) {
			this.moved = moved; 
		}

		public boolean hasMoved() {
			return this.moved; 
		}
		
		// Get type of chess piece
		public ChessPieceType getType() {
			return this.type;
		}
		
		// Set font befitting to surrounding cell
		@Override
		public void paint(Graphics g) {
			if (getFont().getSize() != getHeight())
				setFont(getFont().deriveFont((float)getHeight()));
			super.paint(g);
		} 
	}

	/************************************************************/
	/**
	 * Tiles class 
	 */
	class JChessTile extends JPanel {
		protected int row;
		protected int col;
		JChessPiece piece = null;
		
		public JChessTile(int row, int col, Color color) {
			this.setLayout(new BorderLayout());
			this.row = row;
			this.col = col;
			setBackground(color);
			setOpaque(true);
		}
		
		public void setPiece(ChessPieceType type) {
			piece = new JChessPiece(type);
			add(piece);
		}
		public void unsetPiece() {
			if (piece != null) {
				remove(piece);
				piece = null;
			}
		}
		
		public void setPiece(JChessPiece piece) {
			unsetPiece();
			this.piece = piece;
			add(piece, BorderLayout.CENTER);
		}
		
		public JChessPiece getPiece() {
			return piece;
		}
		
		public int getRow() {
			return row;
		}
		
		public int getCol() {
			return col;
		}
	}
	
	/************************************************************/
	/**
	 * Enforce square aspect ratio of a JPanel and it's parent
	 * inside of a ChessBoard
	 * @author argon
	 *
	 */
	class PanelRestricter extends ComponentAdapter {
		public void componentResized(ComponentEvent e) {
			JChessBoard	outer = (JChessBoard)e.getSource();
			JPanel board = outer.getBoard();
			
			int min = Math.min(outer.getWidth(), outer.getHeight());
			if (! board.getSize().equals(new Dimension(min, min)))  {
					board.setSize(min, min);
					board.getParent().setSize(min, min);
			}
		}
	}

	/************************************************************/
	/* Option Setters and Getters */

	public void setShowMoves(boolean visible) {
		showMoves = visible; 
	}

	public void setLegalityCheck(boolean checkit) {
		checkLegality = checkit; 
	}
	
	public void showAnnotations(boolean visible) {
		for (JLabel label : getAnno()) {
			label.setVisible(visible);
		}
	}
	
	
	/************************************************************/
	/* private functions */
	// get vector with annotations 
	private Vector<JLabel> getAnno() {
		if (annotations == null)
			annotations = new Vector<JLabel>();
		return annotations;
	}
	
	// get actual board
	private JPanel getBoard() {
		if (boardPanel == null) {
			boardPanel = new JPanel();
		}
		return boardPanel;
	}
	
	// get layered pane
	private JLayeredPane getPane() {
		if (parentPane == null) {
			parentPane = new JLayeredPane();
		}
		return parentPane;
	}
	
	// Accessor function for board array
	private JChessTile[][] getBoardArr() {
		if (board == null) {
			board = new JChessTile[this.TILES_Y][this.TILES_X];
		}
		return board;
	}

	// Helper Function to add Annotation fields and save their labels in a list
	private void addAnnotation(String name, GridBagConstraints constraints) {
		JLabel label = new JLabel(name, SwingConstants.CENTER);
		label.setVisible(false);
		getAnno().add(label);
		getBoard().add(label, constraints);
	}
	
	// translate coordinates into annotationstring
	private String yxToStr(int y, int x) {
		return String.valueOf((char)('a' + x)) + y;
	}

	// show possible moves for piece
	private void showHighlights(JChessPiece piece) {
		hideHighlights();
//		highlightedTiles = piece.getMoves();
		highlightedTiles = ChessValidator.getValidMoves(piece, getBoardArr());
		for (Point pos: highlightedTiles) {
			getBoardArr()[pos.y][pos.x].setBackground(
					(pos.x + pos.y) % 2 == 0 ?
							COLOR_HIGHLIGHT.brighter() : COLOR_HIGHLIGHT.darker());
		}
	}

	// hide possible moves for piece
	private void hideHighlights() {
		if (highlightedTiles != null) {
			for (Point pos : highlightedTiles) {
				getBoardArr()[pos.y][pos.x].setBackground(
						(pos.x + pos.y) % 2 == 0 ? COLOR_WHITE : COLOR_BLACK);
			}
		}
	}
	
	/************************************************************/
	/* publicly accessible functions */

	// get String in notation with figure	
	public String getMoveStrLong(ChessPieceType type, int y, int x, int yTo, int xTo) {
		String result = getMoveStr(y, x, yTo, xTo);
		if (! result.isEmpty())
			result = type.getNotation() + result;
		return result;
	}

	// get String in notation
	public String getMoveStr(int y, int x, int yTo, int xTo) {
		String result = yxToStr(y, x);
		if (getBoardArr()[yTo][xTo].getPiece() != null) {
			System.out.println(getBoardArr()[yTo][xTo].getPiece());
			result += "x";
		}
		result += yxToStr(yTo,xTo);
		return result;
	}
	
	// move a piece on the board via notation (e.g. "e2e4")
	public void movePiece(String str) {
		if (str.equals("O-O")) {
			int y = whiteMoves ? 7 : 0;
			getBoardArr()[y][6].setPiece(getBoardArr()[y][4].getPiece());
			getBoardArr()[y][5].setPiece(getBoardArr()[y][7].getPiece());
			unsetPiece(y, 4); unsetPiece(y, 7);
		} else if (str.equals("O-O-O")) {
			int y = whiteMoves ? 7 : 0;
			getBoardArr()[y][2].setPiece(getBoardArr()[y][4].getPiece());
			getBoardArr()[y][3].setPiece(getBoardArr()[y][0].getPiece());
			unsetPiece(y, 4); unsetPiece(y, 0);
		} else {
			int x =  (str.charAt(0) - 'a');
			int y =  TILES_Y - (str.charAt(1) - '1') - 1;
			int xTo =  (str.charAt(2) - 'a');
			int yTo = TILES_X - (str.charAt(3) - '1') - 1;
			movePiece(y, x, yTo, xTo);
		}
		repaint();
	}
	
	// move a piece on the board
	public void movePiece(int y, int x, int yTo, int xTo) {
		JChessPiece piece = getBoardArr()[y][x].getPiece();
		if (piece == null)
			return;
		if (! checkLegality || ChessValidator.getValidMoves(piece, board).contains(new Point(xTo, yTo))) {
			getBoardArr()[y][x].unsetPiece();
			getBoardArr()[yTo][xTo].setPiece(piece);
			piece.hasMoved(true);
			whiteMoves = ! whiteMoves;
		}
	}
	
	// remove a piece from the board 
	public void unsetPiece(int y, int x) {
		getBoardArr()[y][x].unsetPiece();
	}

	// put a piece on the board 
	public void setPiece(int y, int x, ChessPieceType type) {
		getBoardArr()[y][x].setPiece(type);
	}
	
	// Setup regular chessboard with pieces
	public void setupBoard() {
		whiteMoves = true;
		clearBoard();
		for (int col = 0; col < this.TILES_X; col++) {
				setPiece(1, col, ChessPieceType.BLACK_PAWN);
				setPiece(TILES_Y - 2, col, ChessPieceType.WHITE_PAWN);
		}
		
		setPiece(0,0, ChessPieceType.BLACK_ROOK);
		setPiece(0,7, ChessPieceType.BLACK_ROOK);
		setPiece(7,0, ChessPieceType.WHITE_ROOK);
		setPiece(7,7, ChessPieceType.WHITE_ROOK);
		
		setPiece(0,1,ChessPieceType.BLACK_KNIGHT);
		setPiece(0,6,ChessPieceType.BLACK_KNIGHT);
		setPiece(7,1,ChessPieceType.WHITE_KNIGHT);
		setPiece(7,6,ChessPieceType.WHITE_KNIGHT);
		
		setPiece(0,2,ChessPieceType.BLACK_BISHOP);
		setPiece(0,5,ChessPieceType.BLACK_BISHOP);
		setPiece(7,2,ChessPieceType.WHITE_BISHOP);
		setPiece(7,5,ChessPieceType.WHITE_BISHOP);
		
		setPiece(0,3,ChessPieceType.BLACK_QUEEN);
		setPiece(0,4,ChessPieceType.BLACK_KING);
		setPiece(7,3,ChessPieceType.WHITE_QUEEN);
		setPiece(7,4,ChessPieceType.WHITE_KING);
	}
	
	// clear board of pieces
	public void clearBoard() {
		getBoardArr();
		for (int row = 0; row < TILES_Y; row++)
			for (int col = 0; col < TILES_X; col++)
				board[row][col].unsetPiece();
	}

	// ChessBoard Constructor
	public JChessBoard() {
		//// Setup board and annotations
		setLayout(new BorderLayout());
		// Add board to the Layered Pane
		getPane().add(getBoard(), JLayeredPane.DEFAULT_LAYER);
		
		// Add Upper Layered Pane
		add(getPane());

		// GridBagLayout and default constraints
		GridBagLayout layout = new GridBagLayout();
		getBoard().setLayout(layout);

		getBoard().addMouseListener(this);

		setLayout(null);
		addComponentListener(new PanelRestricter());

		//  Constraints for elements on the board
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
		
		// Annotation columns
		for (int col = 0; col < TILES_X; col++) {
			String name = String.valueOf((char)('a' + col));
			constraints.gridx = col + 1;
			
			// first line
			constraints.gridy = 0;
			addAnnotation(name, constraints);
			
			// last line
			constraints.gridy = TILES_Y + 2;
			addAnnotation(name, constraints);
		}

		// Annotation rows
		for (int row = 0; row < TILES_Y; row++) {
			String name = String.valueOf(TILES_Y - row);
			constraints.gridy = row + 1;
			
			// first col
			constraints.gridx = 0;
			addAnnotation(name, constraints);
			
			// last col
			constraints.gridx = this.TILES_X + 2;
			addAnnotation(name, constraints);
		}
		
		/// Tiles
		// Setup constraints for the tiles
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1. / TILES_X;
		constraints.weighty = 1. / TILES_Y;
		
		for (int row = 0; row < this.TILES_Y; row++) {
			for (int col = 0; col < this.TILES_X; col++) {
				constraints.gridx = col + 1;
				constraints.gridy = row + 1;
				JChessTile tile = new JChessTile(row, col, (row + col) % 2 == 0 ? COLOR_WHITE : COLOR_BLACK);
				// set preferred size to zero, so it won't be bigger than the other tiles
				tile.setPreferredSize(new Dimension(0,0));
				getBoard().add(tile, constraints);
				getBoardArr()[row][col] = tile;
			}
		}
	}

	/************************************************************/
	// Mouse Overrides
	
	// Mouse pressed, if over piece, will be dragged around the board, shows possible moves
	@Override
	public void mousePressed(MouseEvent e) {
		Component tmp = getBoard().findComponentAt(e.getPoint());
		if (!(tmp instanceof JChessPiece))
			return;
		
		movingPiece = (JChessPiece)tmp;
		movingTile = (JChessTile)tmp.getParent();
		
		if (movingPiece.getType().isWhite() != whiteMoves) {
			movingPiece = null;
			movingTile = null;
			return;
		}
		
		// add motion listener for dragging
		getBoard().addMouseMotionListener(this);
		
		Point pos = movingPiece.getParent().getLocation();
		movingPos = new Point(pos.x - e.getX(), pos.y - e.getY());
		if (showMoves == true)
			showHighlights(movingPiece);
		
		movingPiece.setVisible(false);
		movingTile.unsetPiece();
		getPane().add(movingPiece, JLayeredPane.DRAG_LAYER);
		movingPiece.setLocation(pos);
		movingPiece.setVisible(true);
	}

	// Mouse dragged, update position of piece on pane
	@Override
	public void mouseDragged(MouseEvent e) {
		if (movingPiece == null)
			return;
		Point pos = e.getPoint();
		pos.x += movingPos.x;
		pos.y += movingPos.x;
		movingPiece.setLocation(pos);
	}

	// Mouse released, dragged piece will be put from pane to tile under mouse cursor 
	@Override
	public void mouseReleased(MouseEvent e) {
		if (movingPiece == null)
			return;

		String moved = "";
		
		// clean highlighted fields
		if (showMoves)
			hideHighlights();
		
		// remove piece from layered pane
		movingPiece.setVisible(false);
		getPane().remove(movingPiece);
		movingPiece.setVisible(true);
		
		// get tile under cursor, fallback to origin
		JChessTile tile = movingTile;
		Component tmp = getBoard().getComponentAt(e.getPoint());
		
		if (tmp instanceof JChessTile) {
			tile = (JChessTile)tmp;
			// enforce valid moves
			if (e.isShiftDown() || !checkLegality || highlightedTiles.contains(new Point(tile.getCol(), tile.getRow()))) {
				whiteMoves = ! whiteMoves;
				movingPiece.hasMoved(true);
				moved = getMoveStr(TILES_Y - movingTile.getRow(), movingTile.getCol(),
						TILES_Y - tile.getRow(), tile.getCol());
				
				// XXX Test for printing out move
//				System.out.println(getMoveStr(TILES_Y - movingTile.getRow(), movingTile.getCol(), tile.getRow(), tile.getCol()));
			}
		}
		
		if (moved.isEmpty())
			tile = movingTile;
		
		// add piece to tile
		tile.unsetPiece();
		tile.setPiece(movingPiece);
		
		// remove motion listener again
		getBoard().removeMouseMotionListener(this);
		
		if (! moved.isEmpty()) {
			try {
				repaint();
				gnuchess.sendMove(moved);
				movePiece(gnuchess.readMove());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		// clean up transfer variables
		movingPiece = null;
		movingPos = null;
		movingTile = null;
	}

	// unused listener functions
	
	@Override
	public void mouseMoved(MouseEvent arg0) {}
	@Override
	public void mouseClicked(MouseEvent arg0) {}
	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}
	
	
	
	
}
