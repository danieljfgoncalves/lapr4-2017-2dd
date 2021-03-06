package lapr4.blue.s1.lang.n1151031.formulastools.ui;

import csheets.ext.Extension;
import csheets.ui.ctrl.UIController;
import csheets.ui.ext.UIExtension;

import javax.swing.*;

/**
 * This class implements the UI interface extension for the conditional
 * formatting extension. A UI interface extension must extend the UIExtension
 * abstract class.
 *
 * @author Tiago Correia - 1151031@isep.ipp.pt
 * Edited by João Cardoso - 1150943
 *  - Corrected Javadocs
 *
 * @see UIExtension
 */
public class ConditionalStyleUIExtension extends UIExtension {

    /**
     * A side bar that provides editing of conditional formatting
     */
    private JComponent sideBar;

    public ConditionalStyleUIExtension(Extension extension, UIController uiController) {
        super(extension, uiController);
    }

    /**
     * Returns an icon to display with the extension's name.
     *
     * @return an icon with style
     */
    public Icon getIcon() {
        return null;
    }

    /**
     * Returns a side bar that provides editing of conditional formatting.
     *
     * @return a side bar
     */
    @Override
    public JComponent getSideBar() {
        if (sideBar == null) {
            sideBar = new ConditionalStylePanel(uiController);
        }
        return sideBar;
    }

}
