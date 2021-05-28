import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class BoardPanel extends JPanel{
	
	private final int WINDOW_WIDTH = 760;
	private int boardSize;
	private int[][] board;
	private JTextField[][] inputBoard;
	private boolean userInputComplete;
	private boolean solved;
	private long computeTime;

	public BoardPanel(){
		init();
	}

	private void init(){
		boardSize = 9;
		board = new int[boardSize][boardSize];
		inputBoard = new JTextField[boardSize][boardSize];
		userInputComplete = false;
		solved = false;
		computeTime = 0;
	}

	public void reset(){

		for(int row = 0; row < boardSize; row++){
			for(int col = 0; col < boardSize; col++){
				this.remove(inputBoard[row][col]);
			}
		}

		init();
		repaint();
	}

	public void solve(boolean threaded){
		userInputComplete = true;
		setBoard();
		SudokuSolver solver = new SudokuSolver();
		long start = System.nanoTime();
		Board boardObj = new Board(board);

		if(threaded){
			boardObj = solver.parallelSolve(new Board(board));
		}
		else{
			boardObj = solver.seqSolve(new Board(board));
		}

		long stop = System.nanoTime();
		computeTime = (stop - start) / 1_000_000;

		if(boardObj.solved()){
			solved = true;

			for(int i = 0; i < boardSize; i++){

				for(int j = 0; j < boardSize; j++){
					board[i][j] = boardObj.getCell(i, j).getValue();
				}
			}
		}
		
		repaint();
	}

	private void setBoard(){

		for(int row = 0; row < boardSize; row++){

			for(int col = 0; col < boardSize; col++){

				String s = inputBoard[row][col].getText();

				try{
					int input = Integer.parseInt(s);
					if(input >= 1 && input <= boardSize){
						board[row][col] = input;
					}
					else{
						board[row][col] = 0;
					}
				}
				catch(NumberFormatException nfe){
					board[row][col] = 0;
				}
			}
		}
	}

	private void drawBoard(Graphics g){
		int margin = 20;
		int boardWidth = WINDOW_WIDTH - margin * 2;
		int cellWidth = boardWidth / boardSize;

		int i = margin;
		int count = 0;

		//draw borders of board
		while(i <= margin + boardWidth){

			if(count % ((int)Math.sqrt(boardSize)) == 0){
				int lineWidth = 4;
				g.fillRect(i, margin, lineWidth, boardWidth);
				g.fillRect(margin, i - lineWidth / 2, boardWidth + 4, lineWidth);
			}
			else{
				g.drawLine(i, margin, i, margin + boardWidth);
				g.drawLine(margin, i, margin + boardWidth, i);
			}
			
			i += cellWidth;
			count++;
		}

		//fill board
		for(int row = 0; row < boardSize; row++){

			for(int col = 0; col < boardSize; col++){

				if(!userInputComplete){
					int x = margin + col * cellWidth + cellWidth / 4;
					int y = margin + row * cellWidth + cellWidth / 4;

					inputBoard[row][col] = new JTextField();
					inputBoard[row][col].setBounds(x, y, cellWidth / 2, cellWidth / 2);
					this.add(inputBoard[row][col]);
				}
				else{
					this.remove(inputBoard[row][col]);

					if(board[row][col] != 0){
						int offset = 6;
						int x = margin + col * cellWidth + cellWidth / 2 - offset;
						int y = margin + row * cellWidth + cellWidth / 2 + offset;
						g.setFont(new Font("TimesRoman", Font.PLAIN, 24)); 
						g.drawString(Integer.toString(board[row][col]), x, y);
					}
				}
			}
		}

		//display solveTime
		if(computeTime > 0){
			String msg = "";
			if(solved){
				msg = "Solved! (" + computeTime + " ms)";
			}
			else{
				msg = "Failed to solve! (" + computeTime + " ms)";
			}
			g.setFont(new Font("TimesRoman", Font.PLAIN, 12)); 
			g.drawString(msg, margin, margin / 4 * 3);
		}
	}
	
	@Override
	public Dimension getPreferredSize() {
        return new Dimension(WINDOW_WIDTH, WINDOW_WIDTH);
    }

	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);      
    }

	/*
	FOR TESTING PURPOSES
	private final int[][] TEST_1 = new int[][]{
		{0,0,4,0,0,1,0,7,6},
		{0,0,0,0,0,9,1,0,0},
		{0,0,0,8,0,0,0,0,5},
		{0,3,0,0,0,0,0,0,1},
		{7,4,0,2,0,0,0,0,0},
		{1,9,0,0,0,3,4,0,0},
		{0,0,0,0,0,0,3,0,0},
		{0,0,0,3,0,4,0,2,0},
		{0,0,8,0,6,0,0,0,0}
	};
	private int[][] TEST_2 = new int[][]{
		{8,0,0,0,0,0,0,0,0},
		{0,0,3,6,0,0,0,0,0},
		{0,7,0,0,9,0,2,0,0},
		{0,5,0,0,0,7,0,0,0},
		{0,0,0,0,4,5,7,0,0},
		{0,0,0,1,0,0,0,3,0},
		{0,0,1,0,0,0,0,6,8},
		{0,0,8,5,0,0,0,1,0},
		{0,9,0,0,0,0,4,0,0}
	};
	
	*/
}