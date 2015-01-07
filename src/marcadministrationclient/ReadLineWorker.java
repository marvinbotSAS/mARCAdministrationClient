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
public class ReadLineWorker extends SwingWorker<Void,Void>
{
    MainJFrame _frame;
    boolean showABSTRACT;
    int abstractFielIndex;
    public String tablename;
    public Server server;
    public String rowid;
    public String field;
    
    String content;
    
    public ReadLineWorker()
    {
        
    }
        @Override
    protected Void doInBackground()
    {



           // server.connector.Lock();
            server.connector.directExecute = true;
            server.connector.openScript(null);
            server.connector.TABLE_ReadLine(tablename, rowid , new String[]{field} );
            String[] result = server.connector.getDataByName(field,-1);
            
            content = result[0];
            
           server.connector.executeScript();
        //   server.connector.UnLock();
 
        return null;
    }
    
    @Override
    protected void done() 
    {


    }
}
