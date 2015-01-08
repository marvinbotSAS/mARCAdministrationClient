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
