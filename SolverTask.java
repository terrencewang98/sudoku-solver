import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicReference;

public class SolverTask extends RecursiveAction {
	Board board;
	AtomicReference<Board> result;

	public SolverTask(Board board, AtomicReference<Board> result){
		this.board = board;
		this.result = result;
	}

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
		int min = 9;

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

	@Override
	protected void compute(){
		solveCertainCells(board);
		Cell cell = getNextCell(board);

		if(cell == null){
			result.set(board);
		}
		else if(cell.getNumPossibleValues() != 0){
			int row = cell.getRow();
			int col = cell.getCol();
			ArrayList<Integer> possibleValues = cell.getPossibleValues();
			ArrayList<SolverTask> subtasks = new ArrayList<SolverTask>();

			//deterministic
			for(int x : possibleValues){
				Board newBoard = board.clone();
				newBoard.getCell(row, col).setValue(x);
				SolverTask subtask = new SolverTask(newBoard, result);
				subtasks.add(subtask);
			}

			invokeAll(subtasks);
		}
	}
}