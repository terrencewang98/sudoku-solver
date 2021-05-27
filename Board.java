import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;

public class Board{
	
	private Cell[][] board;
	private CellGroup[] rows;
	private CellGroup[] cols;
	private CellGroup[][] blocks;
	private boolean[][] isSolved;
	private final ArrayList<Integer> POSSIBLE_VALUES = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9));
	
	public Board(int[][] arr){

		if(arr.length != arr[0].length){
			throw new IllegalArgumentException();
		}

		//initialize board
		int n = arr.length;
		int blockLength = (int)Math.sqrt(n);
		board = new Cell[n][n];
		rows = new CellGroup[n];
		cols = new CellGroup[n];
		blocks = new CellGroup[blockLength][blockLength];
		isSolved = new boolean[n][n];

		for(int i = 0; i < board.length; i++){

			for(int j = 0; j < board.length; j++){

				if(arr[i][j] == 0){
					board[i][j] = new Cell(i, j, new ArrayList<Integer>(POSSIBLE_VALUES));
				}
				else{
					board[i][j] = new Cell(i, j, new ArrayList<Integer>(Arrays.asList(arr[i][j])));
				}
			}
		}

		//initialize row groups
		for(int i = 0; i < board.length; i++){
			rows[i] = new CellGroup();

			for(int j = 0; j < board.length; j++){
				rows[i].add(board[i][j]);
			}
		}

		//initialize col groups
		for(int j = 0; j < board.length; j++){
			cols[j] = new CellGroup();

			for(int i = 0; i < board.length; i++){
				cols[j].add(board[i][j]);
			}
		}

		//intialize block
		for(int row = 0; row < board.length; row += blockLength){

			for(int col = 0; col < board.length; col += blockLength){
				CellGroup block = new CellGroup();
				blocks[row / blockLength][col / blockLength] = block;

				for(int i = 0; i < blockLength; i++){

					for(int j = 0; j < blockLength; j++){
						block.add(board[row + i][col + j]);
						board[row + i][col + j].setBlock(block);
					}
				}
			}
		}
	}

	public Cell[][] getBoard(){
		return board;
	}

	public CellGroup[] getRows(){
		return rows;
	}

	public CellGroup[] getCols(){
		return cols;
	}

	public CellGroup[][] getBlocks(){
		return blocks;
	}

	public boolean[][] getIsSolved(){
		return isSolved;
	}

	public void setIsSolved(boolean[][] arr){
		isSolved = arr;
	}

	public boolean checkCellSolved(int row, int col){
		return isSolved[row][col];
	}

	public void setCellSolved(int row, int col){
		isSolved[row][col] = true;
	}

	public Cell getCell(int row, int col){
		return board[row][col];
	}

	public int getSize(){
		return board.length;
	}

	public Board clone(){
		int n = board.length;
		int[][] arr = new int[n][n];

		for(int i = 0; i < n; i++){
			for(int j = 0; j < n; j++){

				arr[i][j] = getCell(i, j).getValue();
			}
		}

		boolean[][] isSolvedCopy = new boolean[n][n];
		for(int i = 0; i < n; i++){
			for(int j = 0; j < n; j++){
				isSolvedCopy[i][j] = isSolved[i][j];
			}
		}

		Board clone = new Board(arr);
		clone.setIsSolved(isSolvedCopy);

		return clone;
	}

	public boolean solved(){
		int n = getSize();

		for(int i = 0; i < n; i++){

			if(!verifyCellGroup(rows[i]) || !verifyCellGroup(cols[i])){
				return false;
			}
		}

		for(int i = 0; i < blocks.length; i++){

			for(int j = 0; j < blocks.length; j++){

				if(!verifyCellGroup(blocks[i][j])){
					return false;
				}
			}
		}

		return true;
	}

	public boolean verifyCellGroup(CellGroup cellGroup){
		ArrayList<Cell> cells = cellGroup.getCells();

		for(int i = 1; i < getSize() + 1; i++){
			boolean correct = false;

			for(int j = 0; j < cells.size(); j++){
				Cell c = cells.get(j);

				if(c.getValue() == i){
					correct = !correct;
				}
			}

			if(!correct){
				return false;
			}
		}

		return true;
	}

	/*
	FOR TESTING PURPOSES
	public void print(){
		for(int i = 0; i < getSize(); i++){
			for(int j = 0; j < getSize(); j++){
				System.out.print(getCell(i, j).getValue() + " ");
			}
			System.out.println();
		}
	}

	public void printIsSolved(){
		for(int i = 0; i < isSolved.length; i++){
			for(int j = 0; j < isSolved.length; j++){
				System.out.print(isSolved[i][j] + " ");
			}
			System.out.println();
		}
	}*/
}