/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marcadministrationclient;

/**
 *
 * @author patrice
 */
public class UpdateDataWorker extends javax.swing.SwingWorker<Void,Void>
{
 MainJFrame  _frame;
 Server server;
 String tablename;
 String[] field;
 String rowid;
 String[] text;
            @Override
    protected Void doInBackground()
    {
     //   server.connector.Lock();
        server.connector.directExecute = true;
        server.connector.openScript(null);
        server.connector.TABLE_Update(tablename, rowid, field, text);
      //  server.connector.UnLock();
        return null;
    }
    
    @Override
    protected void done() 
    {

 
        _frame.ShowInsertedData(rowid);
        
    }
    
}
