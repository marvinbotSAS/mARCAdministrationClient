/*
 * Copyright (C) 2015 Marvinbot S.A.S
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
