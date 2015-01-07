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
public class TableKillTreePopUpMenu extends JPopupMenu implements ActionListener
{

   JMenuItem removeItem = new JMenuItem("Kill") ;

   public MainJFrame _frame;
   
   DefaultMutableTreeNode cutnode;
   DefaultMutableTreeNode pastenode;
   
   public int mouseX;
   public int mouseY;
   
    public TableKillTreePopUpMenu() 
    {
        add( removeItem );
        removeItem.addActionListener( this );
        
    }
  
public void actionPerformed( ActionEvent e )
{
             // determine which menu item was selected
             JTree tree = (JTree) getInvoker();
              TreePath theSelectedPath = tree.getPathForLocation(mouseX, mouseY);
             if ( e.getActionCommand().equals(removeItem.getActionCommand() ) )
             {
                 _frame.KillATable(theSelectedPath);
                 return;
             }
 

               
}
              
}
