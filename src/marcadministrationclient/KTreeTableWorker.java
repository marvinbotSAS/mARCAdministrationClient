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
public class KTreeTableWorker extends SwingWorker<Void,Void>
{
    MainJFrame _frame;
    Server server;
    String tablename;
    String field;
    String boolUnique;
    String boolInterrupt;
    String Action;
    String logMsg;

    public KTreeTableWorker() {
        this.logMsg = "";
    }
    
                @Override
    protected Void doInBackground()
    {
      // server.connector.Lock();
       server.connector.directExecute = true;
       server.connector.openScript(null);
       if ( Action.equals("Create"))
       {
           server.connector.TABLE_KIndexCreate(tablename, field);
       }
       else if (Action.equals("Delete"))
       {
           server.connector.TABLE_KIndexDelete(tablename,field );
       }
       else if (Action.equals("Rebuild"))
       {
           server.connector.TABLE_KIndexRebuild(tablename, field,  boolInterrupt);
       }

      if ( server.connector.result.mError )
      {
       this.logMsg +=" ERROR occured script was '"+server.connector.getToSend()+"'  server answer is '"+server.connector.result.mErrorMessage+"' \n";
      }
       // on maj 
       
       server.UpdateTablesStats();
       
     //  server.connector.UnLock();
        return null;
    }
    
    @Override
    protected void done() 
    {
if ( _frame != null )
{
    _frame.Updatelog(logMsg);
    _frame.updateCurrentServerTablesStats(server);
}
    }
}
