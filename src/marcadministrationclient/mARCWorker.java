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
import mARC.Connector.Connector;
/**
 *
 * @author patrice
 */
public class mARCWorker extends javax.swing.SwingWorker<Void,Void> {
    
    MainJFrame _frame;
    String Action;
    String ip;
    String port;
    String logMsg;
    String start;
    String end;
    String RebuildRef;
    String columns;
    Connector connector;
    String restart;
    String[] accessors;

    public mARCWorker()
    {
        Action = "none";
        logMsg = "";
    }
    @Override
    protected Void doInBackground() throws Exception
    {
        connector = new Connector();
        connector.setIp(ip);
        connector.setPort(port);
        connector.connect();
        if ( !connector.getIsConnected() )
        {
            logMsg += "Could not Connect to Server \n";
            return null;
        }
        
        connector.directExecute = true;
        connector.openScript(null);
        if ( Action.equals("SetProperties"))
        {
            connector.SERVER_SetProperties(accessors);
            if ( connector.result.mError )
            {
                this.logMsg +=" ERROR occured command was '"+connector.getToSend()+"'  server answer is '"+connector.result.mErrorMessage+"' \n";
                return null;
            }
        }   
        else if (Action.equals("Rebuild"))
        {
            connector.SESSION_MarcRebuild(columns, start, end, RebuildRef);
            if ( connector.result.mError )
            {
                this.logMsg +=" ERROR occured command was '"+connector.getToSend()+"'  server answer is '"+connector.result.mErrorMessage+"' \n";
                return null;
            }
        }
        else if(Action.equals("Save"))
        {
            connector.SESSION_MarcSave();
            if ( connector.result.mError )
            {
                this.logMsg +=" ERROR occured command was '"+connector.getToSend()+"'  server answer is '"+connector.result.mErrorMessage+"' \n";
                return null;
            }
        }
        else if (Action.equals("Reload"))
        {
            connector.SESSION_MarcReload();
            if ( connector.result.mError )
            {
                this.logMsg +=" ERROR occured command was '"+connector.getToSend()+"'  server answer is '"+connector.result.mErrorMessage+"' \n";
                return null;
            }
        }
        else if (Action.equals("Publish"))
        {
            connector.SESSION_MarcPublish();
            if ( connector.result.mError )
            {
                this.logMsg +=" ERROR occured command was '"+connector.getToSend()+"'  server answer is '"+connector.result.mErrorMessage+"' \n";
                return null;
            }
        }
        else if (Action.equals("Clear"))
        {
            connector.SESSION_MarcClear();
            if ( connector.result.mError )
            {
                this.logMsg +=" ERROR occured command was '"+connector.getToSend()+"'  server answer is '"+connector.result.mErrorMessage+"' \n";
                return null;
            }
        }
        else if(Action.equals("ShutDown"))
        {
            connector.SERVER_ShutDown(restart);
            if ( connector.result.mError )
            {
                this.logMsg +=" ERROR occured command was '"+connector.getToSend()+"'  server answer is '"+connector.result.mErrorMessage+"' \n";
                return null;
            }
        }
              
        logMsg += "'"+Action+"' task susscessfull. \n";
        return null;
    }
    
    @Override
    protected void done() 
    {
        try {
            Void result = get();
            if ( connector.getIsConnected() )
            {
                connector.disConnect();
            }
            _frame.Updatelog(logMsg);
        } catch (InterruptedException ex) {
            Logger.getLogger(mARCWorker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(mARCWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
