OVERVIEW
Sudoku solver algorithms typically use recursion to explore various possibilities and arrive at a solution
Depending on the difficulty of the Sudoku puzzle, this search can become very computationally intensive as the number of possibilities increase, especially for bigger boards
My solution employs a constraint satisfaction approach to solve as much of the board as possible
Next, a DFS search is used to explore each of the multiple possiblities until a solution is found
For the sequential version, a Stack is used to store the different possible boards
For the parallel version, a ForkJoinPool is used to recursively explore the different possible boards

TO RUN
javac *.java
java Viewer

TO TEST
Fill in the textboxes with numbers from a sample sudoku puzzle
Interesting puzzles can be found here: https://sudoku.com/expert/
The hardest ever sudoku puzzle can be found here (takes over a minute): https://abcnews.go.com/blogs/headlines/2012/06/can-you-solve-the-hardest-ever-sudoku
Clicking the "reset" button will reset the puzzle to an empty board (unfortunately, the user has to manually input the puzzle for each solve)
Clicking the "sequential solve" or "parallel solve" button will solve the puzzle (if valid sudoku board)
Clicking "sequential solve" will run the sequential version of the solver
Clicking "parallel solve" will run the multithreaded version of the solver
Compute time is shown in the top left corner

CLASSES AND METHODS
Viewer: contains the main method, creates the GUI components
BoardPanel: extends JPanel, creates the Sudoku board graphics
	solve(boolean threaded): called when the user clicks the "sequential solve" button or the "parallel solve" button; solves the board and records compute time
	reset(): called when the user clicks the "reset" button, resets the BoardPanel to take new user input
Board(int[][] arr): represents the Sudoku board
	solved(): checks the board and returns true if it is a correctly solved Sudoku board
	clone(): returns a clone of this board as a new Board object
	verifyCellGroup(CellGroup cellGroup): takes a CellGroup (a row, column, or block) and returns true if it is valid
Cell(int row, int col, ArrayList<Integer> possibleValues): represents an individual square in the board; initialized with its position and its possible values (usually 1-9)
	getPossibleValues(): returns an ArrayList of possible values for this cell
	removePossibleValue(int value): removes this value from this cell's possible values
	setValue(int value): sets this cell to value and removes all other values from this cell's possible values
	getBlock(): returns the block (subgrid represented as a CellGroup) that this cell belongs to
CellGroup: represents a group of cells: either a row, column, or block (subgrid)
	getCells(): returns an ArrayList of Cell objects contained in this CellGroup
	getMissingValues(): returns an ArrayList of Integers representing values not yet in this CellGroup
SudokuSolver: used to solve a Board object
	seqSolve(Board board): runs the sequential version of the solve algorithm and returns a solved Board object
	parallelSolve(Board board): runs the multithreaded version of the solve algorithm and returns a solved Board object
	solveCertainCells(Board board): continually parses "board" for single possibility Cells until no further such Cells can be found
	checkCellGroup(CellGroup group, ArrayList<Cell> found): checks if a missing value in the given CellGroup only has one candidate Cell; if found, sets that Cell to 
		that value and writes the Cell to "found"
	reduce(Board board, Cell cell): called when "cell" has only one possible value; removes that value from that cell's row, column, and block
	getNextCell(Board board): searches uncertain cells (greater than one possible value) and returns the cell with the fewest possible values
		returns null if board is solved, returns 0 if board is incorrect
SolverTask(Board board, AtomicReference<Board> result): extends RecursiveAction, used for the multithreaded version of the solve algorithm

SEQUENTIAL APPROACH:
Push the board to the top of the stack
While the stack is not empty and no solution has been found, perform the following: 
1. Pop a board off of the stack
2. Find all singular possibility cells using solveCertainCells()
3. When no further progress can be made, find the cell with the fewest possible values using getNextCell()
4. Create separate versions of the board with each of the possible values for that cell, and push each board to the stack

PARALLEL APPROACH:
Create a ForkJoinPool and execute a SolverTask (extends RecursiveAction)
SolverTask performs the following: 
1. Find all singular possibility cells using solveCertainCells()
2. When no further progress can be made, find the cell with the fewest possible values using getNextCell()
3. Create separate versions of the board with each of the possible values for that cell
4. Create new SolverTask subtasks for each version of the board

PERFORMANCE ANALYSIS:
Each of these runtimes were averaged over ten trials

On an expert level puzzle, the sequential solve had an average run time of 1202.1 ms
On the same puzzle, the parallel solve had an average run time of 1417.3 ms

On the most difficult Sudoku puzzle (listed above), the sequential solve had an average run time of 184.838 s
On the same puzzle, the parallel solve had an average run time of 134.29 s

The parallel performance is slightly slower than the sequential performance on (relatively) easier puzzles
As the possibilities that need to be explored increases, the parallel solve starts performing slightly better than the sequential solve
From these results, it is clear that the parallel solution is comparable to the sequential solution, but does not result in any dramatic increase for a majority of Sudoku puzzles
One possible explanation for this finding is that the constraint satisfaction approach is too sophisticated to allow for any significant improvement from multithreading
Compared to a brute force recursive backtracking algorithm, the constraint satisfaction approach will create far fewer possible boards to be explored
Therefore, any performance improvement gained from assessing boards in parallel using a constraint satisfaction approach may not be substantial enough to overcome the inherent costs of multithreading for most Sudoku puzzles
For extremely difficult puzzles and perhaps bigger puzzles (such as 16x16 Sudoku boards), I would expect to see a more significant performance improvement from parallelism