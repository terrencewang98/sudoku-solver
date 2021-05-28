import java.util.ArrayList;
import java.util.Arrays;

public class Cell{

	private int row;
	private int col;
	private ArrayList<Integer> possibleValues;
	private int value;
	private CellGroup block;

	public Cell(int row, int col, ArrayList<Integer> possibleValues){
		this.row = row;
		this.col = col;
		this.possibleValues = possibleValues;
		this.value = possibleValues.size() == 1 ? possibleValues.get(0) : 0;
		this.block = null;
	}

	public int getRow(){
		return row;
	}

	public int getCol(){
		return col;
	}

	public ArrayList<Integer> getPossibleValues(){
		return possibleValues;
	}

	public int getNumPossibleValues(){
		return possibleValues.size();
	}

	public boolean hasPossibleValue(int value){
		return possibleValues.contains(value);
	}

	public void removePossibleValue(int x){

		for(int i = 0; i < possibleValues.size(); i++){
			if(possibleValues.get(i) == x){
				possibleValues.remove(i);
				break;
			}
		}

		if(possibleValues.size() == 0){
			value = 0;
		}
		else if(possibleValues.size() == 1){
			value = possibleValues.get(0);
		}
	}

	public int getValue(){
		return value;
	}

	public void setValue(int value){
		possibleValues = new ArrayList<Integer>(Arrays.asList(value));
		this.value = value;
	}

	public CellGroup getBlock(){
		return block;
	}

	public void setBlock(CellGroup block){
		this.block = block;
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof Cell){
			Cell other = (Cell)o;
			return row == other.getRow() && col == other.getCol();
		}
		return false;
	}

	/*
	FOR TESTING PURPOSES
	
	public void printPossibleValues(){
		System.out.println("row: " + row + " col: " + col + " possible values:");
		for(int value : possibleValues){
			System.out.print(value + " ");
		}
		System.out.println();
	}
	*/
}