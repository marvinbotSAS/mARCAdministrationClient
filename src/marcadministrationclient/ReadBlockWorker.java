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
public class ReadBlockWorker extends javax.swing.SwingWorker<Void,Void>
{
  MainJFrame  _frame;
 Server server;
 String tablename;
 String field;
 String rowid;
 String content;
 
            @Override
    protected Void doInBackground()
    {
      //  server.connector.Lock();
        server.connector.directExecute = true;
        String start = "1";
        String count = "4096";
        String[] r;
        String[] data;
        content = "";
        int nextPosition = -1;
        while (nextPosition != 0)
        {
            server.connector.openScript(null);
            server.connector.TABLE_ReadBlock(tablename, rowid, field, start, count);
            r = server.connector.getDataByName("NextPosition", -1);
            data = server.connector.getDataByName("Data",-1);
            content += data[0];
            nextPosition = Integer.parseInt(r[0]);
            start = r[0];
        }
     //  server.connector.UnLock();
        return null;
    }
    
    @Override
    protected void done() 
    {

        
    }
}
