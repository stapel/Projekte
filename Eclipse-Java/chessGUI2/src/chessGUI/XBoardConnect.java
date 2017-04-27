package chessGUI;

import java.io.*;

/**
 * Very simple gnuchess xboard interface
 * does not handle illegal moves, nor any other "unforseen" events
 * @author argon
 *
 */

class XBoardConnect {
	private final String MOVESTRING = "My move is : ";

	private BufferedReader input;
	private PrintWriter output;
	private Process xboard;

	public XBoardConnect() {
		try {
			connect();
		} catch (IOException e) {
				e.printStackTrace();
		}
	}

	private void connect() throws IOException {
			xboard = new ProcessBuilder("gnuchess","-e","-x").start();
			input  = new BufferedReader(new InputStreamReader(xboard.getInputStream()));
			output = new PrintWriter(xboard.getOutputStream());
			discardLines(1);
			
			Runtime.getRuntime().addShutdownHook(new Thread() {
			    public void run() {
			        try {
						close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    }
			});
			
	}

	public void sendMove(String move) {
			System.out.println("gnuchess<: " + move);
			output.println(move);
			output.flush();
	}

	public String readMove() throws IOException {
			String line;
			while ((line = input.readLine()) != null) {
				System.out.println("gnuchess>: " + line);
				if (line.startsWith(MOVESTRING)) {
					return line.substring(MOVESTRING.length()).trim();
				}
			}
		return "";
	}

	private void discardLines(int num) {
		try {
			for (int i = 0; i < num; i++)
				System.out.println("gnuchess>: " + input.readLine());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void readAll() throws IOException {
		String line; 
		while ((line = input.readLine()) != null) {
			System.out.println("gnuchess>: " + line);
		};
	}

	public void close() throws IOException {
			sendMove("quit");
			readAll();
			input.close();
			output.close();
			xboard.destroy();
	}

	// Test main, send a move and print answer from gnuchess
	public static void main(String[] args) {
		try {
			XBoardConnect con = new XBoardConnect();
			con.sendMove("e2e4");
			System.out.println(con.readMove());
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

