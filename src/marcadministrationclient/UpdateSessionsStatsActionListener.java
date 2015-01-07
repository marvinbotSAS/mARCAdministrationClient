/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
