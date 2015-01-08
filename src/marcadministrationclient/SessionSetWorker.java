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
public class SessionSetWorker extends javax.swing.SwingWorker<Void,Void>
{
    String Action;
    Connector connector;
    MainJFrame _frame;

    String[] accessors;
    String spectrum;
    
    String[] propertiesInstances;
    String[] properties_values;
    String[] spectrum_values;
    
    //
            @Override
    protected Void doInBackground() throws Exception
    {
        
       //    connector.Lock();

           
           if (Action.equals("SetProperties") )
           {
               connector.directExecute = false;
               connector.openScript(null);
               connector.SESSION_SetProperties(accessors);
               connector.SESSION_GetProperties(null);
               connector.executeScript();
               properties_values = connector.getDataByName("prop_value", 1);
           }
           else if (Action.equals("SetSpectrum"))
           {
               connector.directExecute = true;
               connector.SESSION_SetSpectrum(spectrum);
               connector.SESSION_GetSpectrum();
               this.spectrum_values = connector.getDataByName("value",0);
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
       propertiesInstances[1] = "";
        propertiesInstances[2] = properties_values[0];
        propertiesInstances[3] = "false";
        propertiesInstances[4] = properties_values[2];
        propertiesInstances[5] = properties_values[5];
        propertiesInstances[6] = properties_values[7];
        propertiesInstances[7] = properties_values[6];
        propertiesInstances[8] = "false";
        
        return null;
    }
    
    @Override
    protected void done() 
    {
        try {
            Void result = get();
            if ( Action.equals("SetProperties"))
            {
                _frame.CurrentSession.values = properties_values;
                _frame.UpdateCurrentSessionProperties();
            }
            else if ( Action.equals("SetSpectrum"))
            {
                _frame.updateSessionSpectrum(this.spectrum_values);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(SessionSetWorker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(SessionSetWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
