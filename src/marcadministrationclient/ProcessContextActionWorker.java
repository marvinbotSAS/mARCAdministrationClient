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
import java.awt.Component;
import javax.swing.SwingWorker;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author patrice
 */
public class ProcessContextActionWorker extends SwingWorker<Void,Void>
{
    Component componentToEnable;
    
    static int maxContextIndex = 0;
    
    static ArrayDeque<String> NamesStock = new ArrayDeque<>();
    
    public Session session;
    MainJFrame _frame;
    boolean fetchContent;
    boolean getSpectrum;
    boolean setSpectrum;
    boolean fetchAll;
    int contextIndex;
    
    Map<String,ArrayList<String> > nodesMap;
    
    boolean fetchTopMostContent;
    boolean getproperties;
    boolean setproperties;
    String accessors;
    private final ArrayList<Integer> toName;
    
    String range;
    String consolidation;
    
    String name;
    String[] shapes, activities,generalities, gen_class,id;
    String[] spectrum_values, values;
    
    public int[] rows;
    
    ArrayList<String> roots; // les noeuds du graphe issus d'une query
    
    String script;
    
    String Action;
    String logMsg;
    ProcessContextActionWorker()
    {
        this.script = "";
        this.nodesMap = Collections.synchronizedMap(new HashMap<String,ArrayList<String>>());

        this.componentToEnable = null;
        this.toName = new ArrayList<>();
        contextIndex = -1;
        shapes = activities = gen_class = id = null;
        getSpectrum = fetchContent = fetchAll = setSpectrum = getproperties = setproperties = false;
        _frame = null;
        this.logMsg = "";
        fetchAll=fetchContent=fetchTopMostContent = false;
        Action = "none";
        name = null;
    }
    
    static public String GetAContextName()
    {
        if ( NamesStock.isEmpty() )
        {
            for (int i = 0; i < 10; i++ )
            {
                NamesStock.add(new String("context#"+String.valueOf(maxContextIndex++) ) );
            }

        }
       return NamesStock.removeFirst();      
    }
    
    static public void ReleaseAContextName(String name)
    {
        if ( NamesStock.contains(name))
        {
            return;
        }
        NamesStock.add(name);
    }
    
