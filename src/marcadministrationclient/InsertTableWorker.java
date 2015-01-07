/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marcadministrationclient;

import javax.swing.SwingWorker;

/**
 *
 * @author patrice
 */
public class InsertTableWorker extends SwingWorker<Void,Void>
{
    MainJFrame _frame;
    Server server;
    String tablename;
    String[] fields;
    String[] values;
    String text;
    String mode;
    String id;
    
                @Override
    protected Void doInBackground()
    {
       // server.connector.Lock();
        server.connector.directExecute = true;
        server.connector.TABLE_Insert(tablename, fields, values);
       // server.connector.UnLock();
        return null;
    }
    
    @Override
    protected void done() 
    {

    }
}
