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
