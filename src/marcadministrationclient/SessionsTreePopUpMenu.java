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
public class SessionsTreePopUpMenu extends JPopupMenu implements ActionListener
{

   JMenuItem newItem = new JMenuItem("Add A Session") ;
   JMenuItem removeItem = new JMenuItem("Remove Session") ;
   public MainJFrame _frame;
   
   DefaultMutableTreeNode cutnode;
   DefaultMutableTreeNode pastenode;
   
   public int mouseX;
   public int mouseY;
   
    public SessionsTreePopUpMenu() 
    {
        add(newItem);
        newItem.addActionListener( this );
        add( removeItem );
        removeItem.addActionListener( this );
        
    }
  
public void actionPerformed( ActionEvent e )
{
             // determine which menu item was selected
             JTree tree = (JTree) getInvoker();
             TreePath theSelectedPath = tree.getPathForLocation(mouseX, mouseY);

             if ( e.getActionCommand().equals(newItem.getActionCommand() ) )
             {
                 _frame.addASession(mouseX,mouseY);
                 return;
             }
             else if ( e.getActionCommand().equals(removeItem.getActionCommand() ) )
             {
                 _frame.removeASession();
             }

               
}
              
}
