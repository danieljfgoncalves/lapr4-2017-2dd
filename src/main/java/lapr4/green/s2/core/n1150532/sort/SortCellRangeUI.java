package lapr4.green.s2.core.n1150532.sort;

import static csheets.core.Address.HIGHEST_CHAR;
import static csheets.core.Address.LOWEST_CHAR;
import csheets.core.Cell;
import csheets.ui.ctrl.UIController;
import eapli.util.Strings;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import lapr4.green.s2.core.n1150532.sort.algorithms.SortingAlgorithm;
import lapr4.green.s2.core.n1150532.sort.comparators.RangeRowDTOComparator;

/**
 * The pop-up menu to configure the cell range sorting and performing the
 * operation.
 *
 * @author Manuel Meireles (1150532)
 */
public class SortCellRangeUI extends JDialog {

    /**
     * The title of the pop-up menu.
     */
    private static final String TITLE = "Sort Cell Range...";

    /**
     * The user interface's controller.
     */
    private final SortCellRangeController controller;

    /**
     * The selected cells to be sorted.
     */
    private final Cell[][] selectedCells;

    /**
     * The field in which to write the column name.
     */
    private JTextField inSortingColumn;

    /**
     * The combo box in which to select the algorithm.
     */
    private JComboBox outAlgorithms;

    /**
     * The combo box in which to select the comparator.
     */
    private JComboBox outComparators;

    /**
     * The radio button to indicate the ascending sort ordering.
     */
    private JRadioButton btAscending;

    /**
     * The radio button to indicate the descending sort ordering.
     */
    private JRadioButton btDescending;

    /**
     * A label to show error messages to the user.
     */
    private JLabel outInformation;

    /**
     * The button to cancel the operation.
     */
    private JButton btCancel;

    /**
     * The button to perform the sorting.
     */
    private JButton btSort;
    /**
     * The real column index
     */
    private int realColumnIndex;
    /**
     * The checkbox for persisting the changes
     */
    private   JCheckBox check;

    /**
     * The full constructor of the user interface.
     *
     * @param theSelectedCells The cell range to sort.
     */
    public SortCellRangeUI(Cell[][] theSelectedCells, UIController controller) {
        super();
        selectedCells = theSelectedCells;
        this.controller = new SortCellRangeController(controller);
        createUserInterface();
        createInteractions();
        setInitialState();
        setVisible(true);
    }

    /**
     * It builds the user interface.
     */
    private void createUserInterface() {
        setLayout(new BorderLayout());
        add(createTopPanel(), BorderLayout.NORTH);
        add(createCentralPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
        setTitle(TITLE);
        pack();
        setLocationRelativeTo(null);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    /**
     * It builds the sorting column panel.
     *
     * @return It returns the panel fully built.
     */
    private JPanel createTopPanel() {
        final String labelText = "Select the sorting column: ";
        final int size = 5;
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inSortingColumn = new JTextField(size);
        panel.add(new JLabel(labelText));
        panel.add(inSortingColumn);
        return panel;
    }

    /**
     * It builds the central panel.
     *
     * @return It returns the panel fully built.
     */
    private JPanel createCentralPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createOrderPanel(), BorderLayout.NORTH);
        panel.add(createAlgorithmPanel(), BorderLayout.CENTER);
        panel.add(createComparisonPanel(), BorderLayout.SOUTH);
        return panel;
    }

    /**
     * It builds the order panel.
     *
     * @return It returns the panel fully built.
     */
    private JPanel createOrderPanel() {
        final String panelTitle = "Order";
        final String ascendingText = "Ascending";
        final String descendingText = "Descending";
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btAscending = new JRadioButton(ascendingText);
        btDescending = new JRadioButton(descendingText);
        ButtonGroup btGroup = new ButtonGroup();
        btGroup.add(btAscending);
        btGroup.add(btDescending);
        panel.add(btAscending);
        panel.add(btDescending);
        panel.setBorder(BorderFactory.createTitledBorder(panelTitle));
        return panel;
    }

    /**
     * It builds the algorithms panel.
     *
     * @return It returns the panel fully built.
     */
    private JPanel createAlgorithmPanel() {
        final String panelTitle = "Algorithm";
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        outAlgorithms = new JComboBox();
        panel.add(outAlgorithms);
        panel.setBorder(BorderFactory.createTitledBorder(panelTitle));
        check = new JCheckBox("persist sorting");
        panel.add(check);
        return panel;
    }

