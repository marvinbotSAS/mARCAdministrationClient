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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
/**
 *
 * @author patrice
 */
public class ProcessResultsActionWorker extends SwingWorker<Void,Void>
{
   static int maxRSIndex = 0;
    
   static ArrayDeque<String> NamesStock = new ArrayDeque<>();
    
    public boolean fetchNext;
    public Session session;
    private ResultSet RS;
    MainJFrame _frame;
    boolean fetchContent;
    boolean fetchAll;
    int ResultSetIndex;
    
    boolean fetchTopMostContent;
    
    private final ArrayList<Integer> toName;
    
    String range;
    String consolidation;
    
    String script;
    
    String[] accessors;
    
    int[] indices;
    
    String operator;
    String tableName;
    String op1,op2,mode;
    String colname;
    
    String name;

    
    String Action;
    String logMsg;
    ProcessResultsActionWorker()
    {
        script = "";
        this.fetchNext = false;
        this.toName = new ArrayList<>();
        this.RS = null;
        this.fetchTopMostContent = false;
        ResultSetIndex = -1;
        this.fetchContent = false;
        this.fetchAll = false;
        op1 = "null";
        op2 = "null";
        Action = "none";
        this.logMsg = "";
        _frame = null;
    }
    
        static public String GetARSName()
    {
        if ( NamesStock.isEmpty() )
        {
            for (int i = 0; i < 10; i++ )
            {
                NamesStock.add(new String("ResultSet#"+String.valueOf(maxRSIndex++) ) );
            }

        }
       return NamesStock.removeFirst();      
    }
    
