/*
 * Copyright (c) 2005 Einar Pehrson <einar@pehrson.nu>.
 *
 * This file is part of
 * CleanSheets - a spreadsheet application for the Java platform.
 *
 * CleanSheets is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * CleanSheets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CleanSheets; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package csheets.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import csheets.core.formula.compiler.FormulaCompilationException;
import csheets.ext.Extension;
import csheets.ext.ExtensionManager;
import csheets.ext.SpreadsheetExtension;
import csheets.ui.ctrl.UIController;
import lapr4.green.s1.ipc.n1150800.importexportTXT.CellRange;
import lapr4.green.s3.lang.n1150838.TablesAndFilters.domain.Table;

/**
 * The implementation of the <code>Spreadsheet</code> interface.
 * @author Einar Pehrson
 */
public class SpreadsheetImpl implements Spreadsheet {

	/** The unique version identifier used for serialization */
	private static final long serialVersionUID = 7010464744129096272L;

	/** The base of the titles of new spreadsheets */
	public static final String BASE_TITLE = "Sheet ";

	/** The workbook to which the spreadsheet belongs */
	private Workbook workbook;

	/** The cells that have been instantiated */
	private Map<Address, Cell> cells = new HashMap<Address, Cell>();

	/** The title of the spreadsheet */
	private String title;

	/** The number of columns in the spreadsheet */
	private int columns = 0;

	/** The number of rows in the spreadsheet */
	private int rows = 0;

	/** The cell listeners that have been registered on the cell */
	private transient List<CellListener> cellListeners
		= new ArrayList<CellListener>();

	/** The cell listener that forwards events from all cells */
	private transient CellListener eventForwarder = new EventForwarder();

	/** The spreadsheet extensions that have been instantiated */
	private transient Map<String, SpreadsheetExtension> extensions = 
		new HashMap<String, SpreadsheetExtension>();
        
        /** The tables that where been created */
        private List<Table> tables = new ArrayList();

	private transient UIController uiController;

        /**
	 * Creates a new spreadsheet.
	 * @param workbook the workbook to which the spreadsheet belongs
	 * @param title the title of the spreadsheet
	 */
	SpreadsheetImpl(Workbook workbook, String title) {
		this.workbook = workbook;
		this.title = title;
	}
        
        /**
	 * Creates a new spreadsheet, in which cells are initialized with data from
	 * the given content matrix.
	 * @param workbook the workbook to which the spreadsheet belongs
	 * @param title the title of the spreadsheet
	 * @param content the contents of the cells in the spreadsheet
	 */
	SpreadsheetImpl(Workbook workbook, String title, String[][] content) {
		this(workbook, title);
		rows = content.length;
		for (int row = 0; row < content.length; row++) {
			int columns = content[row].length;
			if (this.columns < columns)
				this.columns = columns;
			for (int column = 0; column < columns; column++) {
				try {
					Address address = new Address(column, row);
					Cell cell = new CellImpl(this, address, content[row][column]);
					cell.addCellListener(eventForwarder);
					cells.put(address, cell);
				} catch (FormulaCompilationException e) {}
			}
		}
	}
        
	/**
	 * Creates a new spreadsheet.
	 * @param workbook the workbook to which the spreadsheet belongs
	 * @param title the title of the spreadsheet
	 */
	SpreadsheetImpl(Workbook workbook, String title, UIController uiController) {
		this.workbook = workbook;
		this.title = title;
		this.uiController = uiController;
	}
        
	/**
	 * Creates a new spreadsheet, in which cells are initialized with data from
	 * the given content matrix.
	 * @param workbook the workbook to which the spreadsheet belongs
	 * @param title the title of the spreadsheet
	 * @param content the contents of the cells in the spreadsheet
	 */
	SpreadsheetImpl(Workbook workbook, String title, String[][] content, UIController uiController) {
		this(workbook, title, uiController);
		rows = content.length;
		for (int row = 0; row < content.length; row++) {
			int columns = content[row].length;
			if (this.columns < columns)
				this.columns = columns;
			for (int column = 0; column < columns; column++) {
				try {
					Address address = new Address(column, row);
					Cell cell = new CellImpl(this, address, content[row][column], uiController);
					cell.addCellListener(eventForwarder);
					cells.put(address, cell);
				} catch (FormulaCompilationException e) {}
			}
		}
	}

/*
 * LOCATION
 */

	public Workbook getWorkbook() {
		return workbook;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		// fireTitleChanged();
	}

/*
 * DIMENSIONS
 */

	public int getColumnCount() {
		return columns;
	}

	public int getRowCount() {
		return rows;
	}

/*
 * CELLS
 */

	public Cell getCell(Address address) {
		// Updates spreadsheet dimensions
		if (address.getRow() > rows)
			rows = address.getRow();
		if (address.getColumn() > columns)
			columns = address.getColumn();

		// Looks for a previously used cell with this address
		Cell cell = cells.get(address);

		// If the cell has never been requested, create a new one
		if (cell == null) {
			cell = new CellImpl(this, address, uiController);
			cell.addCellListener(eventForwarder);
			cells.put(address, cell);
		}
		return cell;
	}

	public Cell getCell(int column, int row) {
		return getCell(new Address(column, row));
	}

