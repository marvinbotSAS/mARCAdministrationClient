/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