    /**
     * It builds the comparators panel.
     *
     * @return It returns the panel fully built.
     */
    private JPanel createComparisonPanel() {
        final String panelTitle = "Type Hierarchy";
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        outComparators = new JComboBox();
        panel.add(outComparators);
        panel.setBorder(BorderFactory.createTitledBorder(panelTitle));
        return panel;
    }

    /**
     * It builds the buttons panel.
     *
     * @return It returns the panel fully built.
     */
    private JPanel createBottomPanel() {
        final String cancelText = "Cancel";
        final String sortText = "Sort";
        JPanel panel = new JPanel(new BorderLayout());
        JPanel p1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel p2 = new JPanel(new GridLayout(1, 2));
        JPanel p2Left = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel p2Right = new JPanel(new FlowLayout(FlowLayout.CENTER));
        outInformation = new JLabel(" ");
        btCancel = new JButton(cancelText);
        btSort = new JButton(sortText);
        p2Left.add(btCancel);
        p2Right.add(btSort);
        p1.add(outInformation);
        p2.add(p2Left);
        p2.add(p2Right);
        panel.add(p1, BorderLayout.CENTER);
        panel.add(p2, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * It adds the active action listeners.
     */
    private void createInteractions() {
        btCancel.addActionListener(new CancelAction());
        btSort.addActionListener(new SortAction());
    }

    /**
     * It sets the information in to the user interface.
     */
    private void setInitialState() {
        btAscending.setSelected(true);
        Object[] comparators = controller.getComparators().toArray();
        Object[] algorithms = controller.getAlgorithms().toArray();
        if (comparators.length > 0) {
            outComparators.setModel(new DefaultComboBoxModel(comparators));
            outComparators.setSelectedIndex(0);
        }
        if (algorithms.length > 0) {
            outAlgorithms.setModel(new DefaultComboBoxModel(algorithms));
            outAlgorithms.setSelectedIndex(0);
        }
    }

    /**
     * It translates the sorting column input text in to the matching cell range
     * index.
     *
     * @return It returns the sorting column index.
     */
    private int getSortingColumnIndex() {

        int columnIndex = translateColumn(inSortingColumn.getText());
        this.realColumnIndex =  translateColumn(inSortingColumn.getText());
        return changeToCellRangeIndex(columnIndex);
    }

    /**
     * It translates the column name like shown in the UI to the matching index
     * used in its address.
     *
     * @param column The column name from which to get the address column index.
     * @return It returns the column index used in the address of the matching
     * cell.
     */
    private int translateColumn(String column) {
        if (Strings.isNullOrWhiteSpace(column)) {
            throw new IllegalArgumentException("The column name must be written.");
        }
        int columnIndex = 0;
        if (!column.matches("^[A-Z]+$")) {
            throw new IllegalArgumentException("The column must contain only uppercase letters.");
        }
        final char[] columnArray = column.toCharArray();
        int arrayIndex = 0;
        while (arrayIndex < columnArray.length) {
            columnIndex += (columnArray[arrayIndex] - LOWEST_CHAR) + (arrayIndex * (HIGHEST_CHAR - LOWEST_CHAR));
            arrayIndex++;
        }
        return columnIndex;
    }

    /**
     * It checks if the column index is within the selected cells columns and
     * provides the index within the selected cells.
     *
     * @param columnIndex The spreadsheet column index.
     * @return It returns the selected cells column index.
     */
    private int changeToCellRangeIndex(int columnIndex) {
        int minimumAllowedColumn = selectedCells[0][0].getAddress().getColumn();
        int maximumAllowedColumn = selectedCells[0][selectedCells[0].length - 1].getAddress().getColumn();
        if (columnIndex < minimumAllowedColumn || columnIndex > maximumAllowedColumn) {
            throw new IllegalArgumentException("The column must be within the the selected range.");
        }
        return columnIndex - minimumAllowedColumn;
    }

    /**
     * A private class for the cancel action.
     */
    private class CancelAction implements ActionListener {

        /**
         * It disposes the window.
         *
         * @param e Not used.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    }

    /**
     * A private class for the sort action.
     */
    private class SortAction implements ActionListener {

        /**
         * It sorts the cell range and disposes the window.
         *
         * @param e Not used.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if(check.isSelected()) {
                    controller.sort(selectedCells, getSortingColumnIndex(), SortCellRangeUI.this
                            .realColumnIndex, (SortingAlgorithm) outAlgorithms.getSelectedItem(), (RangeRowDTOComparator) outComparators.getSelectedItem(), btDescending.isSelected());
                }
                dispose();
            } catch (IllegalArgumentException ex) {
                outInformation.setText(ex.getMessage());
                pack();
            }
        }
    }

}
