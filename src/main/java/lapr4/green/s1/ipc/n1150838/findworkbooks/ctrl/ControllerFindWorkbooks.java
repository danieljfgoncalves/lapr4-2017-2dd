/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lapr4.green.s1.ipc.n1150838.findworkbooks.ctrl;

import csheets.core.Workbook;
import eapli.framework.application.Controller;
import java.io.File;
import java.io.IOException;
import lapr4.green.s1.ipc.n1150838.findworkbooks.Directory;

/**
 *
 * @author nunopinto
 */
public class ControllerFindWorkbooks implements Controller {
    /**
     * The path to search for cls,etc.. files 
     */
    private Directory rootPath;
    
    public ControllerFindWorkbooks(String rootPath){
        this.rootPath = new Directory(new File(rootPath));
    }
    /**
     * The call to the method to search the files
     */
    public void searchFiles() {
        this.rootPath.searchFiles();
    }
    /**
     * The method to load a workbook
     * @param filePath to the workbook to load
     * @return the workbook
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    public Workbook load(String filePath) throws IOException, ClassNotFoundException{
        return this.rootPath.load(new File(filePath));
    }
    
}