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

/**
 *
 * @author patrice
 */
public class SelectFromTableWorker extends javax.swing.SwingWorker<Void,Void>
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
       // server.connector.Lock();
        server.connector.directExecute = true;
        server.connector.openScript(null);
        server.connector.TABLE_Update(tablename, rowid, field, text);
    //    server.connector.UnLock();
        return null;
    }
    
    @Override
    protected void done() 
    {

 
        _frame.ShowInsertedData(rowid);
        
    }
    
}
