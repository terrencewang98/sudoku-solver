import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicReference;

public class SudokuSolver{

	public Board seqSolve(Board board){
		Stack<Board> possibleBoards = new Stack<Board>();
		possibleBoards.push(board);

		while(possibleBoards.size() > 0){
			Board currentBoard = possibleBoards.pop();
			solveCertainCells(currentBoard);
			Cell cell = getNextCell(currentBoard);
			
			if(cell == null){
				//board is solved
				board = currentBoard;
				break; 
			}
			else if(cell.getNumPossibleValues() != 0){
				//is valid board
				int row = cell.getRow();
				int col = cell.getCol();
				ArrayList<Integer> possibleValues = cell.getPossibleValues();

				//deterministic
				/*for(int x : possibleValues){
					Board newBoard = currentBoard.clone();
					newBoard.getCell(row, col).setValue(x);
					possibleBoards.push(newBoard);
				}*/

				//randomized
				while(possibleValues.size() > 0){
					int rand = (int)(Math.random() * possibleValues.size());
					int value = possibleValues.remove(rand);
					Board newBoard = currentBoard.clone();
					newBoard.getCell(row, col).setValue(value);
					possibleBoards.push(newBoard);
				}
			}
		}

		return board;
	}

	public Board parallelSolve(Board board){
		int nThreads = Runtime.getRuntime().availableProcessors();
		ForkJoinPool pool = new ForkJoinPool();
		AtomicReference<Board> result = new AtomicReference<Board>();
		pool.invoke(new SolverTask(board, result));

		return result.get();
	}

	//find cells with only one possibility and fill them in
	private void solveCertainCells(Board board){

		boolean notDone = true;

		while(notDone){
			notDone = false;

			//eliminate invalid values
			for(int i = 0; i < board.getSize(); i++){

				for(int j = 0; j < board.getSize(); j++){
					Cell cell = board.getCell(i, j);

					if(!board.checkCellSolved(i, j) && cell.getNumPossibleValues() == 1){
						reduce(board, cell);
						notDone = true;
					}
				}
			}

			//check blocks, rows, and cols
			ArrayList<Cell> found = new ArrayList<Cell>();
			CellGroup[][] blocks = board.getBlocks();
			CellGroup[] rows = board.getRows();
			CellGroup[] cols = board.getCols();

			for(int i = 0; i < blocks.length; i++){

				for(int j = 0; j < blocks.length; j++){
					checkCellGroup(blocks[i][j], found);
				}
			}
			
			for(int i = 0; i < rows.length; i++){
				checkCellGroup(rows[i], found);
			}			

			for(int i = 0; i < cols.length; i++){
				checkCellGroup(cols[i], found);
			}

			if(found.size() > 0){
				notDone = true;

				for(Cell c : found){
					reduce(board, c);
				}
			}
		}
	}

	//check row, col, or block to find single possiblility cells
	private void checkCellGroup(CellGroup group, ArrayList<Cell> found){
		group.updateMissingValues();

		for(int x : group.getMissingValues()){
			Cell candidate = null;

			for(Cell c : group.getUncertainCells()){

				if(c.hasPossibleValue(x)){
					if(candidate == null){
						candidate = c;
					}
					else{
						candidate = null;
						break;
					}
				}
			}

			if(candidate != null){
				candidate.setValue(x);
				found.add(candidate);				
			}
		}
	}

	//when cell has only one possible value, remove its value from other cells in the same row, col, and block
	private void reduce(Board board, Cell cell){
		
		if(cell.getNumPossibleValues() == 1){
			int row = cell.getRow();
			int col = cell.getCol();
			int value = cell.getValue();
			board.setCellSolved(row, col);

			for(int i = 0; i < board.getSize(); i++){
				Cell x = board.getCell(row, i);
				Cell y = board.getCell(i, col);
				if(!x.equals(cell)){
					x.removePossibleValue(value);
				}
				if(!y.equals(cell)){
					y.removePossibleValue(value);
				}	
			}

			for(Cell c : cell.getBlock().getCells()){
				if(!c.equals(cell)){
					c.removePossibleValue(value);
				}
			}
		}
	}

	//returns next uncertain cell to explore by finding the cell with minimum possible values
	//ignore cells with one possible value, as they are certain
	//if next cell is null, board is solved
	//if next cell has 0 possible values, board is incorrect
	private Cell getNextCell(Board board){
		Cell minCell = null;
		int min = 10;

		for(int i = 0; i < board.getSize(); i++){

			for(int j = 0; j < board.getSize(); j++){
				Cell cell = board.getCell(i, j);
				int n = cell.getNumPossibleValues();

				if(n != 1 && n < min){
					minCell = cell;
					min = n;

					if(min == 0){
						break;
					}
				}
			}
		}

		return minCell;
	}
}
