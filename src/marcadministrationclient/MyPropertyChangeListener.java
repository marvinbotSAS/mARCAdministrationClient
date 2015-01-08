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
