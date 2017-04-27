package chessGUI;

import java.awt.EventQueue;
import javax.swing.JFrame;
import java.awt.BorderLayout;

/**
 *  Chess GUI
 * 
 * @author argon
 */
public class ChessGUI {

	private JFrame frame;
	private JChessBoard board;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChessGUI window = new ChessGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ChessGUI() {
		initialize();

		// show annotations)
		getBoard().showAnnotations(true);
		
		// setup board config
		getBoard().setLegalityCheck(true);
		getBoard().setShowMoves(true);
		
		// put pieces on the board
		getBoard().setupBoard();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 600, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		frame.getContentPane().add(getBoard(), BorderLayout.CENTER);
	}

	private JChessBoard getBoard() {
		if (board == null) {
			board = new JChessBoard();
		}
		return board;
	}
}
