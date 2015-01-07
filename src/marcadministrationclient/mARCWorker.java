/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