    static public void ReleaseARSName(String name)
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
            fetchAll = true;
            fetchContent = true;
            this.ResultSetIndex = 0;
        }

        
        if ( fetchContent ==true && this.ResultSetIndex == -1 )
        {
            logMsg += "Fetching a ResultSet required but no Result Set index set. Aborting. \n";
            return null;
        }
           
      //  session.connector.Lock();
        
        
        try
        {
       switch (Action) {
           case "ShowOneResultSetProperties":
               // on recupere le content d'un context
               if ( ResultSetIndex == -1)
               {
                   logMsg = "No Context Selected. Aborting. \n";
                   return null;
               }  break;
           case "New":
               session.connector.directExecute = true;
               session.connector.openScript(null);
               session.connector.RESULTS_New();
               if ( session.connector.result.mError )
               {
                   this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
            //       session.connector.UnLock();
                   return null;
            }  
               script += session.connector.RawScript+"\n";
               break;
           case "Drop":
               session.connector.directExecute = false;
               session.connector.openScript(null);
               for (int index : indices )
               {
                   session.connector.RESULTS_OnTop(String.valueOf(index + 1));
               }
               session.connector.RESULTS_Drop(String.valueOf(indices.length) );
               session.connector.executeScript();
               if ( session.connector.result.mError )
               {
                   this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
                //   session.connector.UnLock();
                   return null;
               }  
               script += session.connector.RawScript+"\n";
               break;
           case "Dup":
               
               session.connector.directExecute = false;
               session.connector.openScript(null);
               int increment = 0;
               for (int i : indices)
               {
                   i += increment++;
                   session.connector.RESULTS_OnTop(String.valueOf(i+1));
                   session.connector.RESULTS_Dup();
               }
               session.connector.executeScript();
               if ( session.connector.result.mError )
               {
                   this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
               //    session.connector.UnLock();
                   return null;            
               }  
               script += session.connector.RawScript+"\n";
               break;
           case "Swap":
               session.connector.directExecute = false;
               session.connector.openScript(null);
               for (int index : indices )
               {
                   session.connector.RESULTS_OnTop(String.valueOf(index + 1));
               }
               session.connector.RESULTS_Swap();
               if ( session.connector.result.mError )
               {
                   this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
                //   session.connector.UnLock();
                   return null;
               }  
               script += session.connector.RawScript+"\n";
               break;
           case "onTop":
               session.connector.directExecute = false;
               session.connector.openScript(null);
               for (int index : indices )
               {
                   session.connector.RESULTS_OnTop(String.valueOf(index + 1));
               }  
               session.connector.executeScript();
               if ( session.connector.result.mError )
               {
                   this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
             //      session.connector.UnLock();
                return null;           
               }  
               this.ResultSetIndex = 0;
               script += session.connector.RawScript+"\n";
               break;
           case "Intersection":
               session.connector.directExecute = false;
               session.connector.openScript(null);
               for (int index : indices )
               {
                   session.connector.RESULTS_OnTop(String.valueOf(index + 1));
               }
               for (int i = 0; i < indices.length - 1; i++)
               {
                   session.connector.RESULTS_Intersection();
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
           case "Union":
               session.connector.directExecute = false;
               session.connector.openScript(null);
               for (int index : indices )
               {
                   session.connector.RESULTS_OnTop(String.valueOf(index + 1));
               }
               for (int i = 0; i < indices.length - 1; i++)
               {
                   session.connector.RESULTS_Union();
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
           case "SelectBy":
               
               session.connector.directExecute = false;
               session.connector.openScript(null);
               for (int i : indices)
               {
                   session.connector.RESULTS_OnTop(String.valueOf(i+1));
                   session.connector.RESULTS_SelectBy(colname, operator, op1, op2);
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
           case "DeleteBy":
               session.connector.directExecute = false;
               session.connector.openScript(null);
               for (int i : indices)
               {
                   session.connector.RESULTS_OnTop(String.valueOf(i+1));
                   session.connector.RESULTS_DeleteBy(colname, operator, op1, op2);
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
           case "SortBy":
               session.connector.directExecute = false;
               session.connector.openScript(null);
               for (int i : indices)
               {
                   session.connector.RESULTS_OnTop(String.valueOf(i+1));
                   session.connector.RESULTS_SortBy(range, consolidation);
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
           case "UniqueBy":
               session.connector.directExecute = false;
               session.connector.openScript(null);
               for (int i : indices)
               {
                   session.connector.RESULTS_OnTop(String.valueOf(i+1));
                   session.connector.RESULTS_UniqueBy(colname);
               }
               session.connector.executeScript();

               if ( session.connector.result.mError )
               {
                   this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
          //         session.connector.UnLock();
                   return null;
               }  
               script += session.connector.RawScript+"\n";
               break;
           case "Normalize":
               session.connector.directExecute = false;
               session.connector.openScript(null);
               for (int i : indices)
               {
                session.connector.RESULTS_OnTop(String.valueOf(i+1));
                session.connector.RESULTS_Normalize(range);
               }
               session.connector.executeScript();

               if ( session.connector.result.mError )
               {
                   this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
        //           session.connector.UnLock();
                   return null;            
               }  
               script += session.connector.RawScript+"\n";
               break;
           case "Amplify":
               session.connector.directExecute = false;
               session.connector.openScript(null);
               for (int i : indices)
               {
                   session.connector.RESULTS_OnTop(String.valueOf(i+1));
                   session.connector.RESULTS_Amplify(range, consolidation);
               }
               session.connector.executeScript();
               if ( session.connector.result.mError )
               {
                   this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
          //         session.connector.UnLock();
                   return null;            
               }  
               script += session.connector.RawScript+"\n";
               break;
           case "SelectFromTable":
               session.connector.directExecute = false;
               session.connector.openScript(null);
               for (int i : indices)
               {
                   session.connector.RESULTS_OnTop(String.valueOf(i+1));
                   session.connector.TABLE_Select(tableName, mode, colname, operator, op1, op2);
               }
               session.connector.executeScript();

               if ( session.connector.result.mError )
               {
                   this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
               //    session.connector.UnLock();
                   return null;
               }  
               script += session.connector.RawScript+"\n";
               break;
           case "SelectToTable":
               session.connector.directExecute = false;
               session.connector.openScript(null);
               for (int i : indices)
               {
                   session.connector.RESULTS_OnTop(String.valueOf(i+1));
                   session.connector.RESULTS_SelectToTable(colname, range, mode);
               }
               session.connector.executeScript();
               if ( session.connector.result.mError )
               {
                   this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
        //           session.connector.UnLock();
                   return null;
               }  
               script += session.connector.RawScript+"\n";
               break;
           case "ResultsSetProperties":
               if ( this.ResultSetIndex == -1 )
               {
                   this.logMsg +="No ResulSet set for required 'SetProperties' command. Aborting. \n";
            //       session.connector.UnLock();
                   return null;
               }  session.connector.directExecute = true;
               session.connector.RESULTS_SetProperties(String.valueOf(this.ResultSetIndex + 1), accessors);
               if ( session.connector.result.mError )
               {
                   this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
           //     session.connector.UnLock();
                return null;            
            }  
               script += session.connector.RawScript+"\n";
               break;
       }
        
       
        session.connector.directExecute = true;
        session.connector.SESSION_GetProperties("result_count");
        if ( session.connector.result.mError )
        {
                this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
          //      session.connector.UnLock();
                return null;
        }
        script += session.connector.RawScript+"\n";
        // on recupere le nombre de contexts sur la pile 
        String[] r = session.connector.getDataByName("prop_value", -1);
        int count = Integer.parseInt(r[0] );
        if ( count == 0 )
        {
            this.logMsg +="No ResulSet on Results Stack. \n";
        }
        else // maj de la pile
        {
            session.connector.directExecute = false;
            session.connector.openScript(null);
            for (int i = 0; i < count;i++)
            {
                session.connector.RESULTS_GetProperties(null, String.valueOf(i+1));
            }
            session.connector.executeScript();
            if ( session.connector.result.mError )
            {
                this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
              //  session.connector.UnLock();
                return null;
            }
            script += session.connector.RawScript+"\n";
            session.RSStack.stack.clear();
            toName.clear();
            ResultSet rs;
            for (int i = 0; i < count;i++)
            {
                rs = new ResultSet();
                rs.values = session.connector.getDataByName("prop_value", i);
                rs.name = rs.values[0];
                if ( rs.name.isEmpty())
                {
                    rs.name = "#"+i+": "+rs.values[1]+" elements"; // GetARSName();
                   // toName.add(i);
                }
                else
                {
                    rs.name +=": "+rs.values[1]+" elements"; 
                }
                session.RSStack.stack.add(rs);
            }

            /*
            if ( !toName.isEmpty() )
            {
                session.connector.directExecute = false;
                session.connector.openScript(null);
                for (Integer ii : toName )
                {
                    rs = session.RSStack.stack.get(ii.intValue());
                    session.connector.RESULTS_SetProperties(String.valueOf(ii.intValue() + 1), new String[]{"name = "+rs.name} );
                }
                 session.connector.executeScript();
                 if ( session.connector.result.mError )
                {
                    this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
                //    session.connector.UnLock();
                    return null;
                }
            }
            */
        }

        if ( fetchContent && count > 0 )
        {
            /*
            properties de ResultSet
            name         #0
            count        #1
            owner_table  #2
            format       #3
            fetch_size   #4
            fetch_start  #5
            fetch_id     #6
            */
            try
            {
            RS = session.RSStack.stack.get(this.ResultSetIndex);
            }
            catch(Exception e)
            {
                this.logMsg += "ERROR Could not get ResultSet at index "+this.ResultSetIndex;
            }
            if ( RS == null )
            {
                this.logMsg +="Unable to get the Current ResultSet. Aborting. \n";
             //   session.connector.UnLock();
                return null;
            }
            session.connector.directExecute = true;
            session.connector.openScript(null);
            session.connector.RESULTS_GetProperties(null, String.valueOf(this.ResultSetIndex + 1) );
            if ( session.connector.result.mError )
            {
                    this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
              //      session.connector.UnLock();
                    return null;
            }
            script += session.connector.RawScript+"\n";
            // on recupere les donnees de fetch
            String[] properties_RS = session.connector.getDataByName("prop_value", 0);
            if (ResultSet.firstInit )
            {
                ResultSet.properties = session.connector.getDataByName("prop_name", 0);
                ResultSet.types = session.connector.getDataByName("prop_type", 0);
                ResultSet.access = session.connector.getDataByName("prop_access", 0);
                ResultSet.firstInit = false;
            }
            RS.format = null;
            RS.values = session.connector.getDataByName("prop_value", 0);
            RS.format = RS.values[3].split(" "); // le format
            // on fetch
            session.connector.openScript(null);
            if ( !fetchNext)
            {
                session.connector.RESULTS_Fetch(properties_RS[4], "1", String.valueOf(this.ResultSetIndex + 1) );
            }
            else
            {
                if ( properties_RS[5].equals("0") || Integer.parseInt(properties_RS[5]) > Integer.parseInt(properties_RS[1]) )
                {
                    this.logMsg +="Fetch end reached. Cannot go further \n";
              //      session.connector.UnLock();
                    return null;
                }
                session.connector.RESULTS_Fetch(null, null, String.valueOf(this.ResultSetIndex + 1) );
            }
            if ( session.connector.result.mError )
            {
                    this.logMsg +=" ERROR occured command was '"+session.connector.getToSend()+"'  server answer is '"+session.connector.result.mErrorMessage+"' \n";
               //     session.connector.UnLock();
                    return null;
            }
            script += session.connector.RawScript+"\n";
            RS.cols = null;
            RS.cols = new String[RS.format.length ][ Integer.parseInt( RS.values[4] ) ];
            int i = 0;
            for (String s : RS.format)
            {
                RS.cols[i++] = session.connector.getDataByName(s, 0);
            }
        }
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
     //   session.connector.UnLock();
        return null;
    }
    
    @Override
    protected void done()  
    {
       try {
           Void result = get();
           if ( _frame != null )
           {
               _frame.Updatelog(logMsg);
               _frame.updateScriptLog(script);
           }
           
           if ( session.connector.result.mError)
           {
               return;
           }
           if ( fetchAll  && _frame != null )
           {
               _frame.updateResultsStack();
           }
           
           // _frame.SetShownContextIndex(this.ResultSetIndex);
           if ( RS != null && fetchContent && ResultSetIndex != -1 && _frame != null )
           {
               _frame.UpdateResultSetContent(RS, this.ResultSetIndex);
           }
       } catch (InterruptedException ex) {
           Logger.getLogger(ProcessResultsActionWorker.class.getName()).log(Level.SEVERE, null, ex);
       } catch (ExecutionException ex) {
           Logger.getLogger(ProcessResultsActionWorker.class.getName()).log(Level.SEVERE, null, ex);
       }

    }
                
}