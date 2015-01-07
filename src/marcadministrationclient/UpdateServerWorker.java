/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marcadministrationclient;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

import mARC.Connector.Connector;
import static marcadministrationclient.Server.sessionsNamesStock;
/**
 *
 * @author patrice
 */
public class UpdateServerWorker extends SwingWorker<Void,Void>
{

    Connector connector;
    private  String logMsg;
    Server server;
    MainJFrame frame;

    UpdateServerWorker(Server s)
    {
        this.frame = null;
        this.logMsg = "";
        if ( s == null )
        {
            this.logMsg = "No server supplied. \n";
        }
        connector = new Connector();
        SetServer(s);
    }
    void SetServer(Server s)
    {
        connector.setIp(s.ip);
        connector.setPort(s.port);
        server = s;
    }
    @Override
    protected Void doInBackground() throws Exception
    {
        try
        {
        if ( !connector.connect() )
        {
            this.logMsg +="Unable to connect to server '"+connector.getIp()+":"+connector.getPort()+"' \n";
            return null;
        }


        connector.directExecute = false;
        connector.openScript(null);
        connector.SERVER_GetProperties(null);
        connector.SESSION_GetInstances("1", "-1");
        connector.SERVER_GetTasks();
        connector.executeScript();
        if ( connector.result.mError )
        {
                this.logMsg +=" ERROR occured script was '"+connector.getToSend()+"'  server answer is '"+connector.result.mErrorMessage+"' \n";
                connector.disConnect();
                return null;
        }
        String[] props = connector.getDataByName("prop_value", 0);
        server.name = props[0];
        server.type = props[2];
        server.model= props[3];
        server.version = props[4];
        server.build= props[5];
        server.connection_count= props[6];
        server.command_threads= props[7];
        server.time_local= props[8];
        server.time_gmt= props[9];
        server.up_time= props[10];
        server.idle_time= props[11];
        server.cache_size= props[12];
        server.cache_used = props[13];
        server.cache_hits= props[14];
        server.exec_timeout_default= props[15];
        server.session_timeout_default= props[16];
        server._marc.relations = props[17];
        server._marc.shapes = props[18];
        server._marc.references  =props[19];
        server._marc.particles = props[20];
        server.indexation_cache_used = props[21];
        server.indexation_cache_size= props[22];
        server.indexation_timeout = props[23];
        server._marc.quality = props[24];
        
        // les sessions


        String[] ids = connector.getDataByName("id", 1);
        if ( ids == null || ids.length == 0)
        {
            server.sessions.clear();

        }
        else
        {
            String[] names = connector.getDataByName("name", 1);
            String[] persistant = connector.getDataByName("persistant", 1);
            String[] owner_ip = connector.getDataByName("owner_ip", 1);
            String[] owner_port = connector.getDataByName("owner_port", 1);
            String[] priority = connector.getDataByName("priority", 1);
            String[] exec_timeout = connector.getDataByName("exec_timeout", 1);
            String[] session_timeout = connector.getDataByName("session_timeout", 1);
            String[] debug = connector.getDataByName("debug", 1);
            for (Session se : server.sessions)
            {
              se.toUpdate = false;
            }

            server.ServerSession.toUpdate = true;
            server.toName.clear();

            int i = 0;
            for( String n : ids)
            {
                if ( n.equals(this.server.Session_Id) || n.equals(connector.SessionId) )
                {
                    i++;
                    continue;
                }
                Session se = server.FindSessionFromId(n);
                if ( se == null )
                {
                    se = new Session();
                    se.connector = this.server.connector;
                    se.owned = false;
                    se.owner = server;
                    se.connector.setIp(server.connector.getIp());
                    se.connector.setPort(server.connector.getPort());
                    // on enregistre la session
                    server.connector.AddASessionId(n);
                    //se.connector.connect();
                    se.id = n;
                    server.sessions.add(se);
                }
                se.name = names[i];
                se.owner_ip = owner_ip[i];
                se.owner_port = owner_port[i];
                se.persistant = persistant[i];
                se.exec_timeout = exec_timeout[i];
                se.session_timeout = session_timeout[i];
                se.priority = priority[i];
                se.debug = debug[i++];
                se.toUpdate = true; 
            }

             for (Session se : server.sessions)
            {
              if ( !se.toUpdate)
              {
                  // si la session n'est pas attachee au serveur
                  // on deconnecte la session du serveur
                  if ( !server.connector.FindASessionId(se.id) )
                  {
                      se.connector.disConnect();
                  }
                  server.sessionsToRemove.add(se);
                  server.connector.RemoveASessionId(se.id);
              }
            }

            server.sessions.removeAll(server.sessionsToRemove);
            server.sessionsToRemove.clear();
        }
        // les tasks
        String[] task = connector.getDataByName("task", 2);
        if ( task == null || task.length == 0)
        {
            server.tasks.clear();
        }
        else
        {
            String[] completions = connector.getDataByName("completion", -1);
            String[] currents = connector.getDataByName("current", -1);
            String[] froms = connector.getDataByName("from", -1);
            String[] tos = connector.getDataByName("to", -1);
            String[] elapseds = connector.getDataByName("elapsed", -1);


            for (Task t : server.tasks)
            {
              t.toUpdate = false;
            }

            int i = 0;
            for( String n : task)
            {
                Task t = server.FindTaskFromName(n);
                if ( t == null )
                {
                    t = new Task();
                    server.tasks.add(t);
                    t.task = n;
                }
                t.completion = completions[i];
                t.current = currents[i];
                t.from = froms[i];
                t.to = tos[i];
                t.elapsed = elapseds[i++];
                t.toUpdate = true; 
            }
        
            for (Task t : server.tasks)
            {
              if ( !t.toUpdate)
              {
                  server.tasksToRemove.add(t);
              }
            }
             server.tasks.removeAll(server.tasksToRemove);
             server.tasksToRemove.clear();
        }
        UpdateTablesStats();
        connector.disConnect();
        }
        catch(Exception e)
        {
            int status = 0;
        }
        return null;
    }
  
