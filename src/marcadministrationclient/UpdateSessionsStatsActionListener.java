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
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;

/**
 *
 * @author patrice
 */
public class UpdateSessionsStatsActionListener implements ActionListener
{
    public MainJFrame frame;
    
    public Server server;
    
    UpdateSessionsStatsActionListener()
    {

    }
    @Override
    public void actionPerformed(ActionEvent e) 
    {
        if ( server == null )
        {
            return;
        }
       // server.connector.Lock();
        server.updateSessions();
      //  server.connector.UnLock();
        frame.UpdateCurrentSessionProperties();
        
    }
}
