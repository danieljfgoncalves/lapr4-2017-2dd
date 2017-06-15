/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lapr4.green.s3.lang.n1150838.TablesAndFilters.domain;

import csheets.core.Cell;
import java.io.Serializable;
import java.util.List;
import lapr4.green.s1.ipc.n1150800.importexportTXT.CellRange;

/**
 *
 * @author Nuno Pinto 1150838
 */
public class Table implements Serializable {

    /**
     * @return the cells
     */
    public List<Row> getCells() {
        return cells;
    }

    private List<Row> cells;

    private CellRange range;

    private Filter filters;

    public Table(CellRange range, List<Row> cells, Filter filters) {
        this.range = range;
        this.cells = cells;
        this.filters = filters;

    }

    /**
     * @return the range
     */
    public CellRange getRange() {
        return range;
    }

    /**
     * @param range the range to set
     */
    public void setRange(CellRange range) {
        this.range = range;
    }

    public boolean containsCells(List<Cell> cells) {

        for (Row row : this.getCells()) {
            for (Cell cell : cells) {
                if (row.containsCell(cell)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean containsCell(Cell cell) {
        for (Row row : getCells()) {
            if (row.containsCell(cell)) {
                return true;
            }
        }

        return false;
    }

    public boolean insertFilter(String filter) {

        if (filter.startsWith("=")) {
            filters.getFormulas().add(filter);
            return true;
        }
        return false;
    }

    /**
     * @return the filters
     */
    public Filter getFilters() {
        return filters;
    }

    public Row getRow(int i) {
        return cells.get(i);
    }

}
