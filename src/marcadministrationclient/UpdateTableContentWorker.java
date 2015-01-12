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

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import mARC.Connector.Connector;

/**
 *
 * @author patrice
 */
public class UpdateTableContentWorker extends SwingWorker<Void,Void>
{
    MainJFrame _frame;
    boolean showABSTRACT;
    
    boolean restore;
    String ip;
    String port;
    int abstractFielIndex;
    public Table table;
    public Server server;
    public int start;
    public int size;
    public String[] fields;
    
    String logMsg;
    
    public String[][] lines;
    public String[] shapes;
    public UpdateTableContentWorker()
    {
        ip = "";
        port = "";
        this.restore = false;
        this.logMsg = "";
        showABSTRACT = false;

        this.abstractFielIndex = -1;
    }
        @Override
    protected Void doInBackground() throws Exception
    {
 
        Connector connector = new Connector();
        connector.setIp(ip);
        connector.setPort(port);
        
        if (!connector.connect() )
        {
            this.logMsg += " Unable to connect to server '"+ip+":"+"port. Aborting \n";
            return null;
        }
        if ( table == null )
       {
           this.logMsg +="WARNING: No table supplied. Aborting table content visualization. \n";
           return null;
       }

      int end  = Integer.parseInt(table.lines) - 1;

      end = Math.min(end, start + size );

        if ( !showABSTRACT)
        {
            connector.directExecute = false;
            connector.openScript(null);
            for (int i = start; i < end ;i++ )
            {
                connector.TABLE_ReadLine(table.name, String.valueOf(i) , fields);
            }
           connector.executeScript();
           
           this.lines = new String[this.fields.length][this.size];
           for (int i = 0; i < this.fields.length;i++)
           {
               for(int j = 0; j < size ; j++)
               {
                   lines[i][j] = connector.getDataByName(fields[i], j)[0];
               }
           }
        }
        else
        {
            // on extrait le champs KNW_ABSTRACT qui est accede d'une autre maniere
            String[] nf = new String[ fields.length - 1  ];

            if (nf.length > 0 )
            {
                connector.directExecute = false;
                connector.openScript(null);
                int k = 0;
                for (int i = 0; i< fields.length ;i++)
                {
                        if ( i != abstractFielIndex && k < nf.length )
                        {
                            nf[k++] = fields[i];
                        }
                }
                // on recupere les lignes de tous les champs hors KNW_ABSTRACT
                for (int ii = start; ii < end ;ii++ )
                {
                    connector.TABLE_ReadLine(table.name, String.valueOf(ii) , nf);
                    // on lit le champs KNW_ABSTRACT du doc i
                    connector.SESSION_DocToContext(String.valueOf(ii), "false");
                    connector.CONTEXTS_Fetch("2000", "1", "1");
                    connector.CONTEXTS_Drop("1");
                }
                connector.executeScript();
                this.lines = new String[this.fields.length][this.size];
                int ReadLine_index = 0;
                for (int ij = 0; ij < fields.length;ij++)
                {
                    if ( ij == abstractFielIndex )
                    {
                        continue;
                    }
                    ReadLine_index = 0;
                    for(int j = 0; j < size ; j++)
                    {
                        lines[ij][j] = connector.getDataByName(fields[ij], ReadLine_index)[0];
                        ReadLine_index += 4;
                    }
                }
                // maintenant l' abstract
                    ReadLine_index = 2;
                    String[] s;
                    String Abstract = "";
                    for(int j = 0; j < size ; j++)
                    {
                        s = connector.getDataByName("shape", ReadLine_index);
                        Abstract = "";
                        for (k = 0; k < s.length;k++)
                        {
                             Abstract += s[k]+", ";
                        }
                        lines[abstractFielIndex][j] = Abstract;
                        ReadLine_index += 4;
                    }
            }

            
        }
        connector.disConnect();
        return null;
    }
    
    @Override
    protected void done() 
    {
        try {
            Void result = get();  
            _frame.UpdateCurrentTableContent(this.restore,this.showABSTRACT,abstractFielIndex, size,fields,lines);
        } catch (InterruptedException ex) {
            Logger.getLogger(UpdateTableContentWorker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(UpdateTableContentWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
