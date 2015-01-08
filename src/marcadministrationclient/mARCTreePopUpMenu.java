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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.swing.*;

import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.*;

/**
 *
 * @author descourt
 */
public class mARCTreePopUpMenu extends JPopupMenu implements ActionListener
{

   JMenuItem saveItem = new JMenuItem("Save") ;
   JMenuItem reloadItem = new JMenuItem("Reload") ;
   JMenuItem rebuildItem = new JMenuItem("Rebuild") ;
   JMenuItem shutdownItem = new JMenuItem("ShutDown") ;
   JMenuItem publishItem = new JMenuItem("Publish") ;
   public MainJFrame _frame;
   
   DefaultMutableTreeNode cutnode;
   DefaultMutableTreeNode pastenode;
   
   public int mouseX;
   public int mouseY;
   
    public mARCTreePopUpMenu() 
    {
        add(saveItem);
        saveItem.addActionListener( this );
        add( reloadItem );
        reloadItem.addActionListener( this );
        add( rebuildItem );
        rebuildItem.addActionListener( this );        
        add( shutdownItem );
        shutdownItem.addActionListener( this );        
        add( publishItem );
        publishItem.addActionListener( this );
        
    }
  
public void actionPerformed( ActionEvent e )
{
             // determine which menu item was selected

             if ( e.getActionCommand().equals(this.saveItem.getActionCommand() ) )
             {
                 _frame.mARCSave();

             }
             else if ( e.getActionCommand().equals(this.reloadItem.getActionCommand() ) )
             {
                 _frame.mARCReload();
             }
             if ( e.getActionCommand().equals(this.shutdownItem.getActionCommand() ) )
             {
                 _frame.mARCShutDown();

             }
             else if ( e.getActionCommand().equals(this.rebuildItem.getActionCommand() ) )
             {
                 _frame.mARCRebuild();
             }
             else if ( e.getActionCommand().equals(this.publishItem.getActionCommand() ) )
             {
                 _frame.mARCPublish();
             }
               
}
              
}
