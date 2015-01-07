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
public class ServersTreePopUpMenu extends JPopupMenu implements ActionListener
{

   JMenuItem newServerItem = new JMenuItem("Add a new Server") ;
   JMenuItem removeServerItem = new JMenuItem("Remove a Server") ;
   JMenuItem DisconnectServerItem = new JMenuItem("Disconnect a Server") ;
   public MainJFrame _frame;
   
   DefaultMutableTreeNode cutnode;
   DefaultMutableTreeNode pastenode;
   
   public int mouseX;
   public int mouseY;
   
    public ServersTreePopUpMenu() 
    {
        add(newServerItem);
        newServerItem.addActionListener( this );
        add( removeServerItem );
        removeServerItem.addActionListener( this );
        add( DisconnectServerItem );
        DisconnectServerItem.addActionListener( this );
    }
  
public void actionPerformed( ActionEvent e )
{
             // determine which menu item was selected
             JTree tree = (JTree) getInvoker();


             if ( e.getActionCommand().equals(newServerItem.getActionCommand() ) )
             {
                 _frame.AddAnewServer(mouseX,mouseY);
                 return;
             }
             if ( e.getActionCommand().equals(removeServerItem.getActionCommand() ) )
             {
                 _frame.RemoveAServer(mouseX,mouseY);
                 return;
             }
             if ( e.getActionCommand().equals(DisconnectServerItem.getActionCommand() ) )
             {
                 _frame.DisconnectAServer(mouseX,mouseY);
                 return;
             } 
               
}
              
}