    public void UpdateTablesStats()
    {
        connector.directExecute = true;
        connector.openScript(null);
        connector.TABLE_GetInstances("1", "-1");
        String[] tbls = connector.getDataByName("tables", -1);
        
        
        connector.SESSION_GetLastDBInfo(); // on recupere les operations en cours sur les tables
        
        String[] LastDBInfoTable = connector.getDataByName("table", 0);
        String[] LastDBInfoOperation = connector.getDataByName("operation", 0);
        String[] LastDBInfoId = connector.getDataByName("id", 0);
        String[] LastDBInfoStatus = connector.getDataByName("status", 0);
        if ( LastDBInfoTable !=null && LastDBInfoTable.length != 0 )
        {    
            server.lastDBInfoTableName =   LastDBInfoTable[0];    
            server.lastDBInfoOperation = LastDBInfoOperation[0];
            server.lastDBInfoId     = LastDBInfoId[0];
            server.lastDBInfoStatus = LastDBInfoStatus[0];
        }        
        else
        {
            server.lastDBInfoTableName =   "none";    
            server.lastDBInfoOperation =  "none"; 
            server.lastDBInfoId     =  "none"; 
            server.lastDBInfoStatus =  "none"; 
            
        }
        
        synchronized(server.tables)
        {
            if ( tbls == null || tbls.length == 0)
            {
                server.tables.clear();
                return;
            }
            for (Table t : server.tables)
            {
              t.toUpdate = false;
            }

            Table tb = null;
            for ( String t : tbls )
            {
                tb = server.FindTableFromName(t);
                if ( tb == null )
                {
                    tb = new Table();
                    tb._server = server;
                    tb.name = t;
                    server.tables.add(tb);
                }
                tb.toUpdate = true;

            }        

            connector.directExecute = false;
            for ( Table t : server.tables)
            {
                if ( !t.toUpdate )
                {
                    server.toRemove.add(t);
                    continue;
                }
                connector.openScript(null);
                connector.TABLE_GetStructure(t.name);
                connector.TABLE_GetLines(t.name);
                connector.TABLE_GetBIndexes(t.name);
                connector.TABLE_GetKIndexes(t.name);
                connector.executeScript();
                String[] names = connector.getDataByName("name", 0);
                String[] types = connector.getDataByName("type", 0);
                String[] sizes = connector.getDataByName("size", 0);


                String[] allNames = new String[names.length+1];
                String[] allTypes = new String[ types.length+1];
                String[] allSizes = new String[sizes.length+1];
                allNames[0]="RowId";
                allTypes[0] = "INT32";
                allSizes[0] = "0";
                for ( int i = 0; i <names.length;i++)
                {
                    allNames[i+1] = names[i];
                    allTypes[i+1] = types[i];
                    allSizes[i+1] = sizes[i];
                }

                t.UpdateStructure(allNames, allTypes, allSizes);

                String[] lines = connector.getDataByName("lines", 1);
                t.lines = lines[0];

                names = connector.getDataByName("index_name", 2);
                String[] status = connector.getDataByName("status", 2);
                String[] progress = connector.getDataByName("progress", 2);
                String[] unique = connector.getDataByName("unique", 2);
                t.UpdateBIndexes(names, status, progress, unique);

                names = connector.getDataByName("index_name", 3);
                status = connector.getDataByName("status", 3);
                progress = connector.getDataByName("progress", 3);
                t.UpdateKIndexes(names, status, progress);


            }

            // on enleve celles qui n'ont pas de toUpdate = true
            server.tables.removeAll(server.toRemove);
            server.toRemove.clear();
        }
        
    }
    @Override
    protected void done() 
    {
        try 
        {
             Void result = get();
            if ( frame == null )
            {
                return;
            }
            if ( frame.MainWindowjSplitPane.getDividerLocation() < 30)
            {
                frame.timerToUpdateServerStats.stop();
            }
            else
            {
                frame.timerToUpdateServerStats.start();
            }
            frame.Updatelog(logMsg);
            frame.updateServerStats(server);
        } catch (InterruptedException ex) {
            Logger.getLogger(UpdateServerWorker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(UpdateServerWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
                
}