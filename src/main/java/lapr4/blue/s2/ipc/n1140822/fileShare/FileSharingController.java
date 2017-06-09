/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lapr4.blue.s2.ipc.n1140822.fileShare;

import lapr4.green.s1.ipc.n1150532.comm.CommTCPClientsManager;
import lapr4.green.s1.ipc.n1150532.comm.connection.ConnectionID;

/**
 *
 * @author Renato Oliveira 1140822@isep.ipp.pt
 */
public class FileSharingController {

    private ConnectionID connection;

    public FileSharingController(ConnectionID connection) {
        this.connection = connection;
    }

    public boolean requestFile(String fileName) {
        CommTCPClientsManager.getManager().requestFile(connection, fileName);
        return true;
    }
}