    @Override
    protected Void doInBackground() throws Exception
    {
        
        if ( !session.connector.getIsConnected() )
        {
            if ( !session.connector.connect())
            {
                this.logMsg += "Session '"+session.id+"' Unable to connect to server '"+session.connector.getIp()+":"+session.connector.getPort()+"' \n";
                return null;
            }
        }
        
        if ( this.fetchTopMostContent )
        {
            fetchContent = true;
            this.contextIndex = 0;
            fetchAll = true;

        }
        
        if ( this.contextIndex == -1)
        {
            this.fetchContent = false;
        }
        
        if ( fetchContent )
        {
            this.getproperties = true;
            this.getSpectrum = true;
        }
        
     //   session.connector.Lock();
        session.connector.directExecute = true;
        
        try
        {
        
        switch (Action) {
            case "none":
                break;
            case "ExecuteScript":
                fetchAll = true;
                session.connector.directExecute = true;
                session.connector.openScript(null);
                session.connector.executeCommand(script);
                if ( session.connector.result.mError )
                {
                    this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
             //       session.connector.UnLock();
                    return null;
                }   
                script += session.connector.RawScript+"\n";       
                break;
            case "ClearSession":
                fetchAll = true;
                session.connector.directExecute = true;
                session.connector.openScript(null);
                session.connector.SESSION_Clear("");
                if ( session.connector.result.mError )
                {
                    this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
             //       session.connector.UnLock();
                    return null;
                }
                script += session.connector.RawScript+"\n";                
                break;
            case "Clear":
                fetchAll = true;
                session.connector.directExecute = true;
                session.connector.openScript(null);
                session.connector.SESSION_Clear("contexts");
                if ( session.connector.result.mError )
                {
                    this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
             //       session.connector.UnLock();
                    return null;
                }
                script += session.connector.RawScript+"\n";
                break;
            case "New":
                session.connector.directExecute = true;
                session.connector.openScript(null);
                session.connector.CONTEXTS_New();
                if ( session.connector.result.mError )
                {
                    this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
             //       session.connector.UnLock();
                    return null;
                }
                script += session.connector.RawScript+"\n";
                break;
            case "Learn":
                session.connector.directExecute = false;
                session.connector.openScript(null);
                for (int l :  rows)
                {
                    session.connector.CONTEXTS_OnTop(String.valueOf(l + 1));
                    session.connector.CONTEXTS_Learn();
                }
                session.connector.executeScript();
                if ( session.connector.result.mError )
                {
                    this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
               //     session.connector.UnLock();
                    return null;
                }                 
                script += session.connector.RawScript+"\n";
                break;
            case "Normalize":
                session.connector.directExecute = false;
                session.connector.openScript(null);
                for (int l :  rows)
                {
                    session.connector.CONTEXTS_OnTop(String.valueOf(l + 1));
                    session.connector.CONTEXTS_Normalize(range);
                }
                session.connector.executeScript();
                if ( session.connector.result.mError )
                {
                    this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
                 //   session.connector.UnLock();
                    return null;
                }
                script += session.connector.RawScript+"\n";
                break;
            case "Split":
                session.connector.directExecute = false;
                session.connector.openScript(null);
                for (int l :  rows)
                {
                    session.connector.CONTEXTS_OnTop(String.valueOf(l + 1));
                    session.connector.CONTEXTS_Split();
                }
                session.connector.executeScript();
                if ( session.connector.result.mError )
                {
                    this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
             //       session.connector.UnLock();
                    return null;
                }  
                script += session.connector.RawScript+"\n";
                break;
            case "onTop":
                session.connector.directExecute = false;
                session.connector.openScript(null);
                for (int l :  rows)
                {
                    session.connector.CONTEXTS_OnTop(String.valueOf(l + 1));
                }
                session.connector.executeScript();
                if ( session.connector.result.mError )
                {
                    this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
          //          session.connector.UnLock();
                return null;
                }   
                this.contextIndex = 0;
                script += session.connector.RawScript+"\n";
                break;
            case "Intersection":
                session.connector.directExecute = false;
                session.connector.openScript(null);
                for (int row : rows)
                {
                    session.connector.CONTEXTS_OnTop(String.valueOf(row + 1));
                }
                session.connector.CONTEXTS_Intersection( String.valueOf(rows.length - 1), consolidation);
                session.connector.executeScript();
                if ( session.connector.result.mError )
                {
                    this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
              //      session.connector.UnLock();
                    return null;
                }   
                script += session.connector.RawScript+"\n";
                break;
            case "Union":
                session.connector.directExecute = false;
                session.connector.openScript(null);
                for (int row : rows)
                {
                    session.connector.CONTEXTS_OnTop(String.valueOf(row + 1));
                }
                session.connector.CONTEXTS_Union( String.valueOf(rows.length - 1), consolidation);
                session.connector.executeScript();
                if ( session.connector.result.mError )
                {
                    this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
        //            session.connector.UnLock();
                    return null;
                }   
                script += session.connector.RawScript+"\n";
                break;
            case "Amplify":
                session.connector.directExecute = false;
                session.connector.openScript(null);
                for (int row : rows)
                {
                    session.connector.CONTEXTS_OnTop(String.valueOf(row + 1));
                    session.connector.CONTEXTS_Amplify(range, consolidation);
                }
                session.connector.executeScript();

                if ( session.connector.result.mError )
                {
                    this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
        //            session.connector.UnLock();
                    return null;
                }  
                script += session.connector.RawScript+"\n";
                break;
            case "Drop":
                fetchAll = true;
                session.connector.directExecute = false;
                session.connector.openScript(null);
                for (int row : rows)
                {
                    session.connector.CONTEXTS_OnTop(String.valueOf(row + 1));
                }
                session.connector.CONTEXTS_Drop( String.valueOf(rows.length) );
                session.connector.executeScript();
                if ( session.connector.result.mError )
                {
                    this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
           //         session.connector.UnLock();
                    return null;
                }
                script += session.connector.RawScript+"\n";
                break;
            case "Dup":
                session.connector.directExecute = false;
                session.connector.openScript(null);
                for (int row : rows)
                {
                    session.connector.CONTEXTS_OnTop(String.valueOf(row + 1));
                }
                session.connector.CONTEXTS_Dup( String.valueOf(rows.length) );
                session.connector.executeScript();
                if ( session.connector.result.mError )
                {
                    this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
          //          session.connector.UnLock();
                    return null;
                }   
                script += session.connector.RawScript+"\n";
                break;
            case "Swap":
                session.connector.directExecute = false;
                session.connector.openScript(null);
                for (int row : rows)
                {
                    session.connector.CONTEXTS_OnTop(String.valueOf(row + 1));
                }
                session.connector.CONTEXTS_Swap( String.valueOf(rows.length) );
                session.connector.executeScript();
                if ( session.connector.result.mError )
                {
                    this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
           //         session.connector.UnLock();
                    return null;
                }   
                script += session.connector.RawScript+"\n";
                break;
            case "stringToContext":
                session.connector.directExecute = true;
                session.connector.SESSION_StringToContext(range, consolidation);
                if ( session.connector.result.mError )
                {
                    this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
        //            session.connector.UnLock();
                    return null;
                }  
                script += session.connector.RawScript+"\n";
                break;
            case "SortBy":
                session.connector.directExecute = false;
                session.connector.openScript(null);
                for (int row : rows)
                {
                    session.connector.CONTEXTS_OnTop(String.valueOf(row + 1));
                    session.connector.CONTEXTS_SortBy(range, consolidation);
                }
                session.connector.executeScript();

                if ( session.connector.result.mError )
                {
                    this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
          //          session.connector.UnLock();
                    return null;
                }   
                script += session.connector.RawScript+"\n";
                break;
            case "ContextToContext":
                session.connector.directExecute = false;
                session.connector.openScript(null);
                for (int row : rows)
                {
                    session.connector.CONTEXTS_OnTop(String.valueOf(row + 1));
                    session.connector.SESSION_ContextToContext();
                }
                session.connector.executeScript();
                if ( session.connector.result.mError )
                {
                    this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
          //          session.connector.UnLock();
                    return null;
                }   
                script += session.connector.RawScript+"\n";
                break;
            case "ContextToInhibitor":
                session.connector.directExecute = false;
                session.connector.openScript(null);
                for (int row : rows)
                {
                    session.connector.CONTEXTS_OnTop(String.valueOf(row + 1));
                    session.connector.SESSION_ContextToInhibitor();
                }
                session.connector.executeScript();
                if ( session.connector.result.mError )
            {
                this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
          //      session.connector.UnLock();
                return null;
            }   
                script += session.connector.RawScript+"\n";
                break;
            case "InhibitorToContext":
                session.connector.directExecute = true;
                session.connector.SESSION_InhibitorToContext();
                if ( session.connector.result.mError )
            {
                this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
         //       session.connector.UnLock();
                return null;
            }   
                script += session.connector.RawScript+"\n";
                break;
            case "ContextToProfiler":
                session.connector.directExecute = false;
                session.connector.openScript(null);
                for (int row : rows)
                {
                    session.connector.CONTEXTS_OnTop(String.valueOf(row + 1));
                    session.connector.SESSION_ContextToProfiler();
                }
                session.connector.executeScript();
                if ( session.connector.result.mError )
            {
                this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
        //        session.connector.UnLock();
                return null;
            }   
                script += session.connector.RawScript+"\n";
                break;
            case "ProfilerToContext":
                session.connector.directExecute = true;
                session.connector.SESSION_ProfilerToContext();
                if ( session.connector.result.mError )
            {
                this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
         //       session.connector.UnLock();
                return null;
            }   
                script += session.connector.RawScript+"\n";
                break;
            case "ContextToDoc":
                session.connector.directExecute = false;
                session.connector.openScript(null);
                for (int row : rows)
                {
                    session.connector.CONTEXTS_OnTop(String.valueOf(row + 1));
                    session.connector.SESSION_ContextToDoc();
                }
                session.connector.executeScript();
                if ( session.connector.result.mError )
            {
                this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
         //       session.connector.UnLock();
                return null;
            }   
                script += session.connector.RawScript+"\n";
                break;
                // on recupere le content d'un context
            case "DocToContext":
                session.connector.directExecute = false;
                session.connector.openScript(null);
                for (String rowid : id)
                {
                    session.connector.SESSION_DocToContext(rowid,consolidation);
                }   
                session.connector.executeScript();
                if ( session.connector.result.mError )
            {
                this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
             //   session.connector.UnLock();
                return null;
            }   
                script += session.connector.RawScript+"\n";
                break;
            case "ShowOneContextProperties":
                if ( contextIndex != -1)
                {
                    session.connector.directExecute = false;
                    session.connector.openScript(null);
                    session.connector.CONTEXTS_GetProperties(null, String.valueOf(this.contextIndex + 1));
                    session.connector.SESSION_GetSpectrum();
                    session.connector.executeScript();
                    if ( session.connector.result.mError )
                    {
                        this.logMsg +=" ERROR occured script was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
                 //       session.connector.UnLock();
                        return null;
                    }
                    script += session.connector.RawScript+"\n";
                    name = session.connector.getDataByName("prop_value", 0)[0];
                    this.values = session.connector.getDataByName("prop_value", 0);
                    this.spectrum_values = session.connector.getDataByName("value",1);
                    this.getSpectrum = true;
                    this.getproperties = true;
                }
                else
                {
                    logMsg = "ERROR. No Context Selected. Could Not retrieve Context Properties. \n";
             //       session.connector.UnLock();
                    return null;
                }   break;
            case "ShowKnowLedgeGraph":
                synchronized (this.nodesMap)
                {
                    this.nodesMap.clear();
                String maxSize = consolidation;
                session.connector.directExecute = false;
                session.connector.openScript(null);
                session.connector.SESSION_StringToContext(accessors, "false");
                session.connector.CONTEXTS_Fetch(maxSize,"1", "1");
                session.connector.newCommand();
                session.connector.push("CONTEXTS.NewFromConnections");
                session.connector.push("true");
                session.connector.addFunction();
                session.connector.CONTEXTS_Fetch(maxSize, "1", "1");
                session.connector.CONTEXTS_Drop("2"); // nombre de contextes sur la pile en fin d'execution du script
                session.connector.executeScript();
                if ( session.connector.result.mError )
                {
                    this.logMsg +=" ERROR occured script was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
              //      session.connector.UnLock();
                    return null;
                }
                script += session.connector.RawScript+"\n";
                // on recupere les shapes du context
                this.shapes = session.connector.getDataByName("shape", 3);
                this.roots = new ArrayList(Arrays.asList(session.connector.getDataByName("shape", 1)));
                
                if ( this.shapes == null || this.shapes.length == 0 )
                {
             //       session.connector.UnLock();
                    return null;
                }
                if ( session.connector.result.mError )
                {
                    this.logMsg +=" ERROR occured script was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
                 //   session.connector.UnLock();
                    return null;
                }
                String[] secondShapes = null;
                // pour chaque shape on recommence le newfromconnections
                ArrayList<String> l = null;
                
                int max_depth = Integer.parseInt(range);
                int depth = 0;
                
                while (depth ++ < max_depth)
                {
                    for (String s : shapes)
                    {
                        if ( !this.nodesMap.containsKey(s) )
                        {
                            l = new ArrayList<>();
                            this.nodesMap.put(s, l );
                        }
                        else
                        {
                            l = this.nodesMap.get(s);
                        }
                        session.connector.directExecute = false;
                        session.connector.openScript(null);
                        session.connector.SESSION_StringToContext(s, "false");
                        session.connector.newCommand();
                        session.connector.push("CONTEXTS.NewFromConnections");
                        session.connector.push("true");
                        session.connector.addFunction();
                        session.connector.CONTEXTS_Fetch(maxSize, "1", "1");
                        session.connector.CONTEXTS_Drop("2"); // nombre de contextes sur la pile en fin d'execution du script
                        session.connector.executeScript();
                        if ( session.connector.result.mError )
                        {
                            this.logMsg +=" ERROR occured script was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
                         //   session.connector.UnLock();
                            return null;
                        }
                        script += session.connector.RawScript+"\n";
                        secondShapes = session.connector.getDataByName("shape", 2);
                        if ( secondShapes == null || secondShapes.length == 0 )
                        {
                            depth = max_depth + 1;
                            break;
                        }
                        for (String s2 : secondShapes)
                        {
                        if ( !l.contains(s2))
                        {
                            l.add(s2);
                        }
                    }
                } // shapes
                // on passe au niveau suivant
                shapes = secondShapes; 
            }
            // on a termine on traite le resultat
         //   session.connector.UnLock();
         }
            return null;
        }
        
        if (this.setSpectrum)
        {
             session.connector.directExecute = true;
            session.connector.SESSION_SetSpectrum(accessors);
            if ( session.connector.result.mError )
            {
                this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
             //   session.connector.UnLock();
                return null;
            }
          script += session.connector.RawScript+"\n";
        }
        if ( this.getSpectrum )
        {
            session.connector.directExecute = true;
            session.connector.SESSION_GetSpectrum();
            if ( session.connector.result.mError )
            {
                this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
            //    session.connector.UnLock();
                return null;
            }
            script += session.connector.RawScript+"\n";
            this.spectrum_values = session.connector.getDataByName("value",0);
        }
        
        if ( setproperties && this.contextIndex != -1 )
        {
            session.connector.directExecute = true;
            session.connector.CONTEXTS_SetProperties(String.valueOf(this.contextIndex + 1), new String[]{accessors} );
            if ( session.connector.result.mError )
            {
                this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
             //   session.connector.UnLock();
                return null;
            }
            script += session.connector.RawScript+"\n";
        }
        if ( getproperties && this.contextIndex != -1 )
        {
            session.connector.directExecute = true;
            session.connector.CONTEXTS_GetProperties(null, String.valueOf(this.contextIndex + 1) );
            if ( session.connector.result.mError )
            {
                this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
            //    session.connector.UnLock();
            }
            else
            {
                name = session.connector.getDataByName("prop_value", 0)[0];
                this.values = session.connector.getDataByName("prop_value", 0);
            }
            script += session.connector.RawScript+"\n";

        }
// maj de la pile
        int count  = -1;
        session.connector.directExecute = true;
        session.connector.openScript(null);
        session.connector.SESSION_GetProperties("context_count");
        if ( session.connector.result.mError )
        {
            this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
        //    session.connector.UnLock();
            return null;
        }
        script += session.connector.RawScript+"\n";
        // on recupere le nombre de contexts sur la pile 
        String[] r = session.connector.getDataByName("prop_value", -1);
        count = Integer.parseInt( r[0] );
        if ( count == 0 )
        {
            logMsg += "No Contexts on the Stack. \n";
            session.contextsStack.stack.clear();
        }
        else
        {

            session.connector.directExecute = false;
            session.connector.openScript(null);
            for (int i = 0; i < count;i++)
            {
                session.connector.CONTEXTS_GetProperties(null, String.valueOf(i+1));
            }
            session.connector.executeScript();
            if ( session.connector.result.mError )
            {
                this.logMsg +=" ERROR occured script was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
         //       session.connector.UnLock();
                return null;
            }
            script += session.connector.RawScript+"\n";
            if ( Context.firstInit )
            {
                Context.properties = session.connector.getDataByName("prop_name", 0);
                Context.types = session.connector.getDataByName("prop_type", 0);
                Context.prop_access = session.connector.getDataByName("prop_access", 0);
                Context.firstInit = false;
            }
            session.contextsStack.stack.clear();
            toName.clear();
            Context ctx;
            int prop_line = 0;
            for (int i = 0; i < count;i++)
            {
                ctx = new Context();
                ctx.values = session.connector.getDataByName("prop_value", prop_line++);
                ctx.name = ctx.values[0];
                if ( ctx.name.isEmpty() )
                {
                    ctx.name = "#"+i+": "+ctx.values[1]+" elements" ; //GetAContextName();
                }
                else
                {
                    ctx.name += ": "+ctx.values[1]+" elements"  ;
                }
                session.contextsStack.stack.add(ctx);
            }
        }
        if ( fetchContent && count > 0 )
        {

            if ( this.contextIndex != -1 )
            {
                session.connector.directExecute = true;
                session.connector.openScript(null);
                session.connector.CONTEXTS_Fetch("100000", "1", String.valueOf(this.contextIndex + 1) );
                if ( session.connector.result.mError )
                {
                    this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
                //    session.connector.UnLock();
                    return null;
                }
                script += session.connector.RawScript+"\n";
                shapes = session.connector.getDataByName("shape", 0);
                activities = session.connector.getDataByName("activity", 0);
                generalities = session.connector.getDataByName("generality", 0);
                gen_class = session.connector.getDataByName("gen_class", 0);
                id = session.connector.getDataByName("id", 0);

            }
            else
            {
                this.logMsg += "ERROR. No Context selected. Could Not Retrieve Context Content. \n";
            }
            
        }
        // on maj les properties de la session
        
        session.connector.directExecute = true;
        session.connector.openScript(null);
        session.connector.SESSION_GetProperties("");
        if ( session.connector.result.mError )
        {
            this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
        //    session.connector.UnLock();
            return null;
        }
        script += session.connector.RawScript+"\n";
        // on recupere le nombre de contexts sur la pile 
        session.values = session.connector.getDataByName("prop_value", -1);
        
        }
        catch(Exception e)
        {
            this._frame.Updatelog(e.getMessage()+"\n");
        }
      //  session.connector.UnLock();
        return null;
    }
    
