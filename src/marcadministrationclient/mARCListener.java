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
import mARC.Connector.Connector;
import static marcadministrationclient.Server.sessionsNamesStock;

/**
 *
 * @author patrice
 */
public class mARCListener implements ActionListener
{
    public MainJFrame frame;
    
    public Server server;
    
    public String logMsg;
    
    public mARCListener()
    {
        this.logMsg = "";
    }
    @Override
    public void actionPerformed(ActionEvent e) 
    {
        javax.swing.Timer t = (javax.swing.Timer) e.getSource();
        if ( !t.isRunning() )
        {
            return;
        }
        if ( frame.updateServerWorker == null || !frame.updateServerWorker.isDone() )
        {
            return;
        }
        // on stoppe le timer pour permettre au worker de terminer tranquillos
        //
        t.stop();
        if ( frame.CurrentServer == null )
        {
            return;
        }



        frame.updateServerWorker = new UpdateServerWorker(frame.CurrentServer);
        frame.updateServerWorker.frame = frame;
        frame.updateServerWorker.execute();
        

    }
    
}
