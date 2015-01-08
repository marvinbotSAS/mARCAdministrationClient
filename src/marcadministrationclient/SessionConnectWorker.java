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
import mARC.Connector.*;
/**
 *
 * @author patrice
 */
public class SessionConnectWorker extends javax.swing.SwingWorker<Void,Void>
{
   public  javax.swing.Timer timer;
    public Connector connector;
    public Server server;
    public int mouseX;
    public int mouseY;
    MainJFrame _frame;
    String[] propertiesInstances;
    String[] properties_values;
    String[] spectrum_values;
    
    String id; // "-1" si on veut une nouvelle session ou id de la session sur laequlle se connecter
    boolean SessionFound;
    boolean SessionIsConnected;
    
    String logMsg;
    String Action;
    //
    public SessionConnectWorker()
    {
        this.logMsg = "";
        timer = null;
        propertiesInstances = properties_values = spectrum_values = null;
        SessionFound = SessionIsConnected = false;
        Action = "none";
        server = null;
        connector = null;
        id = "none";
        
    }
            @Override
    protected Void doInBackground() throws Exception
    {
        if ( server != null )
        {
            connector = server.connector;
        }
        if ( connector == null )
        {
            this.logMsg += "No Session or Server selected. Aborting. \n";
            return null;
        }
          //  connector.Lock();
            SessionIsConnected = connector.getIsConnected();
            if ( id.equals("-1"))
            {
                connector.connect();
                if (connector.getIsConnected())
                {
                    this.SessionIsConnected = true;
                }
            }
            else
            {
                if ( !this.SessionIsConnected )
                {
               //     connector.UnLock();
                    return null;
                }
            }
           connector.directExecute = false;
           connector.openScript(null);
           connector.SESSION_GetProperties(null);
           connector.SESSION_GetSpectrum();
           connector.executeScript();
           if ( connector.result.mError )
           {
               this.logMsg +=" ERROR occured command was '"+connector.getToSend()+"'  server answer is '"+connector.result.mErrorMessage+"' \n";
          //     connector.UnLock();
                return null;
           }
           properties_values = connector.getDataByName("prop_value", 0);
           
           this.spectrum_values = connector.getDataByName("value", 1);
           
           if ( Session.firstInit )
           {
               Session.properties = connector.getDataByName("prop_name", 0);
               Session.types = connector.getDataByName("prop_type", 0);
               Session.prop_access = connector.getDataByName("prop_access", 0);
               Session.spectrum_names = connector.getDataByName("name", 1);
               Session.spectrum_types = connector.getDataByName("type", 1);
               Session.firstInit = false;
           }
 
           if ( Action.equals("ShowAllConnections"))
           {
               server.updateSessions();
           }
           /*  properties : 
    name
last_time
owner_IP
owner_port
id
priority
session_timeout
exec_timeout
context_count
result_count
spectrum_string
profiler_context_string
inhibitor_context_string
result_max_stack_size
result_embedded
result_line_separator
result_column_separator
result_DBCursor

    */
      //  connector.UnLock();
       propertiesInstances = new String[9];
       propertiesInstances[0] = properties_values[4];
       propertiesInstances[1] = properties_values[0];
        propertiesInstances[2] = properties_values[8];
        propertiesInstances[3] = properties_values[2];
        propertiesInstances[4] = properties_values[3];
        propertiesInstances[5] = properties_values[7];
        propertiesInstances[6] = properties_values[6];
        propertiesInstances[7] = properties_values[19];
        propertiesInstances[8] = "false";
        
        return null;
    }
    
    @Override
    protected void done() 
    {

       try {
           Void result = get();
           if ( _frame == null )
           {
               return;
           }
           _frame.Updatelog(this.logMsg);
           
           if ( !this.SessionIsConnected )
           {
               _frame.LogSessionNotConnected(id);
           }
           if ( id.equals("-1"))
           {
               _frame.AddASession(propertiesInstances,properties_values, this.spectrum_values, connector,mouseX,mouseY);
           }
           else
           {
               _frame.CheckSessionConnection(properties_values,this.spectrum_values, id);
           }
           
           _frame.updateSessionSpectrum(spectrum_values);
           
           if ( Action.equals("ShowAllConnections") )
           {
               _frame.updateServerSessionsjTable(server.sessions);
           }
           
           if (timer != null)
           {
               timer.start();
           }
       } catch (InterruptedException ex) {
           Logger.getLogger(SessionConnectWorker.class.getName()).log(Level.SEVERE, null, ex);
       } catch (ExecutionException ex) {
           Logger.getLogger(SessionConnectWorker.class.getName()).log(Level.SEVERE, null, ex);
       }
        
    }
}