    @Override
    protected void done() 
    {
       
        try {
            Void result = get();
            if (_frame == null )
            {
                return;
            }
            
            //_frame.SetShownContextIndex(contextIndex);
            _frame.updateScriptLog(script);
            if ( Action.equals("ShowKnowLedgeGraph"))
            {
                _frame.updateKnowLedgeGraph(this.nodesMap, this.roots);
                
                return;
            }
            
            _frame.Updatelog(logMsg);
            
//            if ( session.connector.result.mError )
//            {
//                return;
//            }
            if ( fetchAll )
            {
                _frame.updateContextsStack();
                
            }
            if ( getSpectrum )
            {
                _frame.updateSessionSpectrum(this.spectrum_values);
            }

            if ( (contextIndex != -1 && fetchContent) || getproperties  ||setproperties )
            {
                _frame.UpdateSelectedContext(contextIndex,values, shapes, activities,generalities, gen_class,id,name);
            }
            
            if ( this.componentToEnable != null )
            {
                this.componentToEnable.setEnabled(true);
            }
            
            if ( Action.equals("ContextToDoc") )
            {
                _frame.ShowResulSetStack();
            }
            
            if ( Action.equals("ClearSession"))
            {
                _frame.ShowResulSetStack();
            }

            if ( Action.equals("ExecuteScript"))
            {
                _frame.ShowResulSetStack();
            }
            
        this._frame.UpdateCurrentSessionProperties();
        
        
        } catch (InterruptedException ex) {
            Logger.getLogger(ProcessContextActionWorker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(ProcessContextActionWorker.class.getName()).log(Level.SEVERE, null, ex);
        }

        
    }
                
}