import java.util.ArrayList;
import java.util.Arrays;

public class CellGroup{

	private ArrayList<Cell> cells;
	private ArrayList<Cell> uncertainCells;
	private ArrayList<Integer> missingValues;

	public CellGroup(){
		cells = new ArrayList<Cell>();
		uncertainCells = new ArrayList<Cell>();
		missingValues = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9));
	}

	public void add(Cell c){
		cells.add(c);
		uncertainCells.add(c);
	}

	public ArrayList<Cell> getCells(){
		return cells;
	}

	public ArrayList<Cell> getUncertainCells(){
		return uncertainCells;
	}

	public ArrayList<Integer> getMissingValues(){
		return missingValues;
	}

	public void updateMissingValues(){
		ArrayList<Cell> temp = new ArrayList<Cell>();

		for(int i = 0; i < uncertainCells.size(); i++){
			Cell c = uncertainCells.get(i);

			if(c.getNumPossibleValues() == 1){
				int value = c.getValue();

				for(int j = 0; j < missingValues.size(); j++){
					if(missingValues.get(j) == value){
						missingValues.remove(j);
						break;
					}
				}
			}
			else{
				temp.add(c);
			}
		}

		uncertainCells = temp;
	}	
}