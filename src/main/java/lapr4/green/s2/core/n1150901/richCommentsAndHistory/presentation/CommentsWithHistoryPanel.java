package lapr4.green.s2.core.n1150901.richCommentsAndHistory.presentation;

import csheets.core.Cell;
import lapr4.green.s2.core.n1150901.richCommentsAndHistory.application.CommentsWithHistoryController;
import csheets.ext.style.StyleExtension;
import csheets.ext.style.ui.FontChooser;
import csheets.ui.ctrl.SelectionEvent;
import csheets.ui.ctrl.SelectionListener;
import csheets.ui.ctrl.UIController;
import lapr4.blue.s1.lang.n1151031.formulastools.UserStyle;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import lapr4.green.s2.core.n1150901.richCommentsAndHistory.domain.CommentsWithHistoryListener;
import lapr4.red.s1.core.n1150690.comments.CommentableCellWithMultipleUsers;
import lapr4.red.s1.core.n1150690.comments.domain.User;
import lapr4.red.s1.core.n1150690.comments.presentation.CommentsWithUserUI;
import lapr4.white.s1.core.n1234567.comments.CommentableCell;
import lapr4.white.s1.core.n1234567.comments.CommentsExtension;

/**
 * @author Miguel Silva - 1150901
 */
public class CommentsWithHistoryPanel extends CommentsWithUserUI implements CommentsWithHistoryListener {

    /**
     * The user's selected styling options.
     */
    private UserStyle userStyle;

    /**
     * The renderer to change format and style of JList content.
     */
    private final ComplexCellRenderer renderer;

    /**
     * The panel for the format, style and reverse buttons.
     */
    private final JPanel formatAndStyle = new JPanel();

    /**
     * The panel for the history of a comment.
     */
    private final JPanel history = new JPanel();

    /**
     * The panel for the search function.
     */
    private final JPanel search = new JPanel();

    /**
     * The list that will contain the history.
     */
    private JList panelHistory;

    /**
     * The list model to save the history.
     */
    private DefaultListModel model;

    /**
     * Creates a new condition style panel.
     *
     * @param uiController the user interface controller
     */
    public CommentsWithHistoryPanel(UIController uiController) {

        super(uiController);

        uiController.addSelectionListener(this);

        userStyle = new UserStyle();
        renderer = new ComplexCellRenderer();

        initComponents();
    }

    /**
     * Initiates the components.
     */
    private void initComponents() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        formatAndStyle.setLayout(new FlowLayout());
        formatAndStyle.add(stylePanel());
        formatAndStyle.add(resetPanel());

        TitledBorder border = BorderFactory.createTitledBorder("History");
        border.setTitleJustification(TitledBorder.CENTER);
        history.setBorder(border);
        history.add(historyPanel());

        search.add(searchPanel());

        add(formatAndStyle);
        add(history);
        add(search);
    }

    /**
     * Creates and returns the panel containing the history list.
     *
     * @return The panel containing the history list.
     */
    private JScrollPane historyPanel() {
        model = new DefaultListModel();
        panelHistory = new JList();
        panelHistory.setModel(model);
        panelHistory.setBackground(this.getBackground());
        panelHistory.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panelHistory.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder()));

        return new JScrollPane(panelHistory);
    }

    private JPanel stylePanel() {
        JPanel subPanel = new JPanel(new FlowLayout());

        JPanel selectFormatAndStylePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel selectFormatAndStyleLabel = new JLabel("Style and Format:");
        selectFormatAndStylePanel.add(selectFormatAndStyleLabel);

        JPanel formatAndStylePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        formatAndStylePanel.add(createFontButton());
        formatAndStylePanel.add(createForegroundButton());

        subPanel.add(selectFormatAndStylePanel);
        subPanel.add(formatAndStylePanel);

        subPanel.setMaximumSize(subPanel.getPreferredSize());

        return subPanel;
    }

    /**
     * Creates the Font button for the true condition style
     *
     * @return the Font button
     */
    private JButton createFontButton() {
        JButton button = new JButton(new ImageIcon(StyleExtension.class.getResource("res/img/font.gif")));
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(25, 25));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Font font = FontChooser.showDialog(
                        null,
                        "Choose Font",
                        userStyle.getTrueStyleFont());
                renderer.getListCellRendererComponent(panelComments, font, panelComments.getSelectedIndex(), true, true);
                panelComments.repaint();
            }
        });
        return button;
    }

    /**
     * Creates the Foreground button for the true condition style
     *
     * @return the Foreground button
     */
    private JButton createForegroundButton() {
        JButton button = new JButton(new ImageIcon(StyleExtension.class.getResource("res/img/color_fg.gif")));
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(25, 25));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color color = JColorChooser.showDialog(
                        null,
                        "Choose Foreground Color",
                        userStyle.getTrueStyleForegroundColor());
                renderer.getListCellRendererComponent(panelComments, color, panelComments.getSelectedIndex(), true, true);
                panelComments.repaint();
            }
        });
        return button;
    }

    /**
     * Creates the Reset button for the formatation and style.
     *
     * @return The panel with the reset button.
     */
    private JPanel resetPanel() {
        JPanel subPanel = new JPanel(new FlowLayout());
        JButton button = new JButton(new ImageIcon(StyleExtension.class.getResource("res/img/reset.gif")));
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(25, 25));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userStyle = new UserStyle();
//                styleChanged();
            }
        });

        subPanel.add(button);
        subPanel.setMaximumSize(subPanel.getPreferredSize());
        return subPanel;
    }

    private JPanel searchPanel() {
        JPanel subPanel = new JPanel(new FlowLayout());

        JTextField searchText = new JTextField();
        searchText.setPreferredSize(new Dimension(150, 30));
        JButton searchButton = new JButton("Search");
        searchButton.setPreferredSize(new Dimension(50, 30));
        searchButton.addActionListener(new Search());
        searchButton.setMargin(new Insets(0, 0, 0, 0));

        subPanel.add(searchText);
        subPanel.add(searchButton);

        return subPanel;
    }

    /**
     * Updates the comment's history when the comments of the active cell are
     * changed.
     *
     * @param cell The cell whose comment's history changed.
     */
    @Override
    public void historyChanged(CommentableCell cell) {
        updateList();
    }

    /**
     * Updates the comment's history.
     */
    private void updateList() {
        Map<User, List<String>> comments = controller.comments();
        for (Map.Entry<User, List<String>> entry : comments.entrySet()) {
            for (String com : entry.getValue()) {
                JLabel label = new JLabel(com);
                model.addElement(label);
            }
        }
    }

    @Override
    public void selectionChanged(SelectionEvent event) {

        super.selectionChanged(event);
        cell.addCommentsWithHistoryListener(this);

        if (event.getPreviousCell() != null) {
            ((CommentableCellWithMultipleUsers) event.getPreviousCell().getExtension(CommentsExtension.NAME))
                    .removeCommentsWithHistoryListener(this);
        }

        updateList();
    }

    /**
     * An inner class that implements the action listener for the select button.
     */
    private class Search implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            //TODO
        }
    }
}