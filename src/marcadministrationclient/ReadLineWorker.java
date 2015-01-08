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
