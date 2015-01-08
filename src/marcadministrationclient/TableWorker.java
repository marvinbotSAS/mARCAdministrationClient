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
public class TableWorker extends SwingWorker<Void,Void>
{
    MainJFrame _frame;
    Server server;
    String tablename;
    String field;
    String Action;
    String boolUnique;
    
    String[] tables;
    String[] rowids;
    String[] fields;
    String[][] values;
    String logMsg;

    public TableWorker() {
        this.logMsg = "";
    }
                @Override
    protected Void doInBackground()
    {
        if ( _frame == null )
        {
            this.logMsg += "No frame to get results. Aborting table worker processing. \n";
        }
        if ( server == null )
        {
            this.logMsg += "No server defined. Aborting table worker processing. \n";
            return null;
        }
    //  server.connector.Lock();
       server.connector.directExecute = true;
       server.connector.openScript(null);
       if ( Action.equals("Create"))
       {
           server.connector.TABLE_Create(tablename, "NULL", "NULL", "100000", field);
       }
       else if (Action.equals("Kill"))
       {
           server.connector.TABLE_Kill(tablename);
       }
       else if (Action.equals("BTreeCreate") )
       { 
           server.connector.TABLE_BIndexCreate(tablename, field, boolUnique);
       }
       else if (Action.equals("BTreeKill") )
       { 
           server.connector.TABLE_BIndexDelete(tablename, field);
       }
       else if ( Action.equals("BTreeRebuild"))
       {
           server.connector.TABLE_BIndexRebuild(tablename,field, "true");
       }
       else if (Action.equals("KTreeCreate"))
       { 
           server.connector.TABLE_KIndexCreate(tablename, field);
       }
       else if ( Action.equals("KTreeKill"))
       {
           server.connector.TABLE_KIndexDelete(tablename, field);
       }
       else if ( Action.equals("KTreeRebuild"))
       {
           server.connector.TABLE_KIndexRebuild(tablename,field, "true");
       }
       else if ( Action.equals("DeleteRows"))
       {
           server.connector.TABLE_Delete(tablename, rowids);
       }
       else if (Action.equals("UpdateData"))
       {
           server.connector.directExecute = false;
           server.connector.openScript(null);
           for (int i = 0; i < rowids.length;i++)
           {
               for (int k = 0; k < fields.length;k++)
               {
                   String[] v = values[k];
                   server.connector.TABLE_Update(tablename, rowids[i], fields, v);
               }
           }
           server.connector.executeScript();
       }
       else if (Action.equals("AddData"))
       {
           server.connector.directExecute = false;
           server.connector.openScript(null);
           for (String rowid : rowids) 
           {
               for (int k = 0; k < fields.length; k++) 
               {
                   String[] v = values[k];
                   server.connector.TABLE_DataAdd(tablename, rowid, fields[k], v[0]);
               }
           }
           server.connector.executeScript();
       }
       else if (Action.equals("InsertData"))
       {
           server.connector.directExecute = false;
           server.connector.openScript(null);


            String[] v = values[0];
            server.connector.TABLE_Insert(tablename, fields, v);

           server.connector.executeScript();
       }
       // on maj les stats
       server.UpdateTablesStats();
       
    //   server.connector.UnLock();
        return null;
    }
    
    @Override
    protected void done() 
    {
        _frame.Updatelog(this.logMsg);
        if ( Action.equals("DeleteRows"))
        {
            this._frame.UpdateTableContent(true);
        }
        _frame.updateCurrentServerTablesStats(server);
    }
}
