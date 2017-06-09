/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lapr4.green.s2.core.n1150838.GlobalSearch.application;

import csheets.core.IllegalValueTypeException;
import csheets.core.formula.Formula;
import csheets.core.formula.compiler.FormulaCompilationException;
import csheets.core.formula.compiler.FormulaCompiler;
import csheets.ui.ctrl.UIController;
import eapli.framework.application.Controller;
import lapr4.green.s2.core.n1150838.GlobalSearch.domain.Filter;
import lapr4.green.s2.core.n1150838.GlobalSearch.util.RegexUtilExtended;
import lapr4.red.s1.core.n1150613.workbookSearch.application.WorkbookSearchController;

/**
 *
 * @author nunopinto
 */
public class GlobalSearchController extends WorkbookSearchController implements Controller {
    
    public GlobalSearchController(UIController ctrl) {
        super(ctrl);
    }
    
    public boolean validateFormula(String formula) throws FormulaCompilationException, IllegalValueTypeException{
        FormulaCompiler instance = FormulaCompiler.getInstance();
        Formula f = instance.compile(null, formula);
        f.evaluate();
        return true;
    }
    
    public void  start(Filter filter,String regex){
        Thread t = new Thread(new RegexUtilExtended(filter,regex,ctrl.getActiveWorkbooks()));
        t.start();
    }
    
    
    
}
