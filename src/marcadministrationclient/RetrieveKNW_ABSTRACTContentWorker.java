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
public class RetrieveKNW_ABSTRACTContentWorker extends SwingWorker<Void,Void>
{
    Server server;
    MainJFrame _frame;
    String rowid;
    String content;
    public String[] fields;
    public RetrieveKNW_ABSTRACTContentWorker()
    {
        
    }
        @Override
    protected Void doInBackground()
    {
        content ="";
       // server.connector.Lock();
        server.connector.directExecute = false;
        server.connector.openScript(null);
        server.connector.SESSION_DocToContext(rowid, "false");
        //server.connector.CONTEXTS_Fetch(rowid, rowid, rowid);
       server.connector.executeScript();
   //    server.connector.UnLock();
        return null;
    }
    
    @Override
    protected void done() 
    {


          _frame.UpdateContentTablejTextArea(rowid);


        
        
    }
}
