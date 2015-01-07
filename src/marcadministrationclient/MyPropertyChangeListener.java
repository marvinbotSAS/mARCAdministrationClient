/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marcadministrationclient;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JSplitPane;

/**
 *
 * @author patrice
 */
public class MyPropertyChangeListener implements PropertyChangeListener {
    
    MainJFrame frame;
        @Override
        public void propertyChange(PropertyChangeEvent pce) 
        {
           JSplitPane p = (JSplitPane) pce.getSource();
           if ( frame == null )
           {
               return;
           }
            if ( p.getDividerLocation() <= 30 )
            {
                frame.timerToUpdateServerStats.stop();
            }
            else
            {
                if (!frame.timerToUpdateServerStats.isRunning())
                {
                    frame.timerToUpdateServerStats.start();
                }
            }
        }
}
