/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marcadministrationclient;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.Timer;

/**
 *
 * @author patrice
 */
public class UpdateKnowledgeGraphActionListener implements ActionListener
{
    public MainJFrame frame;
    
    public Timer timer;
    
    UpdateKnowledgeGraphActionListener()
    {

    }
    @Override
    public void actionPerformed(ActionEvent e) 
    {
        if ( !timer.isRunning() ||frame == null )
        {
            return;
        }
        if ( frame.CurrentSession == null)
        {
            return;
        }
        ProcessContextActionWorker w = new ProcessContextActionWorker();
        w._frame = frame;
        w.range = frame.maxDepthjTextField.getText();
        w.consolidation = frame.maxSizejTextField.getText();
        w.Action = "ShowKnowLedgeGraph";
        w.accessors = frame.jTextField8.getText();
        w.session = frame.CurrentSession;
        w.execute();
    }
}
