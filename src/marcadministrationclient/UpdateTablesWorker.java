/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marcadministrationclient;
import javax.swing.SwingWorker;
/**
 *
 * @author patrice
 */
public class UpdateTablesWorker extends SwingWorker<Void,Void>
{
    public Server server;

    public MainJFrame _frame;
    UpdateTablesWorker()
    {
        
    }
    @Override
    protected Void doInBackground()
    {
      //  server.connector.Lock();
        server.UpdateTablesStats();
     //   server.connector.UnLock();
        return null;
    }
    
    @Override
    protected void done() 
    {
        
    }
                
}