	public SortedSet<Cell> getCells(Address address1, Address address2) {
		// Sorts addresses
		if (address1.compareTo(address2) > 0) {
			Address tempAddress = address1;
			address1 = address2;
			address2 = tempAddress;
		}

		// Builds the set
		SortedSet<Cell> cells = new TreeSet<Cell>();
		for (int column = address1.getColumn(); column <= address2.getColumn(); column++)
			for (int row = address1.getRow(); row <= address2.getRow(); row++)
				cells.add(getCell(new Address(column, row)));

		return cells;
	}

	public Cell[] getColumn(int index) {
		Cell[] column = new Cell[rows];
		for (int row = 0; row < rows; row++)
			column[row] = getCell(new Address(index, row));
		return column;
	}

	public Cell[] getRow(int index) {
		Cell[] row = new Cell[columns];
		for (int column = 0; column < columns; column++)
			row[column] = getCell(new Address(column, index));
		return row;
	}

	public Iterator<Cell> iterator() {
		return cells.values().iterator();
	}
        
        
/** 
 * TABLES
 */
        /**
         * Returns true if the table has been added 
         * @param e the table to add
         * @return true if added otherwise false
         */
        public boolean addTable(Table e){
            return tables.add(e);
        }
        /**
         * Checks if the given cells arent linked to a table
         * @param selecteCells the cells to be verified
         * @return true if the cells arent linked to a table , false otherwise 
         */
        public boolean isAvailableToDefine(List<Cell> selecteCells){
            for (Table table : tables) {
                if(table.containsCells(selecteCells)){
                    return false;
                }
            }
            return true;
        }
        /**
         * Checks if the given cell is already defined
         * @param cell to be verified
         * @return the range  or null if the cell isnt defined
         */
        public CellRange isDefined(Cell cell){
            for (Table table : tables) {
                if(table.containsCell(cell)){
                    return table.getRange();
                }
            }
            return null;
        }
        /**
         * 
         * @return the active tables
         */
        public List<Table> getTables(){
            return tables;
        }
        /**
         * Removes the given table from te table list
         * @param d
         * @return true if the table was removed
         */
        public boolean removeTable(Table d){
            return tables.remove(d);
        }
        
        /**
          *
        * @param cell
        * @return
        */
        public Table getTable(Cell cell){
            for (Table table : tables) {
                if(table.containsCell(cell)){
                    return table;
                }
            }
            return null; // should not happen
        }
/*
 * EVENT HANDLING
 */

	public void addCellListener(CellListener listener) {
		cellListeners.add(listener);
	}

	public void removeCellListener(CellListener listener) {
		cellListeners.remove(listener);
	}

	public CellListener[] getCellListeners() {
		return cellListeners.toArray(new CellListener[cellListeners.size()]);
	}

	/**
	 * A cell listener that forwards events from all cells to registered listeners.
	 */
	private class EventForwarder implements CellListener {

		/**
		 * Creates a new event forwarder.
		 */
		public EventForwarder() {}

		public void valueChanged(Cell cell) {
			for (CellListener listener : cellListeners)
				listener.valueChanged(cell);
		}

		public void contentChanged(Cell cell) {
			for (CellListener listener : cellListeners)
				listener.contentChanged(cell);
		}

		public void dependentsChanged(Cell cell) {
			for (CellListener listener : cellListeners)
				listener.dependentsChanged(cell);
		}

		public void cellCleared(Cell cell) {
			for (CellListener listener : cellListeners)
				listener.cellCleared(cell);
		}

		public void cellCopied(Cell cell, Cell source) {
			for (CellListener listener : cellListeners)
				listener.cellCopied(cell, source);
		}
	}

/*
 * EXTENSIONS
 */

	public Spreadsheet getExtension(String name) {
		// Looks for an existing spreadsheet extension
		SpreadsheetExtension extension = extensions.get(name);
		if (extension == null) {
			// Creates a new spreadsheet extension
			Extension x = ExtensionManager.getInstance().getExtension(name);
			if (x != null) {
				extension = x.extend(this);
				if (extension != null)
					extensions.put(name, extension);
			}
		}
		return extension;
	}

/*
 * GENERAL
 */

	/**
	 * Customizes deserialization by catching exceptions when extensions
	 * are not found.
	 * @param stream the object input stream from which the object is to be read
	 * @throws IOException If any of the usual Input/Output related exceptions occur
	 * @throws ClassNotFoundException If the class of a serialized object cannot be found.
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
	    stream.defaultReadObject();

		// Sets up event forwarder
		eventForwarder = new EventForwarder();
		for (Cell cell : cells.values())
			cell.addCellListener(eventForwarder);
		cellListeners = new ArrayList<CellListener>();

		// Reads extensions
		extensions = new HashMap<String, SpreadsheetExtension>();
		int extCount = stream.readInt();
		for (int i = 0; i < extCount; i++) {
			try {
				SpreadsheetExtension extension = (SpreadsheetExtension)stream.readObject();
				extensions.put(extension.getName(), extension);
			} catch (ClassNotFoundException e) {
				System.err.println(e);
			}
		}
	}

	/**
	 * Customizes serialization, by writing extensions separately.
	 * @param stream the object output stream to which the object is to be written
	 * @throws IOException If any of the usual Input/Output related exceptions occur
	 */
	private void writeObject(ObjectOutputStream stream) throws IOException {
		stream.defaultWriteObject();

		// Writes extensions
		stream.writeInt(extensions.size());
		for (SpreadsheetExtension extension : extensions.values())
			stream.writeObject(extension);
	}
}